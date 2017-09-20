package cn.tomoya.apps.model.book;

import java.util.List;
import java.util.Map;

/**
 * Created by eebn on 2017/4/5.
 */

public class LocalBook {

  private String author;
  private String name;
  private String cover;
  private String lastChapter;
  private String catalogUrl;//目录地址
  private String currentReadCatalog;
  private List<Map<String, Object>> catalogs;

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCover() {
    return cover;
  }

  public void setCover(String cover) {
    this.cover = cover;
  }

  public String getLastChapter() {
    return lastChapter;
  }

  public void setLastChapter(String lastChapter) {
    this.lastChapter = lastChapter;
  }

  public String getCatalogUrl() {
    return catalogUrl;
  }

  public void setCatalogUrl(String catalogUrl) {
    this.catalogUrl = catalogUrl;
  }

  public String getCurrentReadCatalog() {
    return currentReadCatalog;
  }

  public void setCurrentReadCatalog(String currentReadCatalog) {
    this.currentReadCatalog = currentReadCatalog;
  }

  public List<Map<String, Object>> getCatalogs() {
    return catalogs;
  }

  public void setCatalogs(List<Map<String, Object>> catalogs) {
    this.catalogs = catalogs;
  }
}
