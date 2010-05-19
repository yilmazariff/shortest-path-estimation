package riivo.shortestpath.landmarks;

import java.util.HashSet;

import org.jgrapht.graph.SimpleGraph;

import riivo.shortestpath.graph.MyEdge;
import riivo.shortestpath.graph.MyVertex;

public class CombinedCentralityChooser implements LandmarkChooser {

  @Override
  public HashSet<MyVertex> choose(SimpleGraph<MyVertex, MyEdge> graph, int n) {
    DegreeBasedLandmarkChooser degree = new DegreeBasedLandmarkChooser();
    CentralityLandmarkChooser centrality = new CentralityLandmarkChooser();
    HashSet<MyVertex> choose = degree.choose(graph, n);
    HashSet<MyVertex> choose2 = centrality.choose(graph, n);
    return choose;
  }

  @Override
  public String getName() {
    return "hack";
  }

}
