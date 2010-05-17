package riivo.shortespath;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.jgrapht.graph.SimpleGraph;

import riivo.shortestpath.graph.MyEdge;
import riivo.shortestpath.graph.MyVertex;
import riivo.shortestpath.landmarks.DegreeBasedLandmarkChooser;
import riivo.shortestpath.landmarks.LandmarkChooser;

public final class GraphDistanceTest {

  private static final long RANDOM_SEED = 13371337l;
  static int LANDMARKS = 20;
  static int TEST_SET_SIZE = 10;

  private static final Logger log = Logger.getLogger(GraphDistanceTest.class);

  /**
   * @param args
   */
  public static void main(String[] args) {
    log.debug("start!");
    SimpleGraph<MyVertex, MyEdge> graph = GraphReader.run();
    log.debug("graph done");
    index(graph, new DegreeBasedLandmarkChooser());
    log.debug("indexing done");
    log.info("Edges:" + graph.edgeSet().size());
    log.info("Vertices:" + graph.vertexSet().size());
    info(graph);

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
        double realLength = MyShortestPath.distance(graph, from, to);
        double estimated = estimate(from, to);
        double error = Math.abs(estimated - realLength) / realLength;
        stats.addValue(error);

      }
    }
    System.out.println(stats.getValues());
    log.debug("mean error " + stats.getMean());
  }

  private static double estimate(MyVertex from, MyVertex to) {
    Set<MyVertex> keySet = from.getLandMarkDistances().keySet();
    Set<MyVertex> keySet2 = to.getLandMarkDistances().keySet();
    if (keySet.size() != LANDMARKS || keySet2.size() != LANDMARKS) {
      log.error("---------------------------------");
      log.error(from + ": " + keySet);
      log.error(to + ": " + keySet2);
      log.error("---------------------------------");
      // throw new RuntimeException("wrong");
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
    log.debug(landmarks);
    log.debug(landmarks.size());
    for (MyVertex myVertex : landmarks) {
      System.out.println("going:" + myVertex);
      bfs(graph, myVertex);
    }
  }

  public static void bfs(SimpleGraph<MyVertex, MyEdge> graph, MyVertex start) {
    Queue<MyVertex> queue = new LinkedList<MyVertex>();
    Set<MyVertex> visited = new HashSet<MyVertex>();
    queue.add(start);
    breadth(graph, start, queue, visited, 0);
    System.out.println("visited:" + visited.size());
  }

  private static void breadth(final SimpleGraph<MyVertex, MyEdge> graph,
                              final MyVertex start,
                              final Queue<MyVertex> queue,
                              final Set<MyVertex> visited,
                              int level) {

    MyVertex next = queue.poll();
    Queue<MyVertex> nextLevel = new LinkedList<MyVertex>();
    while (next != null) {
      if (!visited.contains(next)) {
        visited.add(next);
        if (next.getId() == 5705) {
          System.out.println(next.getLandMarkDistances().size() + "---------------");
          System.out.println("start:" + start);
          System.out.println("next:" + next);
          System.out.println("count:" + next.cnt);
          System.out.println("map:" + next.getLandMarkDistances());

          System.out.println(level);
        }
        next.getLandMarkDistances().put(start, level);

        next.cnt += 1;
        if (next.getId() == 5705) {
          System.out.println(next.getLandMarkDistances().size() + "XXXXXX");
          System.out.println("\n\n\n");
        }

        Set<MyEdge> edgesOf = graph.edgesOf(next);
        for (MyEdge myEdge : edgesOf) {
          MyVertex target =
          graph.getEdgeSource(myEdge).equals(next) ? graph.getEdgeTarget(myEdge) : graph.getEdgeSource(myEdge);
          nextLevel.add(target);
        }

      }
      next = queue.poll();
    }
    if (!nextLevel.isEmpty())
      breadth(graph, start, nextLevel, visited, level + 1);
  }

  public static HashSet<MyVertex> pickRanomVertices(SimpleGraph<MyVertex, MyEdge> graph, int lANDMARKS2) {
    final Set<MyVertex> vertexSet = graph.vertexSet();
    // find more determinisic solution, if O(landmarks)
    HashSet<MyVertex> landmarks = new HashSet<MyVertex>();

    Random random = new Random(RANDOM_SEED);
    while (landmarks.size() < lANDMARKS2) {
      int size = vertexSet.size();
      int item = random.nextInt(size);
      int i = 0;
      inner: for (MyVertex selected : vertexSet) {
        if (i == item) {
          landmarks.add(selected);
          break inner;
        }
        i = i + 1;
      }
    }
    return landmarks;

  }

}
