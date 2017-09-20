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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.tomoya.apps.R;
import cn.tomoya.apps.activity.customtab.CustomTabActivityHelper;
import cn.tomoya.apps.activity.customtab.WebviewFallback;
import cn.tomoya.apps.listener.DoubleClickBackToContentTopListener;
import cn.tomoya.apps.model.zhihu_daily.Story;
import cn.tomoya.apps.model.zhihu_daily.ZhiHuDaily;
import cn.tomoya.apps.util.DateUtil;
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

public class ZhiHuDailyActivity extends BaseActivity implements
    SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener,
    DoubleClickBackToContentTopListener.IBackToContentTopView,
    LoadMoreFooter.OnLoadMoreListener {

  private Toolbar toolbar;
  private ListView listView;
  private SwipeRefreshLayout refreshLayout;
  private List<Story> data = new ArrayList<>();
  private MyBaseAdapter<Story> adapter;
  private LoadMoreFooter loadMoreFooter;
  private String date = DateUtil.formatDate(DateUtil.getDateAfter(new Date(), 1), DateUtil.DATE2);
  private boolean first = true;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_zhihu_daily);

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle(R.string.zhihu_daily);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ZhiHuDailyActivity.this.finish();
      }
    });
    toolbar.setOnClickListener(new DoubleClickBackToContentTopListener(this));

    listView = (ListView) findViewById(R.id.list_view);
    refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
    adapter = new MyBaseAdapter<Story>(this, data) {
      List<Integer> positions = new ArrayList<>();

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(context, convertView, parent, R.layout.list_view_item_zhihudaily, position);
        Story story = _data.get(position);
        holder.setText(R.id.title, story.getTitle());
        if (story.getImages().size() > 0) {
          positions.add(holder.getPosition());
        } else {
          positions.remove((Integer) holder.getPosition());
        }
        if (positions.contains(holder.getPosition())) {
          holder.setNetImage(R.id.image, story.getImages().get(0));
        }
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
    Request request = new Request.Builder().url("https://news-at.zhihu.com/api/4/news/before/" + date).build();
    OkHttpClient client = new OkHttpClient();
    call = client.newCall(request);
    call.enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        e.printStackTrace();
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        String body = response.body().string();
        ZhiHuDaily zhiHuDaily = GsonUtil.getInstance().fromJson(body, ZhiHuDaily.class);
        data.addAll(zhiHuDaily.getStories());
        if (first) {
          first = false;
          Date currentDate = DateUtil.string2Date(date, DateUtil.DATE2);
          date = DateUtil.formatDate(DateUtil.getDateBefore(currentDate, 1), DateUtil.DATE2);
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

  @Override
  public void onRefresh() {
    date = DateUtil.formatDate(DateUtil.getDateAfter(new Date(), 1), DateUtil.DATE2);
    data.clear();
    initData();
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Story story = data.get(position);
    CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
        .setToolbarColor(getResources().getColor(R.color.colorPrimary)).setShowTitle(true).addDefaultShareMenuItem().build();
    CustomTabActivityHelper.openCustomTab(
        this, customTabsIntent, Uri.parse("https://daily.zhihu.com/story/" + story.getId()), new WebviewFallback());
  }

  @Override
  public void onLoadMore() {
    Date currentDate = DateUtil.string2Date(date, DateUtil.DATE2);
    date = DateUtil.formatDate(DateUtil.getDateBefore(currentDate, 1), DateUtil.DATE2);
    loadMoreFooter.setState(LoadMoreFooter.STATE_LOADING);
    initData();
  }

  @Override
  public void backToContentTop() {
    listView.setSelection(0);
  }

}