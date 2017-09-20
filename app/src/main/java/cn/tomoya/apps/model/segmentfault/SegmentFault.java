package cn.tomoya.apps.model.segmentfault;

/**
 * Created by tomoya on 3/27/17.
 */

public class SegmentFault {

  private int status;
  private Data data;
  private String message;

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
