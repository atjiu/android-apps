package cn.tomoya.apps.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cn.tomoya.apps.R;
import cn.tomoya.apps.listener.DoubleClickBackToContentTopListener;
import cn.tomoya.apps.model.book.LocalBook;
import cn.tomoya.apps.util.Callback;
import cn.tomoya.apps.util.FormatUtil;
import cn.tomoya.apps.util.JsoupUtil;
import cn.tomoya.apps.util.SharedPreferencesUtil;
import cn.tomoya.apps.viewholder.MyBaseAdapter;

/**
 * Created by tomoya on 4/6/17.
 */

public class BookCatalogActivity extends BaseActivity implements
    DoubleClickBackToContentTopListener.IBackToContentTopView,
    SwipeRefreshLayout.OnRefreshListener,
    AdapterView.OnItemClickListener,
    Toolbar.OnMenuItemClickListener {

  private Toolbar toolbar;
  private SwipeRefreshLayout refreshLayout;
  private ListView listView;
  private MyBaseAdapter<Map<String, Object>> adapter;
  private List<Map<String, Object>> data = new ArrayList<>();
  private String catalogUrl, name;
  private List<LocalBook> localBookData = new ArrayList<>();
  private LocalBook localBook;
  private boolean order = false;//倒序 true 正序
  private int catalogIndex = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_book_catalog);
    catalogUrl = getIntent().getStringExtra("catalog_url");
    name = getIntent().getStringExtra("name");

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle(name);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        BookCatalogActivity.this.finish();
      }
    });
    toolbar.setOnClickListener(new DoubleClickBackToContentTopListener(this));
    toolbar.inflateMenu(R.menu.read_book);
    toolbar.setOnMenuItemClickListener(this);

    refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
    listView = (ListView) findViewById(R.id.list_view);
    adapter = new MyBaseAdapter<Map<String, Object>>(this, data) {
      private List<Integer> positions = new ArrayList<>();

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(context, convertView, parent, R.layout.list_view_item_book_catalog, position);
        Map map = _data.get(position);
        ((TextView) holder.getView(R.id.catalog)).setTextColor(Color.BLACK);
        if (map.get("catalog").toString().equals(localBook.getCurrentReadCatalog())) {
          positions.add(holder.getPosition());
        } else {
          positions.remove((Integer) holder.getPosition());
        }
        if (positions.contains(holder.getPosition())) {
          ((TextView) holder.getView(R.id.catalog)).setTextColor(Color.RED);
        }
        holder.setText(R.id.catalog, map.get("catalog").toString())
            .setText(R.id.href, map.get("href").toString());
        return holder.getConvertView();
      }
    };
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(this);
    refreshLayout.setOnRefreshListener(this);
    refreshLayout.setRefreshing(true);
    //初始化本地保存的书箱信息
    localBookData = SharedPreferencesUtil.getLocalBooks(this);
    localBook = FormatUtil.findByName(localBookData, name);
    initData();
  }

  private void initData() {
    localBookData = SharedPreferencesUtil.getLocalBooks(this);
    JsoupUtil.asyncFetchCatalog(catalogUrl, new Callback() {
      @Override
      public void output(Object result) {
        Map map = (Map) result;
        //将菜单保存本地书箱信息里
        for (LocalBook book : localBookData) {
          if (book.getName().equals(name)) {
            book.setCatalogs((List<Map<String, Object>>) map.get("data"));
            break;
          }
        }
        SharedPreferencesUtil.saveLocalBooks(localBookData, BookCatalogActivity.this);
        data.addAll((List<Map<String, Object>>) map.get("data"));
        Collections.reverse(data);
        localBook = FormatUtil.findByName(localBookData, name);
        catalogIndex = FormatUtil.findByCatalog(localBook.getCatalogs(), localBook.getCurrentReadCatalog());
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            adapter.notifyDataSetChanged();
            if (localBook.getCurrentReadCatalog() != null)
              listView.setSelection(localBook.getCatalogs().size() - 1 - catalogIndex);
            refreshLayout.setRefreshing(false);
          }
        });
      }
    });
  }

  @Override
  public void backToContentTop() {
    listView.setSelection(0);
  }

  @Override
  public void onRefresh() {
    data.clear();
    initData();
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Map map = data.get(position);
    Intent intent = new Intent(this, ReadBookActivity.class);
    intent.putExtra("name", name);
    intent.putExtra("order", order);
    intent.putExtra("catalog", map.get("catalog").toString());
    intent.putExtra("href", map.get("href").toString());
    startActivity(intent);
  }

  @Override
  public boolean onMenuItemClick(MenuItem item) {
    order = !order;
    Collections.reverse(data);
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        adapter.notifyDataSetChanged();
        if (localBook.getCurrentReadCatalog() != null) {
          if (order) {
            listView.setSelection(catalogIndex);
          } else {
            listView.setSelection(localBook.getCatalogs().size() - 1 - catalogIndex);
          }
        }
      }
    });
    return true;
  }
}
