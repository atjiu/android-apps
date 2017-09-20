package cn.tomoya.apps.model.zhihu_daily;

import java.util.List;

/**
 * Created by eebn on 3/28/2017.
 */

public class Story {
  private int id;
  private String title;
  private List<String> images;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<String> getImages() {
    return images;
  }

  public void setImages(List<String> images) {
    this.images = images;
  }
}
