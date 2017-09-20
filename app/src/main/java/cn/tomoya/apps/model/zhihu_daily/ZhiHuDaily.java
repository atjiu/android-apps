package cn.tomoya.apps.model.zhihu_daily;

import java.util.List;

/**
 * Created by eebn on 3/28/2017.
 */

public class ZhiHuDaily {
  private String date;
  private List<Story> stories;

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public List<Story> getStories() {
    return stories;
  }

  public void setStories(List<Story> stories) {
    this.stories = stories;
  }
}
