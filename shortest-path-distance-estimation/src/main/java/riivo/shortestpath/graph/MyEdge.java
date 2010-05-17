package riivo.shortestpath.graph;
import org.jgrapht.graph.DefaultEdge;

public final class MyEdge extends DefaultEdge {

  @Override
  public MyVertex getSource() {
    return (MyVertex) super.getSource();
  }

  @Override
  public MyVertex getTarget() {
    return (MyVertex) super.getTarget();
  }

}