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

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.tomoya.apps.R;
import cn.tomoya.apps.activity.customtab.CustomTabActivityHelper;
import cn.tomoya.apps.activity.customtab.WebviewFallback;
import cn.tomoya.apps.util.JsoupUtil;
import cn.tomoya.apps.viewholder.LoadMoreFooter;
import cn.tomoya.apps.viewholder.MyBaseAdapter;
import cn.tomoya.apps.viewholder.MyBaseFragment;

/**
 * Created by tomoya on 4/12/17.
 */

public class SegmentFaultHottestFragment extends MyBaseFragment implements
    SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener,
    LoadMoreFooter.OnLoadMoreListener {

  private ListView listView;
  private SwipeRefreshLayout refreshLayout;
  private List<Map<String, String>> data = new ArrayList<>();
  private MyBaseAdapter<Map<String, String>> adapter;
  private LoadMoreFooter loadMoreFooter;
  private int page = 1;
  private Handler handler = new Handler();


  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_segmentfault, container, false);

    listView = (ListView) view.findViewById(R.id.list_view);
    refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
    adapter = new MyBaseAdapter<Map<String, String>>(getActivity(), data) {
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(context, convertView, parent, R.layout.list_view_item_segmentfault, position);
        Map<String, String> map = data.get(position);
        holder.setText(R.id.title, map.get("title"))
            .setText(R.id.desc, map.get("desc"))
            .setText(R.id.author, map.get("author"))
            .setText(R.id.catagray, map.get("catagray"))
            .setText(R.id.rank, map.get("rank"))
            .setText(R.id.time, map.get("time"));
        holder.setNetImage(R.id.avatar, map.get("avatar"));
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
    JsoupUtil.fetchBody("https://segmentfault.com/blogs/hottest?page=" + page, new cn.tomoya.apps.util.Callback() {
      @Override
      public void output(Object result) {
        Element body = (Element) result;
        Elements streamEles = body.getElementsByClass("stream-list__item");
        for (Element streamE : streamEles) {
          String rank = streamE.getElementsByClass("blog-rank").first().getElementsByClass("votes").first().ownText() + "/" +
              streamE.getElementsByClass("blog-rank").first().getElementsByClass("views").first().ownText();
          String title = streamE.getElementsByClass("title").first().text();
          String href = "https://segmentfault.com" + streamE.getElementsByClass("title").first().getElementsByTag("a").first().attr("href");
          String desc = streamE.getElementsByClass("excerpt").first().text();
          String avatar = streamE.getElementsByClass("author").first().getElementsByTag("img").first().attr("src");
          String author = streamE.getElementsByClass("author").first().getElementsByTag("a").first().ownText();
          String catagray = streamE.getElementsByClass("author").first().getElementsByTag("a").last().ownText();
          String time = streamE.getElementsByClass("author").first().getElementsByTag("li").last().ownText().replace("发布于", "").trim();
          Map<String, String> map = new HashMap<>();
          map.put("rank", rank);
          map.put("title", title);
          map.put("href", href);
          map.put("desc", desc);
          map.put("avatar", avatar);
          map.put("author", author);
          map.put("catagray", catagray);
          map.put("time", time);
          data.add(map);
        }
        handler.post(new Runnable() {
          @Override
          public void run() {
            refreshLayout.setRefreshing(false);
            loadMoreFooter.setState(LoadMoreFooter.STATE_ENDLESS);
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
    fetchData();
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Map<String, String> map = data.get(position);
    CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
        .setToolbarColor(getResources().getColor(R.color.colorPrimary))        .setShowTitle(true)        .addDefaultShareMenuItem()        .build();
    CustomTabActivityHelper.openCustomTab(
        getActivity(), customTabsIntent, Uri.parse(map.get("href")), new WebviewFallback());
  }

  @Override
  public void onLoadMore() {
    page++;
    loadMoreFooter.setState(LoadMoreFooter.STATE_LOADING);
    fetchData();
  }
}
