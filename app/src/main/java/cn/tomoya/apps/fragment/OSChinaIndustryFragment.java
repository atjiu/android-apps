package cn.tomoya.apps.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
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
import cn.tomoya.apps.viewholder.MyBaseFragment;

/**
 * Created by tomoya on 4/3/17.
 */

public class OSChinaIndustryFragment extends MyBaseFragment implements
    SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener,
    DoubleClickBackToContentTopListener.IBackToContentTopView,
    LoadMoreFooter.OnLoadMoreListener {

  private List<Map<String, Object>> data = new ArrayList<>();
  private SwipeRefreshLayout refreshLayout;
  private ListView listView;
  private MyBaseAdapter<Map<String, Object>> adapter;
  private LoadMoreFooter loadMoreFooter;
  private int page = 1;

  @Override
  @Nullable
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_oschina, container, false);

    refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
    listView = (ListView) view.findViewById(R.id.list_view);
    adapter = new MyBaseAdapter<Map<String, Object>>(getActivity(), data) {
      List<Integer> positions = new ArrayList<>();

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(context, convertView, parent, R.layout.list_view_item_osc, position);
        Map<String, Object> map = data.get(position);
        holder.setText(R.id.title, map.get("title").toString())
            .setText(R.id.desc, map.get("desc").toString())
            .setText(R.id.replyCount, map.get("replyCount").toString())
            .setText(R.id.time, map.get("time").toString());
        holder.setNetImage(R.id.avatar, map.get("avatar").toString());
        holder.getView(R.id.readFirstImg).setVisibility(View.GONE);
        if (map.get("readFirstImg") != null) {
          positions.add(holder.getPosition());
        } else {
          positions.remove((Integer) holder.getPosition());
        }
        if (positions.contains(holder.getPosition())) {
          holder.setNetImage(R.id.readFirstImg, map.get("readFirstImg").toString());
          holder.getView(R.id.readFirstImg).setVisibility(View.VISIBLE);
        }
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

  //handler 处理返回的请求结果
  Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case 1: {
          if (refreshLayout.isRefreshing()) refreshLayout.setRefreshing(false);
          loadMoreFooter.setState(LoadMoreFooter.STATE_ENDLESS);
          adapter.notifyDataSetChanged();
        }
      }
    }
  };

  @Override
  public void fetchData() {
    new Thread() {
      @Override
      public void run() {
        try {
          Document document = Jsoup
              .connect("https://www.oschina.net/action/ajax/get_more_news_list?newsType=industry&p=" + page)
              .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.98 Safari/537.36")
              .get();
          Element body = document.body();
          Elements itemEles = body.getElementsByClass("item");
          for (Element itemE : itemEles) {
            String title = itemE.getElementsByClass("title").first().text();
            String desc = itemE.getElementsByClass("summary").first().text();
            String href = itemE.getElementsByClass("title").first().attr("href");
            String avatar = itemE.getElementsByClass("avatar").first().attr("src");
            String time = itemE.getElementsByClass("mr").first().text();
            String replyCount = itemE.getElementsByClass("mr").last().text();
            String readFirstImg = null;
            if (itemE.getElementsByClass("small").size() > 0) {
              readFirstImg = itemE.getElementsByClass("small").first().attr("src");
            }
            if (!href.contains("http://") && !href.contains("https://")) {
              href = "https://www.oschina.net" + href;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("title", title);
            map.put("desc", desc);
            map.put("href", href);
            map.put("avatar", avatar);
            map.put("time", time);
            map.put("replyCount", replyCount);
            map.put("readFirstImg", readFirstImg);
            data.add(map);
          }
          handler.sendEmptyMessage(1);
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
        .setToolbarColor(getResources().getColor(R.color.colorPrimary))        .setShowTitle(true)        .addDefaultShareMenuItem()        .build();
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