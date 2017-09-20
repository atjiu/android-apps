package cn.tomoya.apps.model.weather;

/**
 * Created by eebn on 3/28/2017.
 */

public class Weather3HoursDetailsInfo {
  private String endTime;
  private String highestTemperature;
  //无降水
  private String isRainFall;
  private String lowerestTemperature;
  //雨量
  private String precipitation;
  private String startTime;
  //北风
  private String wd;
  //阴
  private String weather;
  //1级
  private String ws;

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public String getHighestTemperature() {
    return highestTemperature;
  }

  public void setHighestTemperature(String highestTemperature) {
    this.highestTemperature = highestTemperature;
  }

  public String getIsRainFall() {
    return isRainFall;
  }

  public void setIsRainFall(String isRainFall) {
    this.isRainFall = isRainFall;
  }

  public String getLowerestTemperature() {
    return lowerestTemperature;
  }

  public void setLowerestTemperature(String lowerestTemperature) {
    this.lowerestTemperature = lowerestTemperature;
  }

  public String getPrecipitation() {
    return precipitation;
  }

  public void setPrecipitation(String precipitation) {
    this.precipitation = precipitation;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getWd() {
    return wd;
  }

  public void setWd(String wd) {
    this.wd = wd;
  }

  public String getWeather() {
    return weather;
  }

  public void setWeather(String weather) {
    this.weather = weather;
  }

  public String getWs() {
    return ws;
  }

  public void setWs(String ws) {
    this.ws = ws;
  }
}
