import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.jgrapht.VertexFactory;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.generate.ScaleFreeGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public final class GraphDistanceTest {

  static int vertexCount = 10000;
  static int LANDMARKS = 20;

  public static void main(String[] args) {
    SimpleGraph<MyVertex, MyEdge> graph = getGraph();
    System.out.println("graph done");
    index(graph, new RandomLandmarkChooser());
    System.out.println("indexing done");
    evaluate(graph);

    // jclean(graph);

    // index(graph, new CentralityLandMarkChooser());
    // evaluate(graph);
  }

  private static void clean(SimpleGraph<MyVertex, MyEdge> graph) {
    Set<MyVertex> vertexSet = graph.vertexSet();
    for (MyVertex myVertex : vertexSet) {
      myVertex.isLandmark = false;
      myVertex.getLandMarkDistances().clear();
    }

  }

  private static void evaluate(SimpleGraph<MyVertex, MyEdge> graph) {

    DescriptiveStatistics stats = new DescriptiveStatistics();
    HashSet<MyVertex> testSet = pickLandmarksRandom(graph, (int) (vertexCount * 0.1));
    for (MyVertex from : testSet) {
      for (MyVertex to : testSet) {
        if (from.getId() < to.getId()) {
          DijkstraShortestPath<MyVertex, MyEdge> path = new DijkstraShortestPath<MyVertex, MyEdge>(graph, from, to);
          double realLength = path.getPathLength();
          double estimated = estimate(from, to);
          double error = Math.abs(estimated - realLength) / realLength;
          stats.addValue(error);
        }

      }
    }
    System.out.println("mean error " + stats.getMean());
  }

  private static double estimate(MyVertex from, MyVertex to) {
    Set<MyVertex> keySet = from.getLandMarkDistances().keySet();
    Set<MyVertex> keySet2 = to.getLandMarkDistances().keySet();
    if (keySet.size() != keySet2.size()) {
      System.out.println(keySet);
      System.out.println(keySet2);
      throw new RuntimeException("wrong");
    }
    double max = Double.MAX_VALUE;
    for (Entry<MyVertex, Integer> fromLandMark : from.getLandMarkDistances().entrySet()) {
      Integer t = to.getLandMarkDistances().get(fromLandMark.getKey());
      Integer s = fromLandMark.getValue();
      if (t.intValue() + s.intValue() < max) {
        max = t.intValue() + s.intValue();
      }
    }
    // TODO Auto-generated method stub
    return max;
  }

  public static void index(SimpleGraph<MyVertex, MyEdge> graph, LandmarkChooser chooser) {
    HashSet<MyVertex> landmarks = chooser.choose(graph, LANDMARKS);
    for (MyVertex myVertex : landmarks) {
      bfs(graph, myVertex);
    }
  }

  public static void bfs(SimpleGraph<MyVertex, MyEdge> graph, MyVertex start) {
    Queue<MyVertex> queue = new LinkedList<MyVertex>();
    Set<MyVertex> visited = new HashSet<MyVertex>();
    queue.add(start);
    breadth(graph, start, queue, visited, 0);
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

        createIndexVector(start, next, level);

        Set<MyEdge> edgesOf = graph.edgesOf(next);
        for (MyEdge myEdge : edgesOf) {
          MyVertex target = myEdge.getSource().equals(next) ? myEdge.getTarget() : myEdge.getSource();
          nextLevel.add(target);
        }

      }
      next = queue.poll();
    }
    if (!nextLevel.isEmpty())
      breadth(graph, start, nextLevel, visited, level + 1);
  }

  private static void createIndexVector(MyVertex start, MyVertex next, int level) {
    next.getLandMarkDistances().put(start, level);
  }

  public static HashSet<MyVertex> pickLandmarksRandom(SimpleGraph<MyVertex, MyEdge> graph, int lANDMARKS2) {
    final Set<MyVertex> vertexSet = graph.vertexSet();
    // find more determinisic solution, if O(landmarks)
    HashSet<MyVertex> landmarks = new HashSet<MyVertex>();

    Random random = new Random();
    while (landmarks.size() < lANDMARKS2) {
      int size = vertexSet.size();
      int item = random.nextInt(size);
      int i = 0;
      for (MyVertex selected : vertexSet) {
        if (i == item) {
          landmarks.add(selected);
          selected.setLandmark(true);
        }
        i = i + 1;
      }
    }
    return landmarks;

  }

  public static SimpleGraph<MyVertex, MyEdge> getGraph() {
    SimpleGraph<MyVertex, MyEdge> graph = new SimpleGraph<MyVertex, MyEdge>(MyEdge.class);

    ScaleFreeGraphGenerator<MyVertex, MyEdge> completeGenerator =
    new ScaleFreeGraphGenerator<MyVertex, MyEdge>(vertexCount);

    completeGenerator.generateGraph(graph, new VertexFactory<MyVertex>() {
      int last = 0;

      public MyVertex createVertex() {
        return new MyVertex(last++);
      }
    }, null);
    return graph;
  }

  public static final class MyEdge extends DefaultEdge {

    @Override
    public MyVertex getSource() {
      return (MyVertex) super.getSource();
    }

    @Override
    public MyVertex getTarget() {
      return (MyVertex) super.getTarget();
    }

  }

  private static final class MyVertex {
    private final int id;
    private boolean isLandmark;

    private HashMap<MyVertex, Integer> landMarkDistances = new HashMap<MyVertex, Integer>();

    public MyVertex(int id) {
      this.id = id;
    }

    public int getId() {
      return id;
    }

    public boolean isLandmark() {
      return isLandmark;
    }

    public HashMap<MyVertex, Integer> getLandMarkDistances() {
      return landMarkDistances;
    }

    public void setLandMarkDistances(HashMap<MyVertex, Integer> landMarkDistances) {
      this.landMarkDistances = landMarkDistances;
    }

    public void setLandmark(boolean isLandmark) {
      this.isLandmark = isLandmark;
    }

    @Override
    public String toString() {
      return " " + id;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + id;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      MyVertex other = (MyVertex) obj;
      if (id != other.id)
        return false;
      return true;
    }

  }

  public interface LandmarkChooser {
    HashSet<MyVertex> choose(SimpleGraph<MyVertex, MyEdge> graph, int n);
  }

  public static class RandomLandmarkChooser implements LandmarkChooser {

    @Override
    public HashSet<MyVertex> choose(SimpleGraph<MyVertex, MyEdge> graph, int n) {
      final Set<MyVertex> vertexSet = graph.vertexSet();
      // find more determinisic solution, if O(landmarks)
      HashSet<MyVertex> landmarks = new HashSet<MyVertex>();

      Random random = new Random();
      while (landmarks.size() < n) {
        int size = vertexSet.size();
        int item = random.nextInt(size);
        int i = 0;
        for (MyVertex selected : vertexSet) {
          if (i == item) {
            landmarks.add(selected);
            selected.setLandmark(true);
          }
          i = i + 1;
        }
      }
      return landmarks;
    }

  }

  public static class CentralityLandMarkChooser implements LandmarkChooser {
    public static class Entry implements Comparable<Entry> {
      public Entry(MyVertex vertex, Double value) {
        super();
        this.vertex = vertex;
        this.value = value;
      }

      private final MyVertex vertex;
      private final Double value;

      public MyVertex getVertex() {
        return vertex;
      }

      public double getValue() {
        return value;
      }

      @Override
      public int compareTo(GraphDistanceTest.CentralityLandMarkChooser.Entry o) {
        return this.value.compareTo(o.getValue());
      }

      @Override
      public String toString() {
        return "Entry [value=" + value + ", vertex=" + vertex + "]";
      }

    }

    @Override
    public HashSet<MyVertex> choose(SimpleGraph<MyVertex, MyEdge> graph, int n) {
      CentralityComputer<MyVertex, MyEdge> computer = new CentralityComputer<MyVertex, MyEdge>(graph);
      Set<MyVertex> vertexSet = graph.vertexSet();
      List<Entry> list = new ArrayList<Entry>();
      for (MyVertex myVertex : vertexSet) {
        Double findClosenessOf = computer.findClosenessOf(myVertex);
        list.add(new Entry(myVertex, findClosenessOf));
      }
      Collections.sort(list);
      Collections.reverse(list);
      System.out.println(list);
      HashSet<MyVertex> result = new HashSet<MyVertex>();
      Iterator<GraphDistanceTest.CentralityLandMarkChooser.Entry> iterator = list.iterator();
      while (result.size() < n) {
        result.add(iterator.next().getVertex());
      }
      return result;
    }
  }
}
