package cn.tomoya.apps.model.weather;

/**
 * Created by eebn on 3/28/2017.
 */

public class Realtime {
  //湿度
  private String sD;
  //体感温度
  private String sendibleTemp;
  private String temp;
  private String time;
  //南风
  private String wD;
  //4级
  private String wS;
  //阴
  private String weather;
  private String ziwaixian;

  public String getsD() {
    return sD;
  }

  public void setsD(String sD) {
    this.sD = sD;
  }

  public String getSendibleTemp() {
    return sendibleTemp;
  }

  public void setSendibleTemp(String sendibleTemp) {
    this.sendibleTemp = sendibleTemp;
  }

  public String getTemp() {
    return temp;
  }

  public void setTemp(String temp) {
    this.temp = temp;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public String getwD() {
    return wD;
  }

  public void setwD(String wD) {
    this.wD = wD;
  }

  public String getwS() {
    return wS;
  }

  public void setwS(String wS) {
    this.wS = wS;
  }

  public String getWeather() {
    return weather;
  }

  public void setWeather(String weather) {
    this.weather = weather;
  }

  public String getZiwaixian() {
    return ziwaixian;
  }

  public void setZiwaixian(String ziwaixian) {
    this.ziwaixian = ziwaixian;
  }
}
