package cn.tomoya.apps.model.cnode;

import java.util.List;

/**
 * Created by tomoya on 3/25/17.
 */

public class CNode {

  private boolean success;
  private List<Data> data;

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public List<Data> getData() {
    return data;
  }

  public void setData(List<Data> data) {
    this.data = data;
  }

}
