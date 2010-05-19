package riivo.shortestpath.graph;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.jgrapht.graph.SimpleGraph;


/**
 * Finds shortest path by breadth first iteration. Only suitable for unweighted graphs
 * @author Riivo
 *
 */
public class MyShortestPath {
  public static double distance(SimpleGraph<MyVertex, MyEdge> graph, MyVertex start, MyVertex end) {
    Queue<MyVertex> queue = new LinkedList<MyVertex>();
    Set<MyVertex> visited = new HashSet<MyVertex>();
    queue.add(start);
    int dist = breadth(graph, start, end, queue, visited, 0);
    if (dist == -1) {
      throw new IllegalArgumentException("Path from " + start + " to : " + end + " not found!!");
    }
    return dist * 1.0;
  }

  private static int breadth(final SimpleGraph<MyVertex, MyEdge> graph,
                             final MyVertex start,
                             final MyVertex end,
                             final Queue<MyVertex> queue,
                             final Set<MyVertex> visited,
                             int level) {

    MyVertex next = queue.poll();
    Queue<MyVertex> nextLevel = new LinkedList<MyVertex>();
    while (next != null) {

      if (!visited.contains(next)) {

        visited.add(next);
        if (next.equals(end)) {
          return level;
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
      return breadth(graph, start, end, nextLevel, visited, level + 1);

    return -1;
  }
}
