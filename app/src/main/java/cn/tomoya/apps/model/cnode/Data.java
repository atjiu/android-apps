package cn.tomoya.apps.model.cnode;

import java.util.Date;
import java.util.List;

public class Data {
  private String id;
  private String author_id;
  private String tab;
  private String content;
  private String title;
  private String last_reply_at;
  private boolean good;
  private boolean top;
  private int reply_count;
  private int visit_count;
  private Date create_at;
  private Author author;
  private List<Reply> replies;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getAuthor_id() {
    return author_id;
  }

  public void setAuthor_id(String author_id) {
    this.author_id = author_id;
  }

  public String getTab() {
    return tab;
  }

  public void setTab(String tab) {
    this.tab = tab;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getLast_reply_at() {
    return last_reply_at;
  }

  public void setLast_reply_at(String last_reply_at) {
    this.last_reply_at = last_reply_at;
  }

  public boolean isGood() {
    return good;
  }

  public void setGood(boolean good) {
    this.good = good;
  }

  public boolean isTop() {
    return top;
  }

  public void setTop(boolean top) {
    this.top = top;
  }

  public int getReply_count() {
    return reply_count;
  }

  public void setReply_count(int reply_count) {
    this.reply_count = reply_count;
  }

  public int getVisit_count() {
    return visit_count;
  }

  public void setVisit_count(int visit_count) {
    this.visit_count = visit_count;
  }

  public Date getCreate_at() {
    return create_at;
  }

  public void setCreate_at(Date create_at) {
    this.create_at = create_at;
  }

  public Author getAuthor() {
    return author;
  }

  public void setAuthor(Author author) {
    this.author = author;
  }

  public List<Reply> getReplies() {
    return replies;
  }

  public void setReplies(List<Reply> replies) {
    this.replies = replies;
  }
}