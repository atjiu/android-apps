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
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.tomoya.apps.R;
import cn.tomoya.apps.activity.customtab.CustomTabActivityHelper;
import cn.tomoya.apps.activity.customtab.WebviewFallback;
import cn.tomoya.apps.listener.DoubleClickBackToContentTopListener;
import cn.tomoya.apps.util.GsonUtil;
import cn.tomoya.apps.viewholder.LoadMoreFooter;
import cn.tomoya.apps.viewholder.MyBaseAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by tomoya on 3/25/17.
 */

public class TouTiaoComActivity extends BaseActivity implements
    SwipeRefreshLayout.OnRefreshListener,
    DoubleClickBackToContentTopListener.IBackToContentTopView,
    AdapterView.OnItemClickListener, LoadMoreFooter.OnLoadMoreListener {

  private List<Map<String, Object>> data = new ArrayList<>();
  private SwipeRefreshLayout refreshLayout;
  private ListView listView;
  private Toolbar toolbar;
  private MyBaseAdapter<Map<String, Object>> adapter;
  private LoadMoreFooter loadMoreFooter;
  private String page = "";
  private boolean first = true;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_toutiaocom);

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle(R.string.toutiaocom);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        TouTiaoComActivity.this.finish();
      }
    });
    toolbar.setOnClickListener(new DoubleClickBackToContentTopListener(this));

    refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
    listView = (ListView) findViewById(R.id.list_view);
    adapter = new MyBaseAdapter<Map<String, Object>>(this, data) {
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(TouTiaoComActivity.this, convertView, parent, R.layout.list_view_item_toutiaocom, position);
        Map map = _data.get(position);
        holder.setText(R.id.title, "[" + map.get("article_genre").toString() + "] " + map.get("title").toString())
            .setText(R.id.href, "http://www.toutiao.com/a" + map.get("group_id").toString());
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

  private Call call;

  private void initData() {
    new Thread() {
      @Override
      public void run() {
        Request request = new Request.Builder().url("http://www.toutiao.com/api/pc/feed/?category=__all__&max_behot_time=" + page).build();
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
            Map<String, Object> toutiaocomMap = GsonUtil.getInstance().fromJson(body, Map.class);
            data.addAll((List<Map<String, Object>>) toutiaocomMap.get("data"));
            page = ((Map) toutiaocomMap.get("next")).get("max_behot_time").toString();
            if (first) {
              first = false;
              initData();
            } else {
              runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  if (refreshLayout.isRefreshing()) refreshLayout.setRefreshing(false);
                  loadMoreFooter.setState(LoadMoreFooter.STATE_ENDLESS);
                  adapter.notifyDataSetChanged();
                }
              });
            }
          }
        });
      }
    }.start();
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Map<String, Object> map = data.get(position);
    String title = map.get("title").toString();
    CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
        .setToolbarColor(getResources().getColor(R.color.colorPrimary)).setShowTitle(true).addDefaultShareMenuItem().build();
    CustomTabActivityHelper.openCustomTab(
        this, customTabsIntent, Uri.parse("http://www.toutiao.com/a" + map.get("group_id").toString()), new WebviewFallback());
  }

  @Override
  public void onRefresh() {
    data.clear();
    initData();
  }

  @Override
  public void onLoadMore() {
    loadMoreFooter.setState(LoadMoreFooter.STATE_LOADING);
    initData();
  }

  @Override
  public void backToContentTop() {
    listView.setSelection(0);
  }
}
