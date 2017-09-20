package cn.tomoya.apps.model.weather;

import java.util.List;

/**
 * Created by eebn on 3/28/2017.
 */

public class WeatherDetailsInfo {
  private List<Weather3HoursDetailsInfo> weather3HoursDetailsInfos;

  public List<Weather3HoursDetailsInfo> getWeather3HoursDetailsInfos() {
    return weather3HoursDetailsInfos;
  }

  public void setWeather3HoursDetailsInfos(List<Weather3HoursDetailsInfo> weather3HoursDetailsInfos) {
    this.weather3HoursDetailsInfos = weather3HoursDetailsInfos;
  }
}
