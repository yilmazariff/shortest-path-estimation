package riivo.shortespath;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.jgrapht.graph.SimpleGraph;

import riivo.shortestpath.graph.BreadthFirstSearchWithDistance;
import riivo.shortestpath.graph.MyEdge;
import riivo.shortestpath.graph.MyShortestPath;
import riivo.shortestpath.graph.MyVertex;
import riivo.shortestpath.graph.BreadthFirstSearchWithDistance.Callable;
import riivo.shortestpath.landmarks.CentralityLandmarkChooser;
import riivo.shortestpath.landmarks.CombinedCentralityChooser;
import riivo.shortestpath.landmarks.DegreeBasedLandmarkChooser;
import riivo.shortestpath.landmarks.LandmarkChooser;
import riivo.shortestpath.landmarks.RandomLandmarkChooser;

public final class GraphDistanceEstimation {

  private static final Logger log = Logger.getLogger(GraphDistanceEstimation.class);

  private String file;
  private int testSetSize;
  private int landmarkCount;

  private int iterations = 10;

  public GraphDistanceEstimation(String file, int landmarkCount, int testSetSize) {
    super();
    this.file = file;
    this.landmarkCount = landmarkCount;
    this.testSetSize = testSetSize;
  }

  /**
   * @param args
   */
  public void runExperiments() {
    log.debug("start!");
    SimpleGraph<MyVertex, MyEdge> graph = GraphReader.read(this.file);
    log.info("Edges:" + graph.edgeSet().size());
    log.info("Vertices:" + graph.vertexSet().size());
    log.debug("graph done");

    LandmarkChooser[] choosers =
    new LandmarkChooser[] { new RandomLandmarkChooser(), new DegreeBasedLandmarkChooser(),
                           new CentralityLandmarkChooser() };
    for (LandmarkChooser landmarkChooser : choosers) {
      log.debug("starting: " + landmarkChooser.getName());
      clean(graph);
      index(graph, new CombinedCentralityChooser());
      log.debug("indexing done");
      double averageError = evaluate(graph);
      log.debug(landmarkChooser.getName() + ";" + averageError);
      log.debug(" done!");
    }

    log.debug("all done!");
  }

  private void clean(SimpleGraph<MyVertex, MyEdge> graph) {
    Set<MyVertex> vertexSet = graph.vertexSet();
    for (MyVertex myVertex : vertexSet) {
      myVertex.getLandMarkDistances().clear();
    }
  }

  private double evaluate(SimpleGraph<MyVertex, MyEdge> graph) {
    DescriptiveStatistics all = new DescriptiveStatistics();
    for (int i = 0; i < iterations; i++) {
      DescriptiveStatistics stats = new DescriptiveStatistics();
      HashSet<MyVertex> testSet = pickRanomVertices(graph, testSetSize);
      for (MyVertex from : testSet) {
        for (MyVertex to : testSet) {
          if (to.getId() > from.getId()) {
            double realLength = MyShortestPath.distance(graph, from, to);
            double estimated = estimate(from, to);
            double error = Math.abs(estimated - realLength) / realLength;
            stats.addValue(error);
          }
        }
      }
      all.addValue(stats.getMean());
    }
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

  private void index(SimpleGraph<MyVertex, MyEdge> graph, LandmarkChooser chooser) {
    HashSet<MyVertex> landmarks = chooser.choose(graph, landmarkCount);
    for (MyVertex myVertex : landmarks) {
      BreadthFirstSearchWithDistance.bfs(graph, new Callable() {

        @Override
        public void call(SimpleGraph<MyVertex, MyEdge> graph, MyVertex start, MyVertex next, int level) {
          next.getLandMarkDistances().put(start, level);
        }
      }, myVertex);
    }
  }

}
