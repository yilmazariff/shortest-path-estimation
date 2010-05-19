package riivo.shortestpath.landmarks;

import java.util.HashSet;

import org.jgrapht.graph.SimpleGraph;

import riivo.shortestpath.graph.MyEdge;
import riivo.shortestpath.graph.MyVertex;

public interface LandmarkChooser {
  HashSet<MyVertex> choose(SimpleGraph<MyVertex, MyEdge> graph, int n);

  String getName();
}