package cn.tomoya.apps.model.v2ex;

/**
 * Created by tomoya on 3/31/17.
 */

public class Member {
  private int id;
  private String avatar_mini;
  private String avatar_normal;
  private String avatar_large;
  private String username;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getAvatar_mini() {
    return avatar_mini;
  }

  public void setAvatar_mini(String avatar_mini) {
    this.avatar_mini = avatar_mini;
  }

  public String getAvatar_normal() {
    return avatar_normal;
  }

  public void setAvatar_normal(String avatar_normal) {
    this.avatar_normal = avatar_normal;
  }

  public String getAvatar_large() {
    return avatar_large;
  }

  public void setAvatar_large(String avatar_large) {
    this.avatar_large = avatar_large;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
