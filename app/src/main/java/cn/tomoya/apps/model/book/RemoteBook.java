package cn.tomoya.apps.model.book;

/**
 * Created by eebn on 2017/4/5.
 */

public class RemoteBook {
  private String name;
  private String author;
  private String catalog_url;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getCatalog_url() {
    return catalog_url;
  }

  public void setCatalog_url(String catalog_url) {
    this.catalog_url = catalog_url;
  }
}
