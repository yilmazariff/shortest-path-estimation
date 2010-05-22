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

public class ConstrainedCentralityChooser implements LandmarkChooser {

  private static final int SEED = 500;
  private static final double MIN_DISTANCE = 2.0;

  @Override
  public HashSet<MyVertex> choose(final SimpleGraph<MyVertex, MyEdge> graph, int n) {
    final HashSet<MyVertex> vertexSet =
    new RandomLandmarkChooser().choose(graph, Math.min(SEED, graph.vertexSet().size()));

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

    final HashSet<MyVertex> choosed = new HashSet<MyVertex>();

    outer: for (Iterator<Entry> iterator2 = centralityDegree.iterator(); iterator2.hasNext();) {
      Entry poll = iterator2.next();
      final MyVertex underConsideration = poll.getVertex();
      boolean suitable = true;
      inner: for (MyVertex myVertex : choosed) {
        double distance = BreadthFirstSearchWithDistance.distance(graph, underConsideration, myVertex);
        if (distance < MIN_DISTANCE) {

          suitable = false;
          break inner;
        }
      }
      if (suitable) {
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
    return "ConstrainedCentraluty" + MIN_DISTANCE + "SEED_" + SEED;
  }

}
