package cn.tomoya.apps.model.segmentfault;

import java.util.List;

/**
 * Created by tomoya on 3/27/17.
 */

public class Row {

  private String id;
  private String title;
  private String host;
  private String url;
  private String description;
  private String currentStatus;
  private int realViews;
  private String votesWord;
  private String comments;
  private String createdDate;
  private String readFirstImg;
  private String cateType;
  private User user;
  private List<NewsType> newsTypes;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCurrentStatus() {
    return currentStatus;
  }

  public void setCurrentStatus(String currentStatus) {
    this.currentStatus = currentStatus;
  }

  public int getRealViews() {
    return realViews;
  }

  public void setRealViews(int realViews) {
    this.realViews = realViews;
  }

  public String getVotesWord() {
    return votesWord;
  }

  public void setVotesWord(String votesWord) {
    this.votesWord = votesWord;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public String getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(String createdDate) {
    this.createdDate = createdDate;
  }

  public String getReadFirstImg() {
    return readFirstImg;
  }

  public void setReadFirstImg(String readFirstImg) {
    this.readFirstImg = readFirstImg;
  }

  public String getCateType() {
    return cateType;
  }

  public void setCateType(String cateType) {
    this.cateType = cateType;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public List<NewsType> getNewsTypes() {
    return newsTypes;
  }

  public void setNewsTypes(List<NewsType> newsTypes) {
    this.newsTypes = newsTypes;
  }
}
