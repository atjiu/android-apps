package cn.tomoya.apps.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tomoya on 4/6/17.
 */

public abstract class JsoupUtil implements Callback {

  public static final String BOLUOXS = "www.boluoxs.com";
  public static final String SANJIANGGE = "www.sanjiangge.com";
  public static final String QU_LA = "www.qu.la";
  public static final String MEIYUXS = "www.meiyuxs.com";

  public static void fetchBody(final String url, final Callback callback) {
    new Thread() {
      @Override
      public void run() {
        try {
          Document document = Jsoup
              .connect(url)
              .userAgent(FormatUtil.USER_AGENT_PC)
              .get();
          callback.output(document.body());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }.start();
  }

  // 异步操作
  public static void asyncFetchCatalog(final String url, final Callback callback) {
    final Map map = new HashMap();
    if (url == null || "".equals(url)) {
      callback.output(map);
    } else {
      new Thread() {
        @Override
        public void run() {
          if (url.contains(BOLUOXS)) {
            callback.output(boluxsCatalog(map, url));
          } else if (url.contains(SANJIANGGE)) {
            callback.output(sanjianggeCatalog(map, url));
          } else if (url.contains(QU_LA)) {
            callback.output(qulaCatalog(map, url));
          } else if (url.contains(MEIYUXS)) {
            callback.output(meiyuxsCatalog(map, url));
          }
        }
      }.start();
    }
  }

  // 同步操作
  public static void syncFetchCatalog(final String url, final Callback callback) {
    final Map map = new HashMap();
    if (url == null || "".equals(url)) {
      callback.output(map);
    } else {
      if (url.contains(BOLUOXS)) {
        callback.output(boluxsCatalog(map, url));
      } else if (url.contains(SANJIANGGE)) {
        callback.output(sanjianggeCatalog(map, url));
      } else if (url.contains(QU_LA)) {
        callback.output(qulaCatalog(map, url));
      } else if (url.contains(MEIYUXS)) {
        callback.output(meiyuxsCatalog(map, url));
      }
    }
  }

  private static Map boluxsCatalog(Map map, String url) {
    try {
      List data = new ArrayList();
      Document document = Jsoup
          .connect(url)
          .userAgent(FormatUtil.USER_AGENT_PC)
          .get();
      Element body = document.body();
      Elements catalogEles = body.getElementsByClass("article_texttitleb").last().getElementsByTag("li");
      for (Element catalogE : catalogEles) {
        Map<String, Object> _map = new HashMap<>();
        _map.put("catalog", catalogE.text());
        _map.put("href", url + catalogE.getElementsByTag("a").attr("href"));
        data.add(_map);
      }
      String cover = body.getElementsByClass("img").first().getElementsByTag("img").first().attr("src");
      map.put("data", data);
      map.put("cover", cover);
      map.put("lastChapter", ((Map) data.get(data.size() - 1)).get("catalog").toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return map;
  }

  private static Map sanjianggeCatalog(Map map, String url) {
    try {
      List data = new ArrayList();
      Document document = Jsoup
          .connect(url)
          .userAgent(FormatUtil.USER_AGENT_PC)
          .get();
      Element body = document.body();
      Elements catalogEles = body.getElementById("list").getElementsByTag("dd");
      for (Element catalogE : catalogEles) {
        if (catalogE.getElementsByTag("a").size() > 0) {
          Map<String, Object> _map = new HashMap<>();
          _map.put("catalog", catalogE.text());
          _map.put("href", url + catalogE.getElementsByTag("a").first().attr("href"));
          data.add(_map);
        }
      }
      String cover = body.getElementById("fmimg").getElementsByTag("img").first().attr("src");
      map.put("data", data);
      map.put("cover", cover);
      map.put("lastChapter", ((Map) data.get(data.size() - 1)).get("catalog").toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return map;
  }

  private static Map qulaCatalog(Map map, String url) {
    try {
      List data = new ArrayList();
      Document document = Jsoup
          .connect(url)
          .userAgent(FormatUtil.USER_AGENT_PC)
          .get();
      Element body = document.body();
      Elements catalogEles = body.getElementById("list").getElementsByTag("dd");
      for (Element catalogE : catalogEles) {
        if (catalogE.getElementsByTag("a").size() > 0) {
          Map<String, Object> _map = new HashMap<>();
          _map.put("catalog", catalogE.text());
          _map.put("href", url + catalogE.getElementsByTag("a").first().attr("href"));
          data.add(_map);
        }
      }
      String cover = body.getElementById("fmimg").getElementsByTag("img").first().attr("src");
      map.put("data", data);
      map.put("cover", cover);
      map.put("lastChapter", ((Map) data.get(data.size() - 1)).get("catalog").toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return map;
  }

  private static Map meiyuxsCatalog(Map map, String url) {
    try {
      List data = new ArrayList();
      Document document = Jsoup
          .connect(url)
          .userAgent(FormatUtil.USER_AGENT_PC)
          .get();
      Element body = document.body();
      Elements catalogEles = body.getElementsByClass("list-group-item");
      for (Element catalogE : catalogEles) {
        if (catalogE.getElementsByTag("a").size() > 0) {
          Map<String, Object> _map = new HashMap<>();
          _map.put("catalog", catalogE.text());
          _map.put("href", "http://www.meiyuxs.com" + catalogE.getElementsByTag("a").first().attr("href"));
          data.add(_map);
        }
      }
      map.put("data", data);
      map.put("cover", "");
      map.put("lastChapter", ((Map) data.get(data.size() - 1)).get("catalog").toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return map;
  }

  @Override
  public abstract void output(Object result);
}
