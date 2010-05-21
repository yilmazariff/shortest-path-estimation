package riivo.shortestpath.landmarks;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.jgrapht.graph.SimpleGraph;

import riivo.shortestpath.graph.BreadthFirstSearchWithDistance;
import riivo.shortestpath.graph.MyEdge;
import riivo.shortestpath.graph.MyVertex;

public class ConstrainedDegreeChooser implements LandmarkChooser {

  private static final double MIN_DISTANCE = 2.0;

  @Override
  public HashSet<MyVertex> choose(SimpleGraph<MyVertex, MyEdge> graph, int n) {
    LinkedList<Entry> list = new LinkedList<Entry>();

    for (MyVertex myVertex : graph.vertexSet()) {
      list.add(new Entry(myVertex, graph.degreeOf(myVertex)));
    }
    Collections.sort(list);
    Collections.reverse(list);

    final HashSet<MyVertex> choosed = new HashSet<MyVertex>();

    outer: for (Iterator<Entry> iterator2 = list.iterator(); iterator2.hasNext();) {
      Entry poll = iterator2.next();
      final MyVertex underConsideration = poll.getVertex();
      boolean suitable = true;
      inner: for (MyVertex myVertex : choosed) {
        double distance = BreadthFirstSearchWithDistance.distance(graph, underConsideration, myVertex);
        if (distance < MIN_DISTANCE) {
          System.out.println("!" + distance + " ignore:" + poll);

          suitable = false;
          break inner;
        }
      }
      if (suitable) {
        System.out.println(poll);
        choosed.add(underConsideration);
      }
      if (choosed.size() >= n) {
        break outer;
      }

    }

    return choosed;
  }

  @Override
  public String getName() {
    return "ConstrainedDegree" + MIN_DISTANCE;
  }

}
