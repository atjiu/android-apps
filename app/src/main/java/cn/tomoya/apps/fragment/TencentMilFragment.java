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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.tomoya.apps.R;
import cn.tomoya.apps.activity.customtab.CustomTabActivityHelper;
import cn.tomoya.apps.activity.customtab.WebviewFallback;
import cn.tomoya.apps.listener.DoubleClickBackToContentTopListener;
import cn.tomoya.apps.model.tencent.Ids;
import cn.tomoya.apps.model.tencent.NewsIds;
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

public class TencentMilFragment extends MyBaseFragment implements
    SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener,
    DoubleClickBackToContentTopListener.IBackToContentTopView,
    LoadMoreFooter.OnLoadMoreListener {

  private List<Map<String, Object>> data = new ArrayList<>();
  private SwipeRefreshLayout refreshLayout;
  private ListView listView;
  private MyBaseAdapter<Map<String, Object>> adapter;
  private LoadMoreFooter loadMoreFooter;
  private List<Ids> idsList = new ArrayList<>();
  private int page = 1, size = 20;
  private Handler handler = new Handler();
  private boolean isFirstRequest = true;

  @Override
  @Nullable
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_tencent_mil, container, false);

    refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
    listView = (ListView) view.findViewById(R.id.list_view);
    adapter = new MyBaseAdapter<Map<String, Object>>(getActivity(), data) {
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(context, convertView, parent, R.layout.list_view_item_tencent_news, position);
        Map<String, Object> map = data.get(position);
        holder.setText(R.id.title, map.get("title").toString())
            .setText(R.id.abstract_content, map.get("abstract").toString())
            .setText(R.id.time, FormatUtil.getRelativeTimeSpanString(
                new Date((long) (((double) map.get("timestamp")) * 1000))
            ));
        holder.setNetImage(R.id.thumbnails, ((List) map.get("thumbnails_qqnews")).get(0).toString());
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
    String idsUrl = "http://xw.qq.com/service/api/proxy?key=Xw@2017Mmd&charset=GBK&url=http://openapi.inews.qq.com/getQQNewsIndexAndItems?chlid=news_news_mil&refer=mobilewwwqqcom&otype=json";
    OkHttpUtil.fetchData(getActivity(), idsUrl, isFirstRequest, new Callback() {
      @Override
      public void output(Object result) {
        if (result != null) {
          isFirstRequest = false;
          NewsIds newsIds = GsonUtil.getInstance().fromJson((String) result, NewsIds.class);
          idsList.addAll(newsIds.getIdlist().get(0).getIds());
        }
        StringBuffer idsStr = new StringBuffer();
        for (int i = (page - 1) * size; i < page * size; i++) {
          if (i < idsList.size()) {
            idsStr.append(idsList.get(i).getId()).append(",");
          }
        }
        if (idsStr.length() > 0) {
          idsStr.deleteCharAt(idsStr.length() - 1);
          String contentUrl = "http://xw.qq.com/service/api/proxy?key=Xw@2017Mmd&charset=GBK&refer=mobilewwwqqcom&otype=json&url=http://openapi.inews.qq.com/getQQNewsNormalContent?ids=" + idsStr.toString();
          OkHttpUtil.fetchData(getActivity(), contentUrl, true, new Callback() {
            @Override
            public void output(Object result) {
              Map<String, Object> map = GsonUtil.getInstance().fromJson((String) result, new TypeToken<HashMap<String, Object>>() {
              }.getType());
              data.addAll((Collection<? extends Map<String, Object>>) map.get("newslist"));
              handler.post(new Runnable() {
                @Override
                public void run() {
                  adapter.notifyDataSetChanged();
                  if (data.size() < idsList.size()) {
                    loadMoreFooter.setState(LoadMoreFooter.STATE_ENDLESS);
                  } else {
                    loadMoreFooter.setState(LoadMoreFooter.STATE_FINISHED);
                  }
                  refreshLayout.setRefreshing(false);
                }
              });
            }
          });
        }
      }
    });
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Map<String, Object> map = data.get(position);
    String topic_id = map.get("url").toString();
    CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
        .setToolbarColor(getResources().getColor(R.color.colorPrimary)).setShowTitle(true).addDefaultShareMenuItem().build();
    CustomTabActivityHelper.openCustomTab(
        getActivity(), customTabsIntent, Uri.parse(topic_id), new WebviewFallback());
  }

  @Override
  public void onRefresh() {
    page = 1;
    data.clear();
    fetchData();
  }

  @Override
  public void onLoadMore() {
    page++;
    loadMoreFooter.setState(LoadMoreFooter.STATE_LOADING);
    fetchData();
  }

  @Override
  public void backToContentTop() {
    listView.setSelection(0);
  }

}

