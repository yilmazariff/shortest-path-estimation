package riivo.shortestpath.landmarks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.jgrapht.graph.SimpleGraph;

import riivo.shortestpath.graph.MyEdge;
import riivo.shortestpath.graph.MyVertex;

public class DegreeBasedLandmarkChooser implements LandmarkChooser {

  @Override
  public HashSet<MyVertex> choose(SimpleGraph<MyVertex, MyEdge> graph, int n) {
    List<Entry> list = new ArrayList<Entry>();

    for (MyVertex myVertex : graph.vertexSet()) {
      list.add(new Entry(myVertex, graph.degreeOf(myVertex)));
    }
    Collections.sort(list);

    HashSet<MyVertex> result = new HashSet<MyVertex>();
    for (int j = list.size() - 1; j > list.size() - (n + 1); j--) {
      result.add(list.get(j).getVertex());
    }
    return result;
  }

  @Override
  public String getName() {
    return "degree";
  }
}