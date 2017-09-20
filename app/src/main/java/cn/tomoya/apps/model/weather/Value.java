package cn.tomoya.apps.model.weather;

import java.util.List;

/**
 * Created by eebn on 3/28/2017.
 */

public class Value {
  private String city;
  private int cityid;
  private Pm25 pm25;
  private String provinceName;
  private Realtime realtime;
  private WeatherDetailsInfo weatherDetailsInfo;
  private List<WeekWeather> weathers;

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public int getCityid() {
    return cityid;
  }

  public void setCityid(int cityid) {
    this.cityid = cityid;
  }

  public Pm25 getPm25() {
    return pm25;
  }

  public void setPm25(Pm25 pm25) {
    this.pm25 = pm25;
  }

  public String getProvinceName() {
    return provinceName;
  }

  public void setProvinceName(String provinceName) {
    this.provinceName = provinceName;
  }

  public Realtime getRealtime() {
    return realtime;
  }

  public void setRealtime(Realtime realtime) {
    this.realtime = realtime;
  }

  public WeatherDetailsInfo getWeatherDetailsInfo() {
    return weatherDetailsInfo;
  }

  public void setWeatherDetailsInfo(WeatherDetailsInfo weatherDetailsInfo) {
    this.weatherDetailsInfo = weatherDetailsInfo;
  }

  public List<WeekWeather> getWeathers() {
    return weathers;
  }

  public void setWeathers(List<WeekWeather> weathers) {
    this.weathers = weathers;
  }
}
