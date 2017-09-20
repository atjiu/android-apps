package cn.tomoya.apps.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.tomoya.apps.R;
import cn.tomoya.apps.activity.customtab.CustomTabActivityHelper;
import cn.tomoya.apps.activity.customtab.WebviewFallback;
import cn.tomoya.apps.model.v2ex.V2ex;
import cn.tomoya.apps.util.FormatUtil;
import cn.tomoya.apps.util.GsonUtil;
import cn.tomoya.apps.viewholder.LoadMoreFooter;
import cn.tomoya.apps.viewholder.MyBaseAdapter;
import cn.tomoya.apps.viewholder.MyBaseFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by tomoya on 4/3/17.
 */

public class V2exLatestFragment extends MyBaseFragment implements
    SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener,
    LoadMoreFooter.OnLoadMoreListener {

  private List<V2ex> data = new ArrayList<>();
  private SwipeRefreshLayout refreshLayout;
  private ListView listView;
  private MyBaseAdapter<V2ex> adapter;
  private LoadMoreFooter loadMoreFooter;
  private Handler handler = new Handler();

  @Override
  @Nullable
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_v2ex, container, false);

    refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
    listView = (ListView) view.findViewById(R.id.list_view);
    adapter = new MyBaseAdapter<V2ex>(getActivity(), data) {
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(context, convertView, parent, R.layout.list_view_item_v2ex, position);
        V2ex v2ex = _data.get(position);
        holder.setText(R.id.title, v2ex.getTitle())
            .setText(R.id.replyCount, String.valueOf(v2ex.getReplies()))
            .setText(R.id.time, FormatUtil.getRelativeTimeSpanString(new Date(v2ex.getCreated() * 1000)));
        holder.setNetImage(R.id.avatar, v2ex.getMember().getAvatar_normal().replace("//", "https://"));
        return holder.getConvertView();
      }
    };
    listView.setAdapter(adapter);
    loadMoreFooter = new LoadMoreFooter(getActivity(), listView, this);

    refreshLayout.setOnRefreshListener(this);
    listView.setOnItemClickListener(this);

    return view;
  }

  private Call call;

  @Override
  public void fetchData() {
    refreshLayout.setRefreshing(true);
    Request request = new Request.Builder().url("https://www.v2ex.com/api/topics/latest.json").build();
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
        List<V2ex> v2exs = GsonUtil.getInstance().fromJson(body, new TypeToken<List<V2ex>>() {
        }.getType());
        data.addAll(v2exs);
        handler.post(new Runnable() {
          @Override
          public void run() {
            if (refreshLayout.isRefreshing()) refreshLayout.setRefreshing(false);
            loadMoreFooter.setState(LoadMoreFooter.STATE_FINISHED);
            adapter.notifyDataSetChanged();
          }
        });
      }
    });
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    V2ex v2ex = data.get(position);
    CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
        .setToolbarColor(getResources().getColor(R.color.colorPrimary)).setShowTitle(true).addDefaultShareMenuItem().build();
    CustomTabActivityHelper.openCustomTab(
        getActivity(), customTabsIntent, Uri.parse(v2ex.getUrl()), new WebviewFallback());
  }

  @Override
  public void onRefresh() {
    data.clear();
    fetchData();
  }

  @Override
  public void onLoadMore() {

  }

}