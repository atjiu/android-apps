package cn.tomoya.apps.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.tomoya.apps.R;
import cn.tomoya.apps.activity.customtab.CustomTabActivityHelper;
import cn.tomoya.apps.activity.customtab.WebviewFallback;
import cn.tomoya.apps.listener.DoubleClickBackToContentTopListener;
import cn.tomoya.apps.viewholder.LoadMoreFooter;
import cn.tomoya.apps.viewholder.MyBaseAdapter;

/**
 * Created by tomoya on 3/25/17.
 */

public class W3cplusActivity extends BaseActivity implements
    SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener,
    DoubleClickBackToContentTopListener.IBackToContentTopView,
    LoadMoreFooter.OnLoadMoreListener {

  private List<Map<String, Object>> data = new ArrayList<>();
  private SwipeRefreshLayout refreshLayout;
  private ListView listView;
  private Toolbar toolbar;
  private MyBaseAdapter<Map<String, Object>> adapter;
  private LoadMoreFooter loadMoreFooter;
  private int page = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_w3cplus);

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle(R.string.w3cplus);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        W3cplusActivity.this.finish();
      }
    });
    toolbar.setOnClickListener(new DoubleClickBackToContentTopListener(this));

    refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
    listView = (ListView) findViewById(R.id.list_view);
    adapter = new MyBaseAdapter<Map<String, Object>>(this, data) {
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(context, convertView, parent, R.layout.list_view_item_w3cplus, position);
        Map<String, Object> map = _data.get(position);
        holder.setText(R.id.title, map.get("title").toString())
            .setText(R.id.time, map.get("time").toString())
            .setText(R.id.desc, map.get("desc").toString())
            .setText(R.id.author, map.get("author").toString());
        return holder.getConvertView();
      }
    };
    listView.setAdapter(adapter);
    loadMoreFooter = new LoadMoreFooter(this, listView, this);

    refreshLayout.setOnRefreshListener(this);
    listView.setOnItemClickListener(this);

    refreshLayout.setRefreshing(true);
    initData();
  }

  private void initData() {
    new Thread() {
      @Override
      public void run() {
        try {
          Document document = Jsoup
              .connect("https://www.w3cplus.com/?page=" + page)
              .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.98 Safari/537.36")
              .get();
          Element body = document.body();
          Elements nodeBlogEles = body.getElementById("block-system-main").getElementsByClass("node-blog");
          for (Element nodeBlogE : nodeBlogEles) {
            String title = nodeBlogE.getElementsByTag("h1").first().text();
            String href = nodeBlogE.getElementsByTag("h1").first().getElementsByTag("a").first().attr("href");
            String author = nodeBlogE.getElementsByClass("submitted").first().getElementsByTag("span").first().text();
            String time = nodeBlogE.getElementsByClass("submitted").first().getElementsByTag("span").get(1).text();
            String desc = nodeBlogE.getElementsByClass("body-content").first().text();
            href = "https://www.w3cplus.com" + href;
            Map<String, Object> map = new HashMap<>();
            map.put("title", title);
            map.put("href", href);
            map.put("author", author);
            map.put("time", time);
            map.put("desc", desc);
            data.add(map);
          }
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              if (refreshLayout.isRefreshing()) refreshLayout.setRefreshing(false);
              loadMoreFooter.setState(LoadMoreFooter.STATE_ENDLESS);
              adapter.notifyDataSetChanged();
            }
          });
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }.start();
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Map<String, Object> map = data.get(position);
    String topic_id = map.get("href").toString();
    CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
        .setToolbarColor(getResources().getColor(R.color.colorPrimary)).setShowTitle(true).addDefaultShareMenuItem().build();
    CustomTabActivityHelper.openCustomTab(
        this, customTabsIntent, Uri.parse(topic_id), new WebviewFallback());
  }

  @Override
  public void onRefresh() {
    page = 0;
    data = new ArrayList<>();
    initData();
  }

  @Override
  public void onLoadMore() {
    page++;
    loadMoreFooter.setState(LoadMoreFooter.STATE_LOADING);
    initData();
  }

  @Override
  public void backToContentTop() {
    listView.setSelection(0);
  }
}
