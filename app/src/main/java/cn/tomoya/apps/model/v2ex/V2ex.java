package cn.tomoya.apps.model.v2ex;

/**
 * Created by tomoya on 3/27/17.
 */

public class V2ex {
  private String title;
  private String url;
  private Member member;
  private long created;
  private int replies;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Member getMember() {
    return member;
  }

  public void setMember(Member member) {
    this.member = member;
  }

  public long getCreated() {
    return created;
  }

  public void setCreated(long created) {
    this.created = created;
  }

  public int getReplies() {
    return replies;
  }

  public void setReplies(int replies) {
    this.replies = replies;
  }
}
