package riivo.shortestpath.landmarks;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.graph.SimpleGraph;

import riivo.shortestpath.graph.MyEdge;
import riivo.shortestpath.graph.MyVertex;

public class CentralityLandmarkChooser implements LandmarkChooser {

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
    HashSet<MyVertex> result = new HashSet<MyVertex>();
    Iterator<Entry> iterator = list.iterator();
    while (result.size() < n) {
      result.add(iterator.next().getVertex());
    }
    return result;
  }
}