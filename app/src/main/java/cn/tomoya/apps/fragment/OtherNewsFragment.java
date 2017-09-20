package cn.tomoya.apps.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.tomoya.apps.R;
import cn.tomoya.apps.activity.NewsBrotherActivity;
import cn.tomoya.apps.activity.TencentNewsActivity;
import cn.tomoya.apps.activity.TouTiaoComActivity;
import cn.tomoya.apps.viewholder.MyBaseAdapter;
import cn.tomoya.apps.viewholder.MyBaseFragment;

/**
 * Created by liygh on 2017/4/8.
 */

public class OtherNewsFragment extends MyBaseFragment implements AdapterView.OnItemClickListener {

  private ListView listView;
  private List<Map<String, Object>> data = new ArrayList<>();
  private Handler handler = new Handler();
  private MyBaseAdapter<Map<String, Object>> adapter;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_news_list, container, false);
    listView = (ListView) view.findViewById(R.id.list_view);
    listView.setAdapter(adapter = new MyBaseAdapter<Map<String, Object>>(getActivity(), data) {
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(super.context, convertView, parent, R.layout.list_view_item_news, position);
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
    if (name.equals(getString(R.string.toutiaocom))) {
      Intent intent = new Intent(getActivity(), TouTiaoComActivity.class);
      startActivity(intent);
    } else if (name.equals(getString(R.string.tencentnews))) {
      Intent intent = new Intent(getActivity(), TencentNewsActivity.class);
      startActivity(intent);
    } else if (name.equals(getString(R.string.newsbrother))) {
      Intent intent = new Intent(getActivity(), NewsBrotherActivity.class);
      startActivity(intent);
    }
  }

  @Override
  public void fetchData() {
    data.clear();

//    Map toutiaocom_map = new HashMap();
//    toutiaocom_map.put("name", getString(R.string.toutiaocom));
//    toutiaocom_map.put("logo", R.drawable.toutiaocom);
//    data.add(toutiaocom_map);

    Map tencentnews_map = new HashMap();
    tencentnews_map.put("name", getString(R.string.tencentnews));
    tencentnews_map.put("logo", R.drawable.tencentnews);
    data.add(tencentnews_map);

    Map newsbrother_map = new HashMap();
    newsbrother_map.put("name", getString(R.string.newsbrother));
    newsbrother_map.put("logo", R.drawable.newsbrother);
    data.add(newsbrother_map);

    handler.post(new Runnable() {
      @Override
      public void run() {
        adapter.notifyDataSetChanged();
      }
    });
  }

}
