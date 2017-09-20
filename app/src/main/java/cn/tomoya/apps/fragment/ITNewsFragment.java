package cn.tomoya.apps.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.tomoya.apps.R;
import cn.tomoya.apps.activity.CNodeActivity;
import cn.tomoya.apps.activity.ITHomeActivity;
import cn.tomoya.apps.activity.ImportNewActivity;
import cn.tomoya.apps.activity.OSChinaActivity;
import cn.tomoya.apps.activity.SegmentFaultActivity;
import cn.tomoya.apps.activity.TouTiaoIOActivity;
import cn.tomoya.apps.activity.TuiCoolActivity;
import cn.tomoya.apps.activity.V2exActivity;
import cn.tomoya.apps.activity.W3cplusActivity;
import cn.tomoya.apps.activity.WeatherActivity;
import cn.tomoya.apps.activity.ZhiHuDailyActivity;
import cn.tomoya.apps.viewholder.MyBaseAdapter;
import cn.tomoya.apps.viewholder.MyBaseFragment;

/**
 * Created by liygh on 2017/4/8.
 */

public class ITNewsFragment extends MyBaseFragment implements AdapterView.OnItemClickListener, Toolbar.OnMenuItemClickListener {

  private ListView listView;
  private List<Map<String, Object>> data = new ArrayList<>();
  private Handler handler = new Handler();
  private MyBaseAdapter<Map<String, Object>> adapter;

  @Override
  public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    fetchData();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_news_list, container, false);
    listView = (ListView) view.findViewById(R.id.list_view);
    listView.setAdapter(adapter = new MyBaseAdapter<Map<String, Object>>(getActivity(), data) {
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(super.context, convertView, parent, R.layout.list_view_item_itnews, position);
        Map<String, Object> map = _data.get(position);
        holder.setText(R.id.name, map.get("name").toString());
        holder.setDrawableImage(R.id.logo, (Integer) map.get("logo"));
        return holder.getConvertView();
      }
    });
    listView.setOnItemClickListener(this);
    return view;
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Map<String, Object> map = data.get(position);
    String name = map.get("name").toString();
    if (name.equals(getString(R.string.cnode))) {
      Intent intent = new Intent(getActivity(), CNodeActivity.class);
      startActivity(intent);
    } else if (name.equals(getString(R.string.oschina))) {
      Intent intent = new Intent(getActivity(), OSChinaActivity.class);
      startActivity(intent);
    } else if (name.equals(getString(R.string.toutiaoio))) {
      Intent intent = new Intent(getActivity(), TouTiaoIOActivity.class);
      startActivity(intent);
    } else if (name.equals(getString(R.string.tuicool))) {
      Intent intent = new Intent(getActivity(), TuiCoolActivity.class);
      startActivity(intent);
    } else if (name.equals(getString(R.string.segmentfault))) {
      Intent intent = new Intent(getActivity(), SegmentFaultActivity.class);
      startActivity(intent);
    } else if (name.equals(getString(R.string.ithome))) {
      Intent intent = new Intent(getActivity(), ITHomeActivity.class);
      startActivity(intent);
    } else if (name.equals(getString(R.string.v2ex))) {
      Intent intent = new Intent(getActivity(), V2exActivity.class);
      startActivity(intent);
    } else if (name.equals(getString(R.string.zhihu_daily))) {
      Intent intent = new Intent(getActivity(), ZhiHuDailyActivity.class);
      startActivity(intent);
    } else if (name.equals(getString(R.string.w3cplus))) {
      Intent intent = new Intent(getActivity(), W3cplusActivity.class);
      startActivity(intent);
    } else if (name.equals(getString(R.string.importnew))) {
      Intent intent = new Intent(getActivity(), ImportNewActivity.class);
      startActivity(intent);
    }
  }

  @Override
  public void fetchData() {
    data.clear();

    Map cnode_map = new HashMap();
    cnode_map.put("name", getString(R.string.cnode));
    cnode_map.put("logo", R.drawable.cnode);
    data.add(cnode_map);

    Map oschina_map = new HashMap();
    oschina_map.put("name", getString(R.string.oschina));
    oschina_map.put("logo", R.drawable.oschina);
    data.add(oschina_map);

    Map toutiaoio_map = new HashMap();
    toutiaoio_map.put("name", getString(R.string.toutiaoio));
    toutiaoio_map.put("logo", R.drawable.toutiaoio);
    data.add(toutiaoio_map);

    Map tuicool_map = new HashMap();
    tuicool_map.put("name", getString(R.string.tuicool));
    tuicool_map.put("logo", R.drawable.tuicool);
    data.add(tuicool_map);

    Map segmentfault_map = new HashMap();
    segmentfault_map.put("name", getString(R.string.segmentfault));
    segmentfault_map.put("logo", R.drawable.segmentfault);
    data.add(segmentfault_map);

    Map ithome_map = new HashMap();
    ithome_map.put("name", getString(R.string.ithome));
    ithome_map.put("logo", R.drawable.ithome);
    data.add(ithome_map);

    Map v2ex_map = new HashMap();
    v2ex_map.put("name", getString(R.string.v2ex));
    v2ex_map.put("logo", R.drawable.v2ex);
    data.add(v2ex_map);

    Map zhihu_daily_map = new HashMap();
    zhihu_daily_map.put("name", getString(R.string.zhihu_daily));
    zhihu_daily_map.put("logo", R.drawable.zhihudaily);
    data.add(zhihu_daily_map);

    Map w3cplus_map = new HashMap();
    w3cplus_map.put("name", getString(R.string.w3cplus));
    w3cplus_map.put("logo", R.drawable.w3cplus);
    data.add(w3cplus_map);

    Map importnew_map = new HashMap();
    importnew_map.put("name", getString(R.string.importnew));
    importnew_map.put("logo", R.drawable.importnew);
    data.add(importnew_map);

    handler.post(new Runnable() {
      @Override
      public void run() {
        adapter.notifyDataSetChanged();
      }
    });
  }

  @Override
  public boolean onMenuItemClick(MenuItem item) {
    Intent intent = new Intent(getActivity(), WeatherActivity.class);
    startActivity(intent);
    return false;
  }
}
