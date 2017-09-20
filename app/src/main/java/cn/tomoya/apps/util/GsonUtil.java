package cn.tomoya.apps.util;

import com.google.gson.Gson;

/**
 * Created by eebn on 3/28/2017.
 */

public class GsonUtil {

  private static final Gson ourInstance = new Gson();

  /**
   * 拿到GsonUtil单例
   *
   * @return
   */
  public static Gson getInstance() {
    return ourInstance;
  }

  private GsonUtil() {
  }
}
