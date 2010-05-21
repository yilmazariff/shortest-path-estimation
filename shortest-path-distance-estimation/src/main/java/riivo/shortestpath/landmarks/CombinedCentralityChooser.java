package riivo.shortestpath.landmarks;

import java.util.Collections;
import java.util.HashSet;

import org.jgrapht.graph.SimpleGraph;

import riivo.shortestpath.graph.MyEdge;
import riivo.shortestpath.graph.MyVertex;

@Deprecated
public class CombinedCentralityChooser implements LandmarkChooser {

  @Override
  public HashSet<MyVertex> choose(SimpleGraph<MyVertex, MyEdge> graph, int n) {

    return (HashSet<MyVertex>) Collections.EMPTY_SET;
  }

  @Override
  public String getName() {
    return "hack";
  }

}
