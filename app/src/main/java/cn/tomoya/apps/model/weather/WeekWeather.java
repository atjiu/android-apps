package cn.tomoya.apps.model.weather;

/**
 * Created by eebn on 4/4/2017.
 */

public class WeekWeather {

  private String date;
  private String sun_down_time;
  private String sun_rise_time;
  private String temp_day_c;
  private String temp_night_c;
  private String wd;
  private String ws;
  private String weather;
  private String week;

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getSun_down_time() {
    return sun_down_time;
  }

  public void setSun_down_time(String sun_down_time) {
    this.sun_down_time = sun_down_time;
  }

  public String getSun_rise_time() {
    return sun_rise_time;
  }

  public void setSun_rise_time(String sun_rise_time) {
    this.sun_rise_time = sun_rise_time;
  }

  public String getTemp_day_c() {
    return temp_day_c;
  }

  public void setTemp_day_c(String temp_day_c) {
    this.temp_day_c = temp_day_c;
  }

  public String getTemp_night_c() {
    return temp_night_c;
  }

  public void setTemp_night_c(String temp_night_c) {
    this.temp_night_c = temp_night_c;
  }

  public String getWd() {
    return wd;
  }

  public void setWd(String wd) {
    this.wd = wd;
  }

  public String getWs() {
    return ws;
  }

  public void setWs(String ws) {
    this.ws = ws;
  }

  public String getWeather() {
    return weather;
  }

  public void setWeather(String weather) {
    this.weather = weather;
  }

  public String getWeek() {
    return week;
  }

  public void setWeek(String week) {
    this.week = week;
  }
}
