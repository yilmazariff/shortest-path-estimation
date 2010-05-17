package riivo.shortespath;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.jgrapht.VertexFactory;
import org.jgrapht.generate.RandomGraphGenerator;
import org.jgrapht.graph.SimpleGraph;

import riivo.shortestpath.graph.MyEdge;
import riivo.shortestpath.graph.MyVertex;

public class GraphReader {
  static int vertexCount = 811500;
  static int edgeCount = 18000000;

  public static SimpleGraph<MyVertex, MyEdge> run() {
    return getSlahDot();
  }

  public static SimpleGraph<MyVertex, MyEdge> getSmall() {
    return read("data/small.txt");
  }

  public static SimpleGraph<MyVertex, MyEdge> getSlahDot() {
    return read("data/Slashdot0811.txt");
  }

  public static SimpleGraph<MyVertex, MyEdge> read(String file) {
    SimpleGraph<MyVertex, MyEdge> graph = new SimpleGraph<MyVertex, MyEdge>(MyEdge.class);
    HashMap<Integer, MyVertex> map = new HashMap<Integer, MyVertex>();
    try {
      BufferedReader input = new BufferedReader(new FileReader(file));
      try {
        String line = null;
        while ((line = input.readLine()) != null) {
          line = line.trim();
          if (line.indexOf("#") == 0)
            continue;
          String[] parts = line.split("\t");
          int from = Integer.parseInt(parts[0]);
          int to = Integer.parseInt(parts[1]);
          if (map.get(from) == null) {
            MyVertex vert = new MyVertex(from);
            map.put(from, vert);
            graph.addVertex(vert);
          }
          if (map.get(to) == null) {
            MyVertex vert = new MyVertex(to);
            map.put(to, vert);
            graph.addVertex(vert);
          }
          if (from != to) {
            graph.addEdge(map.get(from), map.get(to));

          }
        }
      } finally {
        input.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return graph;
  }

  public static SimpleGraph<MyVertex, MyEdge> getGraph() {
    SimpleGraph<MyVertex, MyEdge> graph = new SimpleGraph<MyVertex, MyEdge>(MyEdge.class);

    RandomGraphGenerator<MyVertex, MyEdge> completeGenerator =
    new RandomGraphGenerator<MyVertex, MyEdge>(vertexCount, edgeCount);

    completeGenerator.generateGraph(graph, new VertexFactory<MyVertex>() {
      int last = 0;

      public MyVertex createVertex() {
        return new MyVertex(last++);
      }
    }, null);
    return graph;
  }
}
