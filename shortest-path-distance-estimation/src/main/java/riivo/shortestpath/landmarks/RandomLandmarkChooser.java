package riivo.shortestpath.landmarks;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.jgrapht.graph.SimpleGraph;

import riivo.shortestpath.graph.MyEdge;
import riivo.shortestpath.graph.MyVertex;

public class RandomLandmarkChooser implements LandmarkChooser {
  static Random random = new Random(System.currentTimeMillis());

  @Override
  public HashSet<MyVertex> choose(SimpleGraph<MyVertex, MyEdge> graph, int n) {
    final Set<MyVertex> vertexSet = graph.vertexSet();
    // find more determinisic solution, if O(landmarks)
    HashSet<MyVertex> landmarks = new HashSet<MyVertex>();

    int size = vertexSet.size();
    while (landmarks.size() < n) {
      int item = random.nextInt(size);
      int i = 0;
      inner: for (MyVertex selected : vertexSet) {
        if (i == item) {
          landmarks.add(selected);
          break inner;
        }
        i = i + 1;
      }
    }
    return landmarks;
  }

  @Override
  public String getName() {
    return "random";
  }

}