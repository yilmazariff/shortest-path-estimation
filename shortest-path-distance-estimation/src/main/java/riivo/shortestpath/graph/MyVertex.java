package riivo.shortestpath.graph;

import java.util.HashMap;

public class MyVertex {

  private final int id;

  private final HashMap<MyVertex, Integer> landMarkDistances = new HashMap<MyVertex, Integer>();

  public MyVertex(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public HashMap<MyVertex, Integer> getLandMarkDistances() {
    return landMarkDistances;
  }

  public void put(MyVertex start, int level) {
    synchronized (landMarkDistances) {
      landMarkDistances.put(start, level);
    }
  }

  @Override
  public String toString() {
    return "MyVertex [id=" + id + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MyVertex other = (MyVertex) obj;
    if (id != other.id)
      return false;
    return true;
  }

}