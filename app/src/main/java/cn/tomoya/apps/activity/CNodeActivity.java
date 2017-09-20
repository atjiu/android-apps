package cn.tomoya.apps.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.tomoya.apps.R;
import cn.tomoya.apps.activity.customtab.CustomTabActivityHelper;
import cn.tomoya.apps.activity.customtab.WebviewFallback;
import cn.tomoya.apps.listener.DoubleClickBackToContentTopListener;
import cn.tomoya.apps.model.cnode.CNode;
import cn.tomoya.apps.model.cnode.Data;
import cn.tomoya.apps.util.FormatUtil;
import cn.tomoya.apps.util.GsonUtil;
import cn.tomoya.apps.viewholder.LoadMoreFooter;
import cn.tomoya.apps.viewholder.MyRecylerViewAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by tomoya on 3/25/17.
 */

public class CNodeActivity extends BaseActivity implements
    SwipeRefreshLayout.OnRefreshListener,
    MyRecylerViewAdapter.OnItemClickListener,
    DoubleClickBackToContentTopListener.IBackToContentTopView,
    LoadMoreFooter.OnLoadMoreListener {

  private Toolbar toolbar;
  private RecyclerView recylerView;
  private SwipeRefreshLayout refreshLayout;
  private List<Data> data = new ArrayList<>();
  private MyRecylerViewAdapter<Data> adapter;
  private int page = 1;
  private final static String TITLE_HREF = "https://cnodejs.org/topic/";
  private boolean loading = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_cnode);

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle(R.string.cnode);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        CNodeActivity.this.finish();
      }
    });
    toolbar.setOnClickListener(new DoubleClickBackToContentTopListener(this));

    recylerView = (RecyclerView) findViewById(R.id.recylerView);
    recylerView.setLayoutManager(new LinearLayoutManager(this));
    recylerView.addItemDecoration(new MyRecylerViewAdapter.DividerItemDecoration(this, MyRecylerViewAdapter.DividerItemDecoration.VERTICAL_LIST));
    refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
    adapter = new MyRecylerViewAdapter<Data>(this, data) {
      @Override
      public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
          case -1:
            return new TypeViewHolder(inflater.inflate(R.layout.recylerview_footer_load_more, parent, false)) {
              @Override
              public void bindHolder(Data model) {

              }
            };
          case 0:
            return new TypeViewHolder(inflater.inflate(R.layout.list_view_item_cnode, parent, false)) {
              @Override
              public void bindHolder(Data model) {
                this.setText(R.id.title, model.getTitle())
                    .setText(R.id.time, FormatUtil.getRelativeTimeSpanString(model.getCreate_at()))
                    .setText(R.id.replyCount, String.valueOf(model.getReply_count()));
                this.setNetImage(R.id.avatar, model.getAuthor().getAvatar_url());
              }
            };
        }
        return null;
      }

      @Override
      public int getItemViewType(int position) {
        if (super.list.get(position) == null) return -1;
        return 0;
      }
    };
    adapter.setOnItemClickListener(this);
    recylerView.setAdapter(adapter);
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
    refreshLayout.setOnRefreshListener(this);
    refreshLayout.setRefreshing(true);
    initData();

  }

  private Call call;

  private void initData() {
    Request request = new Request.Builder().url("https://cnodejs.org/api/v1/topics?page=" + page).build();
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
        CNode cNode = GsonUtil.getInstance().fromJson(body, CNode.class);
        if (page > 1) {
          data.remove(data.size() - 1);
        }
        data.addAll(cNode.getData());
        data.add(data.size(), null);
        loading = false;
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            if (refreshLayout.isRefreshing()) refreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
          }
        });
      }
    });
  }

  @Override
  public void onRefresh() {
    page = 1;
    data.clear();
    initData();
  }

  @Override
  public void onLoadMore() {
    page++;
    initData();
  }

  @Override
  public void backToContentTop() {
    recylerView.smoothScrollToPosition(0);
  }

  @Override
  public void onItemClick(View view, int position) {
    Data _data = data.get(position);
    String href = TITLE_HREF + _data.getId();
    CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
        .setToolbarColor(getResources().getColor(R.color.colorPrimary)).setShowTitle(true).addDefaultShareMenuItem().build();
    CustomTabActivityHelper.openCustomTab(
        this, customTabsIntent, Uri.parse(href), new WebviewFallback());
  }

  @Override
  public void onItemLongClick(View view, int position) {

  }
}