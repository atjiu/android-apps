package cn.tomoya.apps.model.segmentfault;

/**
 * Created by tomoya on 3/27/17.
 */

public class Page {

  private int current;
  private int total;
  private int size;
  private int next;

  public int getCurrent() {
    return current;
  }

  public void setCurrent(int current) {
    this.current = current;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public int getNext() {
    return next;
  }

  public void setNext(int next) {
    this.next = next;
  }
}
