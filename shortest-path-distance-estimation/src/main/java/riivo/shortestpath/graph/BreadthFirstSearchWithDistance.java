package riivo.shortestpath.graph;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.jgrapht.graph.SimpleGraph;

public class BreadthFirstSearchWithDistance {

  public static interface Callable {

    void call(SimpleGraph<MyVertex, MyEdge> graph, MyVertex start, MyVertex next, int level);
  }

  public static void bfs(final SimpleGraph<MyVertex, MyEdge> graph, final Callable callable, final MyVertex start) {
    Queue<MyVertex> queue = new LinkedList<MyVertex>();
    Set<MyVertex> visited = new HashSet<MyVertex>();
    queue.add(start);
    breadth(graph, callable, start, queue, visited, 0);
  }

  private static void breadth(final SimpleGraph<MyVertex, MyEdge> graph,
                              final Callable callable,
                              final MyVertex start,
                              final Queue<MyVertex> queue,
                              final Set<MyVertex> visited,
                              int level) {

    MyVertex next = queue.poll();
    Queue<MyVertex> nextLevel = new LinkedList<MyVertex>();
    while (next != null) {
      if (!visited.contains(next)) {
        visited.add(next);

        callable.call(graph, start, next, level);
        // next.getLandMarkDistances().put(start, level);

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
      breadth(graph, callable, start, nextLevel, visited, level + 1);
  }
}
