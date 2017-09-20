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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.tomoya.apps.R;
import cn.tomoya.apps.activity.customtab.CustomTabActivityHelper;
import cn.tomoya.apps.activity.customtab.WebviewFallback;
import cn.tomoya.apps.util.Callback;
import cn.tomoya.apps.util.FormatUtil;
import cn.tomoya.apps.util.GsonUtil;
import cn.tomoya.apps.util.OkHttpUtil;
import cn.tomoya.apps.viewholder.LoadMoreFooter;
import cn.tomoya.apps.viewholder.MyBaseAdapter;
import cn.tomoya.apps.viewholder.MyBaseFragment;

/**
 * Created by liygh on 2017/4/8.
 */

public class TencentHuaTiFragment extends MyBaseFragment implements
    SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener,
    LoadMoreFooter.OnLoadMoreListener {

  private List<Map<String, Object>> data = new ArrayList<>();
  private SwipeRefreshLayout refreshLayout;
  private ListView listView;
  private MyBaseAdapter<Map<String, Object>> adapter;
  private LoadMoreFooter loadMoreFooter;
  private Handler handler = new Handler();

  @Override
  @Nullable
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_tencent_huati, container, false);

    refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
    listView = (ListView) view.findViewById(R.id.list_view);
    adapter = new MyBaseAdapter<Map<String, Object>>(getActivity(), data) {
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(context, convertView, parent, R.layout.list_view_item_tencent_huati, position);
        Map<String, Object> map = data.get(position);
        holder.setText(R.id.title, map.get("n_title").toString())
            .setText(R.id.desc, map.get("n_describe").toString())
            .setText(R.id.author, map.get("n_use_user").toString())
            .setText(R.id.time, FormatUtil.getRelativeTimeSpanString(
                FormatUtil.string2Date(map.get("n_pushtime").toString(), FormatUtil.DATETIME)
            ));
        holder.setNetImage(R.id.thumbnails, map.get("n_pic").toString());
        return holder.getConvertView();
      }
    };
    listView.setAdapter(adapter);
    loadMoreFooter = new LoadMoreFooter(getActivity(), listView, this);

    refreshLayout.setOnRefreshListener(this);
    listView.setOnItemClickListener(this);
    refreshLayout.setRefreshing(true);

    return view;
  }

  @Override
  public void fetchData() {
    OkHttpUtil.fetchData(getActivity(), "http://xw.qq.com/iphone/m/view/shipeiList.js", true, new Callback() {
      @Override
      public void output(Object result) {
        String body = (String) result;
        body = body.substring(9, body.lastIndexOf(")"));
        System.out.println(body);
        Map map = GsonUtil.getInstance().fromJson(body, new TypeToken<HashMap>() {
        }.getType());
        data.addAll((Collection<? extends Map<String, Object>>) map.get("newlist"));
        handler.post(new Runnable() {
          @Override
          public void run() {
            adapter.notifyDataSetChanged();
            loadMoreFooter.setState(LoadMoreFooter.STATE_FINISHED);
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
        getActivity(), customTabsIntent, Uri.parse(map.get("mobile_url").toString()), new WebviewFallback());
  }

  @Override
  public void onRefresh() {
    data.clear();
    fetchData();
  }

  @Override
  public void onLoadMore() {
    loadMoreFooter.setState(LoadMoreFooter.STATE_LOADING);
    fetchData();
  }

}
