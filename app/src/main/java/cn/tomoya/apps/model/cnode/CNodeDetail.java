package cn.tomoya.apps.model.cnode;

/**
 * Created by tomoya on 3/26/17.
 */

public class CNodeDetail {

  private boolean success;
  private Data data;

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }
}
