package riivo.shortestpath.landmarks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.jgrapht.graph.SimpleGraph;

import riivo.shortestpath.graph.BreadthFirstSearchWithDistance;
import riivo.shortestpath.graph.MyEdge;
import riivo.shortestpath.graph.MyVertex;
import riivo.shortestpath.graph.BreadthFirstSearchWithDistance.Callable;

public class CentralityLandmarkChooser implements LandmarkChooser {

  private static final int FACTOR = 10;

  @Override
  public HashSet<MyVertex> choose(SimpleGraph<MyVertex, MyEdge> graph, int n) {
    final HashSet<MyVertex> vertexSet = new RandomLandmarkChooser().choose(graph, Math.min(n * FACTOR, graph.vertexSet().size()));

    final List<Entry> centralityDegree = new ArrayList<Entry>();

    for (final MyVertex myVertex : vertexSet) {
      final DescriptiveStatistics stats = new DescriptiveStatistics();
      BreadthFirstSearchWithDistance.bfs(graph, new Callable() {

        @Override
        public void call(SimpleGraph<MyVertex, MyEdge> graph, MyVertex start, MyVertex next, int level) {
          stats.addValue(level);
        }
      }, myVertex);
      centralityDegree.add(new Entry(myVertex, stats.getMean()));

    }

    Collections.sort(centralityDegree);
    final HashSet<MyVertex> result = new HashSet<MyVertex>();
    final Iterator<Entry> iterator = centralityDegree.iterator();
    while (result.size() < n) {
      result.add(iterator.next().getVertex());
    }
    return result;
  }
}