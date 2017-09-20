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

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.tomoya.apps.R;
import cn.tomoya.apps.activity.customtab.CustomTabActivityHelper;
import cn.tomoya.apps.activity.customtab.WebviewFallback;
import cn.tomoya.apps.listener.DoubleClickBackToContentTopListener;
import cn.tomoya.apps.util.JsoupUtil;
import cn.tomoya.apps.viewholder.LoadMoreFooter;
import cn.tomoya.apps.viewholder.MyBaseAdapter;

/**
 * Created by tomoya on 3/25/17.
 */

public class NewsBrotherActivity extends BaseActivity implements
    SwipeRefreshLayout.OnRefreshListener,
    DoubleClickBackToContentTopListener.IBackToContentTopView,
    AdapterView.OnItemClickListener, LoadMoreFooter.OnLoadMoreListener {

  private List<Map<String, Object>> data = new ArrayList<>();
  private SwipeRefreshLayout refreshLayout;
  private ListView listView;
  private Toolbar toolbar;
  private MyBaseAdapter<Map<String, Object>> adapter;
  private LoadMoreFooter loadMoreFooter;
  private int page = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_newsbrother);

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle(R.string.newsbrother);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        NewsBrotherActivity.this.finish();
      }
    });
    toolbar.setOnClickListener(new DoubleClickBackToContentTopListener(this));

    refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
    listView = (ListView) findViewById(R.id.list_view);
    adapter = new MyBaseAdapter<Map<String, Object>>(this, data) {
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(NewsBrotherActivity.this, convertView, parent, R.layout.list_view_item_newsbrother, position);
        Map map = _data.get(position);
        holder.setText(R.id.title, map.get("title").toString())
            .setText(R.id.author, map.get("author").toString())
            .setText(R.id.time, map.get("time").toString());
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
    JsoupUtil.fetchBody("http://news.qq.com/c/xinwengeH5_" + page + ".htm", new cn.tomoya.apps.util.Callback() {
      @Override
      public void output(Object result) {
        Element body = (Element) result;
        Elements nrcEles = body.getElementsByClass("nrc");
        for (Element nrcE : nrcEles) {
          Map<String, Object> map = new HashMap<>();
          map.put("title", nrcE.getElementsByTag("h2").first().text());
          map.put("time", nrcE.getElementsByClass("time").first().text());
          map.put("author", nrcE.getElementsByTag("p").first().ownText());
          map.put("href", "http://news.qq.com" + nrcE.getElementsByTag("a").first().attr("href"));
          data.add(map);
        }
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            adapter.notifyDataSetChanged();
            loadMoreFooter.setState(LoadMoreFooter.STATE_ENDLESS);
            refreshLayout.setRefreshing(false);
          }
        });
      }
    });
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Map<String, Object> map = data.get(position);
    CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
        .setToolbarColor(getResources().getColor(R.color.colorPrimary)).setShowTitle(true).addDefaultShareMenuItem().build();
    CustomTabActivityHelper.openCustomTab(
        this, customTabsIntent, Uri.parse(map.get("href").toString()), new WebviewFallback());
  }

  @Override
  public void onRefresh() {
    data.clear();
    page = 1;
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
