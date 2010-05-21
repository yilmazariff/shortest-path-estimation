package riivo.shortestpath.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jgrapht.graph.SimpleGraph;

public class BreadthFirstSearchWithDistance {

  private static final Logger log = Logger.getLogger(BreadthFirstSearchWithDistance.class);

  public static interface Callable {

    void call(SimpleGraph<MyVertex, MyEdge> graph, MyVertex start, MyVertex next, int level);
  }

  public static double distance(SimpleGraph<MyVertex, MyEdge> graph, MyVertex start, MyVertex end) {
    int d = breadthFirstSearch(graph, null, start, end);
    if (d == -1) {
      throw new IllegalArgumentException("target not found");
    }
    return d * 1.;

  }

  public static void bfs(final SimpleGraph<MyVertex, MyEdge> graph, final Callable callable, final MyVertex start) {
    breadthFirstSearch(graph, callable, start, null);
  }

  public static void bfsOld(final SimpleGraph<MyVertex, MyEdge> graph, final Callable callable, final MyVertex start) {
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

  public static int breadthFirstSearch(final SimpleGraph<MyVertex, MyEdge> graph,
                                       final Callable callable,
                                       final MyVertex start,
                                       final MyVertex target) {

    // Set the initial conditions for the source node
    Node snode = new Node();
    snode.setColor(Color.GRAY);
    snode.setDistance(0);
    HashMap<MyVertex, Node> info = new HashMap<MyVertex, Node>();

    Queue<MyVertex> q = new LinkedList<MyVertex>();
    info.put(start, snode);
    q.add(start);

    while (!q.isEmpty()) {
      MyVertex next = q.poll();

      Node unode = info.get(next);
      if (target != null && target.equals(next)) {
        return unode.getDistance();
      }
      if (callable != null)
        callable.call(graph, start, next, unode.getDistance());
      for (MyEdge myEdge : graph.edgesOf(next)) {
        MyVertex to =
        graph.getEdgeSource(myEdge).equals(next) ? graph.getEdgeTarget(myEdge) : graph.getEdgeSource(myEdge);

        Node vnode = info.get(to);
        if (vnode == null) {
          vnode = new Node();
          info.put(to, vnode);
        }
        if (vnode.getColor() == Color.WHITE) {
          vnode.setColor(Color.GRAY);
          vnode.setDistance(unode.getDistance() + 1);
          q.add(to);
        }
      }
      unode.setColor(Color.BLACK);
    }
    return -1;
  }

  public enum Color {
    WHITE, BLACK, GRAY
  };

  public static class Node {
    Color color = Color.WHITE;
    int distance;

    public Color getColor() {
      return color;
    }

    public void setColor(Color color) {
      this.color = color;
    }

    public int getDistance() {
      return distance;
    }

    public void setDistance(int distance) {
      this.distance = distance;
    }

  }

}
