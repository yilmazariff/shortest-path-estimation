import java.util.Iterator;
import java.util.ListIterator;

import annas.graph.DefaultArc;
import annas.graph.DefaultWeight;
import annas.graph.GraphInterface;
import annas.graph.UndirectedGraph;
import annas.graph.util.traversal.BreadthFirst;

public class Main {
  public static void main(String[] args) throws Exception {
    Main m = new Main();
    m.setUp();
  }

  private GraphInterface<String, DefaultArc<String>> graph;

  private String a;
  private String b;
  private String c;
  private String d;
  private String e;
  private String f;
  private String g;

  private BreadthFirst<String, DefaultArc<String>> traversal;

  public void setUp() throws Exception {
    this.graph = new UndirectedGraph<String, DefaultArc<String>>();

    this.a = new String("A");
    this.b = new String("B");
    this.c = new String("C");
    this.d = new String("D");
    this.e = new String("E");
    this.f = new String("F");
    this.g = new String("G");

    this.graph.addNode(a);
    this.graph.addNode(b);
    this.graph.addNode(c);
    this.graph.addNode(d);
    this.graph.addNode(e);
    this.graph.addNode(f);
    this.graph.addNode(g);

    this.graph.addArc(a, b, new DefaultWeight(1d));
    this.graph.addArc(a, c, new DefaultWeight(1d));
    this.graph.addArc(b, d, new DefaultWeight(1d));
    this.graph.addArc(b, e, new DefaultWeight(1d));
    this.graph.addArc(c, f, new DefaultWeight(1d));
    this.graph.addArc(c, g, new DefaultWeight(1d));

    this.traversal = new BreadthFirst<String, DefaultArc<String>>(this.graph);
    int len = length(this.traversal);
    System.out.println(len);

  }

  private int length(BreadthFirst<String, DefaultArc<String>> traversal2) {
    ListIterator<String> i = (ListIterator<String>) traversal2.run(a, e);
    int n = 0;
    while (i.hasNext()) {
      n = n + 1;
      i.next();
    }
    return n;
  }

  public void testIsBipartite() {
    Iterator<String> i = this.traversal.run(a);

    this.traversal.isBipartite();

    this.graph.addArc(b, c, new DefaultWeight(1d));

    this.traversal = new BreadthFirst<String, DefaultArc<String>>(this.graph);

    this.traversal.run(a);
    this.traversal.isBipartite();

  }

}
