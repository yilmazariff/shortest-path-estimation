package riivo.shortestpath.landmarks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.jgrapht.graph.SimpleGraph;

import riivo.shortespath.GraphDistanceEstimation;
import riivo.shortespath.util.ThreadPool;
import riivo.shortestpath.graph.BreadthFirstSearchWithDistance;
import riivo.shortestpath.graph.MyEdge;
import riivo.shortestpath.graph.MyVertex;
import riivo.shortestpath.graph.BreadthFirstSearchWithDistance.Callable;

public class CentralityLandmarkChooser implements LandmarkChooser {

  private static final int SEED_SIZE = 40;

  @Override
  public HashSet<MyVertex> choose(final SimpleGraph<MyVertex, MyEdge> graph, int n) {
    final HashSet<MyVertex> vertexSet =
    new RandomLandmarkChooser().choose(graph, Math.min(n * SEED_SIZE, graph.vertexSet().size()));

    final List<Entry> centralityDegree = new ArrayList<Entry>();
    ThreadPool tp = new ThreadPool(GraphDistanceEstimation.threads);
    for (final MyVertex myVertex : vertexSet) {
      tp.runTask(new Runnable() {

        @Override
        public void run() {
          final DescriptiveStatistics stats = new DescriptiveStatistics();
          BreadthFirstSearchWithDistance.bfs(graph, new Callable() {

            @Override
            public void call(SimpleGraph<MyVertex, MyEdge> graph, MyVertex start, MyVertex next, int level) {
              stats.addValue(level);
            }
          }, myVertex);
          synchronized (centralityDegree) {
            centralityDegree.add(new Entry(myVertex, stats.getMean()));
          }

        }
      });
    }

    tp.join();

    Collections.sort(centralityDegree);

    final HashSet<MyVertex> result = new HashSet<MyVertex>();
    final Iterator<Entry> iterator = centralityDegree.iterator();
    while (result.size() < n) {
      result.add(iterator.next().getVertex());
    }
    return result;
  }

  @Override
  public String getName() {
    return "Centrality";
  }
}