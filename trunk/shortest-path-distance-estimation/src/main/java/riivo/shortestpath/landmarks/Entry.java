package riivo.shortestpath.landmarks;

import org.apache.commons.lang.math.NumberUtils;

import riivo.shortestpath.graph.MyVertex;

public class Entry implements Comparable<Entry> {
  public Entry(MyVertex vertex, Number value) {
    super();
    this.vertex = vertex;
    this.value = value;
  }

  private final MyVertex vertex;
  private final Number value;

  public MyVertex getVertex() {
    return vertex;
  }

  public Number getValue() {
    return value;
  }

  @Override
  public int compareTo(Entry o) {
    return NumberUtils.compare(this.value.doubleValue(), o.value.doubleValue());
  }

  @Override
  public String toString() {
    return "[" + value + ", vertex= " + vertex + "]";
  }

}