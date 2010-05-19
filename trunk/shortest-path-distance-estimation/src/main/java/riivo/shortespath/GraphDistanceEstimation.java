package riivo.shortespath;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.jgrapht.graph.SimpleGraph;

import riivo.shortestpath.graph.BreadthFirstSearchWithDistance;
import riivo.shortestpath.graph.MyEdge;
import riivo.shortestpath.graph.MyVertex;
import riivo.shortestpath.graph.BreadthFirstSearchWithDistance.Callable;
import riivo.shortestpath.landmarks.CentralityLandmarkChooser;
import riivo.shortestpath.landmarks.LandmarkChooser;
import riivo.shortestpath.landmarks.RandomLandmarkChooser;

public final class GraphDistanceEstimation {

  static int LANDMARKS = 10;
  static int TEST_SET_SIZE = 10;

  private static final Logger log = Logger.getLogger(GraphDistanceEstimation.class);

  /**
   * @param args
   */
  public static void main(String[] args) {
    log.debug("start!");
    SimpleGraph<MyVertex, MyEdge> graph = GraphReader.run();
    log.debug("graph done");
    // index(graph, new RandomLandmarkChooser());
    // index(graph, new DegreeBasedLandmarkChooser());
    index(graph, new CentralityLandmarkChooser());
    log.debug("indexing done");
    log.info("Edges:" + graph.edgeSet().size());
    log.info("Vertices:" + graph.vertexSet().size());

    evaluate(graph);
    // jclean(graph);
    // index(graph, new CentralityLandMarkChooser());
    // evaluate(graph);
    log.debug("all done!");
  }

  private static void info(SimpleGraph<MyVertex, MyEdge> graph) {

    DescriptiveStatistics stats = new DescriptiveStatistics();
    for (MyVertex from : graph.vertexSet()) {
      stats.addValue(from.getLandMarkDistances().size());

    }
    log.debug("mean size " + stats.getMean());
  }

  private static void evaluate(SimpleGraph<MyVertex, MyEdge> graph) {

    DescriptiveStatistics stats = new DescriptiveStatistics();
    HashSet<MyVertex> testSet = pickRanomVertices(graph, TEST_SET_SIZE);
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
    log.debug("mean error " + stats.getMean());
  }

  private static HashSet<MyVertex> pickRanomVertices(SimpleGraph<MyVertex, MyEdge> graph, int tESTSETSIZE) {
    return new RandomLandmarkChooser().choose(graph, tESTSETSIZE);
  }

  private static double estimate(MyVertex from, MyVertex to) {
    Set<MyVertex> keySet = from.getLandMarkDistances().keySet();
    Set<MyVertex> keySet2 = to.getLandMarkDistances().keySet();
    if (keySet.size() != LANDMARKS || keySet2.size() != LANDMARKS) {
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

  public static void index(SimpleGraph<MyVertex, MyEdge> graph, LandmarkChooser chooser) {
    HashSet<MyVertex> landmarks = chooser.choose(graph, LANDMARKS);
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
