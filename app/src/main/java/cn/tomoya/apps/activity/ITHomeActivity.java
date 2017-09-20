package cn.tomoya.apps.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.jsoup.Jsoup;
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
import cn.tomoya.apps.model.ithome.ITHome;
import cn.tomoya.apps.util.GsonUtil;
import cn.tomoya.apps.viewholder.LoadMoreFooter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by tomoya on 3/25/17.
 */

public class ITHomeActivity extends BaseActivity implements
    SwipeRefreshLayout.OnRefreshListener,
    DoubleClickBackToContentTopListener.IBackToContentTopView,
    AdapterView.OnItemClickListener, LoadMoreFooter.OnLoadMoreListener {

  private List<Map<String, Object>> data = new ArrayList<>();
  private SwipeRefreshLayout refreshLayout;
  private ListView listView;
  private Toolbar toolbar;
  private SimpleAdapter adapter;
  private LoadMoreFooter loadMoreFooter;
  private int page = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ithome);

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle(R.string.ithome);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ITHomeActivity.this.finish();
      }
    });
    toolbar.setOnClickListener(new DoubleClickBackToContentTopListener(this));

    refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
    listView = (ListView) findViewById(R.id.list_view);
    adapter = new SimpleAdapter(this, data, R.layout.list_view_item_ithome, new String[]{"title", "href"}, new int[]{R.id.title, R.id.href});
    listView.setAdapter(adapter);
    loadMoreFooter = new LoadMoreFooter(this, listView, this);

    refreshLayout.setOnRefreshListener(this);
    listView.setOnItemClickListener(this);

    refreshLayout.setRefreshing(true);
    initData();

  }

  private Call call;

  private void initData() {
    new Thread() {
      @Override
      public void run() {
        Request request = new Request.Builder().url("http://dyn.ithome.com/jsonp/news/wappage?page=" + page).build();
        OkHttpClient client = new OkHttpClient();
        call = client.newCall(request);
        call.enqueue(new Callback() {
          @Override
          public void onFailure(Call call, IOException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onResponse(Call call, Response response) throws IOException {
            String body = response.body().string().replace("display(", "");
            body = body.substring(0, body.length() - 2);
            ITHome itHome = GsonUtil.getInstance().fromJson(body, ITHome.class);
            Element element = Jsoup.parse(itHome.getMsg());
            Elements titleEles = element.getElementsByTag("a");
            for (Element titleE : titleEles) {
              String title = titleE.getElementsByClass("title").text();
              String href = titleE.attr("href");
              if (!href.contains("http://") && !href.contains("https://")) {
                href = "http://wap.ithome.com" + href;
              }
              Map<String, Object> map = new HashMap<>();
              map.put("title", title);
              map.put("href", href);
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
          }
        });
      }
    }.start();
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Map<String, Object> map = (Map<String, Object>) parent.getItemAtPosition(position);
    String topic_id = map.get("href").toString();
    CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
        .setToolbarColor(getResources().getColor(R.color.colorPrimary)).setShowTitle(true).addDefaultShareMenuItem().build();
    CustomTabActivityHelper.openCustomTab(
        this, customTabsIntent, Uri.parse(topic_id), new WebviewFallback());
  }

  @Override
  public void onRefresh() {
    page = 1;
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
