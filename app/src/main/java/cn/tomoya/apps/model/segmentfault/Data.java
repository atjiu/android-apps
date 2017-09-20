package cn.tomoya.apps.model.segmentfault;

import java.util.List;

/**
 * Created by tomoya on 3/27/17.
 */

public class Data {

  private List<Row> rows;
  private Page page;

  public List<Row> getRows() {
    return rows;
  }

  public void setRows(List<Row> rows) {
    this.rows = rows;
  }

  public Page getPage() {
    return page;
  }

  public void setPage(Page page) {
    this.page = page;
  }
}
