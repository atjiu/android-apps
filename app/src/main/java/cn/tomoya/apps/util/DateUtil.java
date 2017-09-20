package cn.tomoya.apps.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by tomoya on 3/26/17.
 */

public class DateUtil {

  public static final String DATE = "yyyy-MM-dd";
  public static final String DATE2 = "yyyyMMdd";
  public static final String DATETIME = "yyyy-MM-dd HH:mm:ss";

  private static final SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat(DATE);
  private static final SimpleDateFormat simpleDateFormatDatetime = new SimpleDateFormat(DATETIME);

  public static String formatDate() {
    return simpleDateFormatDate.format(new Date());
  }

  public static String formatDateTime() {
    return simpleDateFormatDatetime.format(new Date());
  }

  public static String formatDate(Date date) {
    return simpleDateFormatDate.format(date);
  }

  public static String formatDate(Date date, String pattern) {
    return new SimpleDateFormat(pattern).format(date);
  }

  public static Date string2Date(String dateString, String style) {
    Date date = new Date();
    SimpleDateFormat strToDate = new SimpleDateFormat(style);
    try {
      date = strToDate.parse(dateString);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return date;
  }

  public static Date getDateBefore(Date date, int day) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DAY_OF_MONTH, -day);
    return calendar.getTime();
  }

  public static Date getDateAfter(Date date, int day) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DAY_OF_MONTH, day);
    return calendar.getTime();
  }

}
