package riivo.shortespath;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.jgrapht.graph.SimpleGraph;

import riivo.shortespath.util.ThreadPool;
import riivo.shortestpath.graph.BreadthFirstSearchWithDistance;
import riivo.shortestpath.graph.MyEdge;
import riivo.shortestpath.graph.MyVertex;
import riivo.shortestpath.graph.BreadthFirstSearchWithDistance.Callable;
import riivo.shortestpath.landmarks.CentralityLandmarkChooser;
import riivo.shortestpath.landmarks.ConstrainedCentralityChooser;
import riivo.shortestpath.landmarks.ConstrainedDegreeChooser;
import riivo.shortestpath.landmarks.DegreeBasedLandmarkChooser;
import riivo.shortestpath.landmarks.LandmarkChooser;
import riivo.shortestpath.landmarks.RandomLandmarkChooser;

public final class GraphDistanceEstimation {

  public static final int TP_SIZE = 3;

  private static final Logger log = Logger.getLogger(GraphDistanceEstimation.class);

  private String file;
  private int testSetSize;
  private int landmarkCount;

  private int iterations;

  public GraphDistanceEstimation(String file, int landmarkCount, int testSetSize, int iterations) {
    super();
    this.file = file;
    this.landmarkCount = landmarkCount;
    this.testSetSize = testSetSize;
    this.iterations = iterations;
  }

  /**
   * @param args
   */
  public void runExperiments() {
    log.debug("file: " + file);
    log.debug("landmarks: " + landmarkCount);
    log.debug("testSetSize: " + testSetSize);
    log.debug("iteration: " + iterations);

    SimpleGraph<MyVertex, MyEdge> graph = GraphReader.read(this.file);
    log.info("Edges:" + graph.edgeSet().size());
    log.info("Vertices:" + graph.vertexSet().size());
    log.info("Landmarks:" + landmarkCount);
    log.info("Test vertex set size:" + testSetSize);
    log.debug("graph done");

    final LandmarkChooser[] choosers =
    new LandmarkChooser[] { new ConstrainedDegreeChooser(), new ConstrainedCentralityChooser(),
                           new RandomLandmarkChooser(), new DegreeBasedLandmarkChooser(),
                           new CentralityLandmarkChooser() };
    for (LandmarkChooser landmarkChooser : choosers) {
      log.debug("starting: " + landmarkChooser.getName());
      clean(graph);
      index(graph, landmarkChooser);
      log.debug("indexing done");
      double averageError = evaluate(graph);
      log.debug("#####" + landmarkChooser.getName() + ";" + averageError);
      log.debug(" done!" + landmarkChooser.getName() + "\n");
    }

    log.debug("all done!");
  }

  private void clean(SimpleGraph<MyVertex, MyEdge> graph) {
    log.debug("\t-cleaning graph");
    Set<MyVertex> vertexSet = graph.vertexSet();
    for (MyVertex myVertex : vertexSet) {
      myVertex.getLandMarkDistances().clear();
    }
    log.debug("\t-cleaning done");
  }

  private double evaluate(final SimpleGraph<MyVertex, MyEdge> graph) {
    final DescriptiveStatistics all = new DescriptiveStatistics();
    final DescriptiveStatistics estimation = new DescriptiveStatistics();
    final DescriptiveStatistics validation = new DescriptiveStatistics();
    for (int i = 0; i < iterations; i++) {
      log.debug("\t start new evaluation iteration");
      final DescriptiveStatistics stats = new DescriptiveStatistics();
      HashSet<MyVertex> testSet = pickRanomVertices(graph, testSetSize);
      ThreadPool tp = new ThreadPool(TP_SIZE);
      for (final MyVertex from : testSet) {
        for (final MyVertex to : testSet) {
          if (to.getId() > from.getId()) {
            tp.runTask(new Runnable() {

              @Override
              public void run() {
                long valStart = System.currentTimeMillis();
                double realLength = BreadthFirstSearchWithDistance.distance(graph, from, to);
                long timeTakenReal = System.currentTimeMillis() - valStart;

                long estimationStart = System.currentTimeMillis();
                double estimated = estimate(from, to);
                long timeTakenEstimation = System.currentTimeMillis() - estimationStart;

                double error = Math.abs(estimated - realLength) / realLength;
                synchronized (stats) {
                  estimation.addValue(timeTakenEstimation);
                  validation.addValue(timeTakenReal);
                  stats.addValue(error);
                }
              }

            });

          }
        }
      }
      tp.join();
      all.addValue(stats.getMean());
    }
    log.debug("\t\t average, validation:" + validation.getMean() + ", estimation:" + estimation.getMean());
    return all.getMean();
  }

  private HashSet<MyVertex> pickRanomVertices(SimpleGraph<MyVertex, MyEdge> graph, int tESTSETSIZE) {
    return new RandomLandmarkChooser().choose(graph, tESTSETSIZE);
  }

  private double estimate(MyVertex from, MyVertex to) {
    Set<MyVertex> keySet = from.getLandMarkDistances().keySet();
    Set<MyVertex> keySet2 = to.getLandMarkDistances().keySet();
    if (keySet.size() != landmarkCount || keySet2.size() != landmarkCount) {
      throw new RuntimeException("wrong");
    }
    double max = Double.MAX_VALUE;

    final Set<MyVertex> all = new HashSet<MyVertex>();
    for (MyVertex myVertex : keySet2) {
      all.add(myVertex);
    }
    for (MyVertex myVertex : keySet) {
      all.add(myVertex);
    }

    for (MyVertex landMark : all) {
      Integer t = to.getLandMarkDistances().get(landMark);
      Integer s = from.getLandMarkDistances().get(landMark);
      if (t == null || s == null) {
        continue;
      }
      if (t.intValue() + s.intValue() < max) {
        max = t.intValue() + s.intValue();
      }
    }
    return max;
  }

  private void index(final SimpleGraph<MyVertex, MyEdge> graph, LandmarkChooser chooser) {
    HashSet<MyVertex> landmarks = chooser.choose(graph, landmarkCount);
    int i = 0;
    ThreadPool tp = new ThreadPool(TP_SIZE);
    for (final MyVertex myVertex : landmarks) {

      i++;
      final Integer j = new Integer(i);
      Runnable r = new Runnable() {

        @Override
        public void run() {
          log.debug("running " + j);
          BreadthFirstSearchWithDistance.bfs(graph, new Callable() {

            @Override
            public void call(SimpleGraph<MyVertex, MyEdge> graph, MyVertex start, MyVertex next, int level) {
              // next.getLandMarkDistances().put(start, level);
              next.put(start, level);
            }
          }, myVertex);
          // double percent = 1. * j / landmarkCount * 100.0;
          log.debug("\tdone " + j + " of indexing");
        }
      };

      tp.runTask(r);
    }
    tp.join();

  }
}
