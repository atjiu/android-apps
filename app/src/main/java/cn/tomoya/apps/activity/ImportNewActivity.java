package cn.tomoya.apps.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

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
import cn.tomoya.apps.util.Callback;
import cn.tomoya.apps.util.JsoupUtil;
import cn.tomoya.apps.viewholder.MyRecylerViewAdapter;

/**
 * Created by tomoya on 4/11/17.
 */

public class ImportNewActivity extends BaseActivity implements
    DoubleClickBackToContentTopListener.IBackToContentTopView,
    SwipeRefreshLayout.OnRefreshListener,
    MyRecylerViewAdapter.OnItemClickListener {

  private Toolbar toolbar;
  private RecyclerView recylerView;
  private SwipeRefreshLayout refreshLayout;
  private List<Map<String, Object>> data = new ArrayList<>();
  private MyRecylerViewAdapter<Map<String, Object>> adapter;
  private int page = 1;
  private boolean loading = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_importnew);

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle(R.string.importnew);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ImportNewActivity.this.finish();
      }
    });
    toolbar.setOnClickListener(new DoubleClickBackToContentTopListener(this));

    refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
    refreshLayout.setOnRefreshListener(this);
    recylerView = (RecyclerView) findViewById(R.id.recylerView);
    recylerView.setLayoutManager(new LinearLayoutManager(this));
    recylerView.addItemDecoration(new MyRecylerViewAdapter.DividerItemDecoration(this, MyRecylerViewAdapter.DividerItemDecoration.VERTICAL_LIST));
    adapter = new MyRecylerViewAdapter<Map<String, Object>>(this, data) {
      @Override
      public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
          case -1:
            return new TypeViewHolder(inflater.inflate(R.layout.recylerview_footer_load_more, parent, false)) {
              @Override
              public void bindHolder(Map<String, Object> model) {

              }
            };
          case 0:
            return new TypeViewHolder(inflater.inflate(R.layout.list_view_item_importnew, parent, false)) {
              @Override
              public void bindHolder(Map<String, Object> model) {
                this.setText(R.id.title, model.get("title").toString())
                    .setText(R.id.desc, model.get("desc").toString())
                    .setText(R.id.time, model.get("time").toString())
                    .setText(R.id.catagray, model.get("catagray").toString())
                    .setText(R.id.replyCount, model.get("replyCount").toString());
                this.setNetImage(R.id.img, model.get("img").toString());
              }
            };
          default:
            return null;
        }
      }

      @Override
      public int getItemViewType(int position) {
        if (super.list.get(position) == null) return -1;
        return 0;
      }
    };

    recylerView.setAdapter(adapter);
    adapter.setOnItemClickListener(this);
    recylerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int totalItemCount = layoutManager.getItemCount();
        int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
        if (!loading && totalItemCount < lastVisibleItem + 3) {
          page++;
          initData();
          loading = true;
        }
      }
    });
    refreshLayout.setRefreshing(true);
    initData();

  }

  private void initData() {
    JsoupUtil.fetchBody("http://www.importnew.com/all-posts/page/" + page, new Callback() {
      @Override
      public void output(Object result) {
        Element body = (Element) result;
        Element archiveE = body.getElementById("archive");
        Elements postEles = archiveE.getElementsByClass("post");
        List<Map<String, Object>> _list = new ArrayList<>();
        for (Element postE : postEles) {
          String img = postE.getElementsByClass("post-thumb").first().getElementsByTag("img").first().attr("src");
          String href = postE.getElementsByClass("meta-title").first().attr("href");
          String title = postE.getElementsByClass("meta-title").first().text();
          String desc = postE.getElementsByClass("excerpt").first().text();
          String time = postE.getElementsByClass("post-meta").first().getElementsByTag("p").first().ownText().trim().replace("|", "");
          String catagray = postE.getElementsByClass("post-meta").first().getElementsByTag("p").first().getElementsByTag("a").get(1).text();
          String replyCount = postE.getElementsByClass("post-meta").first().getElementsByTag("p").first().getElementsByTag("a").last().text().replace("条评论", "").trim();
          Map map = new HashMap();
          map.put("img", img);
          map.put("href", href);
          map.put("title", title);
          map.put("desc", desc);
          map.put("time", time);
          map.put("catagray", catagray);
          map.put("replyCount", replyCount);
          _list.add(map);
        }
        if (page > 1) {
          data.remove(data.size() - 1);
        }
        data.addAll(_list);
        data.add(data.size(), null);
        loading = false;
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            refreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
          }
        });
      }
    });
  }

  @Override
  public void backToContentTop() {
    recylerView.smoothScrollToPosition(0);
  }

  @Override
  public void onRefresh() {
    page = 1;
    data.clear();
    initData();
  }

  @Override
  public void onItemClick(View view, int position) {
    Map map = data.get(position);
    CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
        .setToolbarColor(getResources().getColor(R.color.colorPrimary)).setShowTitle(true).addDefaultShareMenuItem().build();
    CustomTabActivityHelper.openCustomTab(
        this, customTabsIntent, Uri.parse(map.get("href").toString()), new WebviewFallback());
  }

  @Override
  public void onItemLongClick(View view, int position) {

  }
}
