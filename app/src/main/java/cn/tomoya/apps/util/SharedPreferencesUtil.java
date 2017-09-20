package cn.tomoya.apps.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import cn.tomoya.apps.model.book.LocalBook;

/**
 * Created by eebn on 2017/4/6.
 */

public class SharedPreferencesUtil {

  public static List<LocalBook> getLocalBooks(Context context) {
    String localBooks = PreferenceManager.getDefaultSharedPreferences(context).getString("localBooks", null);
    if (localBooks != null) {
      return GsonUtil.getInstance().fromJson(localBooks, new TypeToken<List<LocalBook>>() {
      }.getType());
    } else {
      return new ArrayList<>();
    }
  }

  public static void saveLocalBooks(List<LocalBook> localBookData, Context context) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString("localBooks", GsonUtil.getInstance().toJson(localBookData));
    editor.commit();
  }

  public static String getLocation(Context context) {
    return PreferenceManager.getDefaultSharedPreferences(context).getString("location", null);
  }

  public static void saveLocation(String location, Context context) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString("location", location);
    editor.commit();
  }

  public static List<String> getLocalTodos(Context context) {
    String localTodos = PreferenceManager.getDefaultSharedPreferences(context).getString("localTodos", null);
    if (localTodos != null) {
      return GsonUtil.getInstance().fromJson(localTodos, new TypeToken<ArrayList>() {
      }.getType());
    } else {
      return new ArrayList<>();
    }
  }

  public static void saveLocalTodos(List<String> localTodoData, Context context) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString("localTodos", GsonUtil.getInstance().toJson(localTodoData));
    editor.commit();
  }
}
