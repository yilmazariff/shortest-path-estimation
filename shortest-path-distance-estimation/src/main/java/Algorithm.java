import java.util.Map;
import java.util.Set;

public class Algorithm {

  public static int LANDMARKS = 10;

  public double centrality(Vertex node, Graph graph) {
    throw new RuntimeException("not implemented");
  }

  public double degree(Vertex node, Graph graph) {
    throw new RuntimeException("not implemented");
  }

  public Object bfs(Vertex from, Graph graph) {
    throw new RuntimeException("not implemented");
  }

  public Object index(Vertex node, Graph graph) {
    throw new RuntimeException("not implemented");
  }

  public double estimateDistance(Vertex from, Vertex to, Graph graph) {

    return 0.0;

  }

  public static class Graph {
    Set<Vertex> vertices;
    Set<Landmark> landmarks;
  }

  public static class Vertex {

    Map<Landmark, Double> distances;
  }

  public static class Landmark extends Vertex {

  }

}
