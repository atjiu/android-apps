package cn.tomoya.apps.model.weather;

import java.util.List;

/**
 * Created by eebn on 3/28/2017.
 */

public class Weather {
  private String code;
  private String message;
  private String redirect;
  private List<Value> value;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getRedirect() {
    return redirect;
  }

  public void setRedirect(String redirect) {
    this.redirect = redirect;
  }

  public List<Value> getValue() {
    return value;
  }

  public void setValue(List<Value> value) {
    this.value = value;
  }
}
