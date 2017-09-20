package cn.tomoya.apps.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.tomoya.apps.model.book.LocalBook;

/**
 * Created by eebn on 3/30/2017.
 */

public class FormatUtil {

  public static final String DATE = "yyyy-MM-dd";
  public static final String DATETIME = "yyyy-MM-dd HH:mm:ss";
  public static final String USER_AGENT_MOBILE = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Mobile Safari/537.36";
  public static final String USER_AGENT_PC = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36";

  /**
   * 获取最近时间字符串
   */

  private static final long MINUTE = 60 * 1000;
  private static final long HOUR = 60 * MINUTE;
  private static final long DAY = 24 * HOUR;
  private static final long WEEK = 7 * DAY;
  private static final long MONTH = 31 * DAY;
  private static final long YEAR = 12 * MONTH;

  public static String getRelativeTimeSpanString(Date dateTime) {
    long offset = System.currentTimeMillis() - dateTime.getTime();
    if (offset > YEAR) {
      return (offset / YEAR) + "年前";
    } else if (offset > MONTH) {
      return (offset / MONTH) + "个月前";
    } else if (offset > WEEK) {
      return (offset / WEEK) + "周前";
    } else if (offset > DAY) {
      return (offset / DAY) + "天前";
    } else if (offset > HOUR) {
      return (offset / HOUR) + "小时前";
    } else if (offset > MINUTE) {
      return (offset / MINUTE) + "分钟前";
    } else {
      return "刚刚";
    }
  }

  public static Date string2Date(String dateString, String style) {
    if (dateString == null || "".equals(dateString)) return null;
    Date date = new Date();
    SimpleDateFormat strToDate = new SimpleDateFormat(style);
    try {
      date = strToDate.parse(dateString);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return date;
  }

  public static LocalBook findByName(List<LocalBook> localBooks, String name) {
    for (LocalBook book : localBooks) {
      if (book.getName().equals(name)) {
        return book;
      }
    }
    return null;
  }

  public static int findByCatalog(List<Map<String, Object>> catalogs, String catalog) {
    if (catalog == null) return 0;
    for (int i = 0; i < catalogs.size(); i++) {
      if (catalog.equals(catalogs.get(i).get("catalog").toString())) {
        return i;
      }
    }
    return 0;
  }
}
