package cn.tomoya.apps.model.cnode;

/**
 * Created by tomoya on 3/26/17.
 */

public class Reply {

  private String id;
  private String content;
  private Author author;
  private String[] ups;
  private String create_at;
  private String reply_id;
  private boolean is_uped;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Author getAuthor() {
    return author;
  }

  public void setAuthor(Author author) {
    this.author = author;
  }

  public String[] getUps() {
    return ups;
  }

  public void setUps(String[] ups) {
    this.ups = ups;
  }

  public String getCreate_at() {
    return create_at;
  }

  public void setCreate_at(String create_at) {
    this.create_at = create_at;
  }

  public String getReply_id() {
    return reply_id;
  }

  public void setReply_id(String reply_id) {
    this.reply_id = reply_id;
  }

  public boolean is_uped() {
    return is_uped;
  }

  public void setIs_uped(boolean is_uped) {
    this.is_uped = is_uped;
  }
}
