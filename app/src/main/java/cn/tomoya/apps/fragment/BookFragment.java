package cn.tomoya.apps.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.tomoya.apps.R;
import cn.tomoya.apps.activity.BookCatalogActivity;
import cn.tomoya.apps.model.book.LocalBook;
import cn.tomoya.apps.model.book.RemoteBook;
import cn.tomoya.apps.util.GsonUtil;
import cn.tomoya.apps.util.JsoupUtil;
import cn.tomoya.apps.util.SharedPreferencesUtil;
import cn.tomoya.apps.viewholder.MyBaseAdapter;
import cn.tomoya.apps.viewholder.MyBaseAdapterFilter;
import cn.tomoya.apps.viewholder.MyBaseFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by tomoya on 4/1/17.
 */

public class BookFragment extends MyBaseFragment implements Toolbar.OnMenuItemClickListener,
    SwipeRefreshLayout.OnRefreshListener,
    AdapterView.OnItemClickListener,
    AdapterView.OnItemLongClickListener {

  private Toolbar toolbar;
  private SwipeRefreshLayout refreshLayout;
  private ListView listView;
  private MyBaseAdapter<LocalBook> adapter;
  private MyBaseAdapterFilter<RemoteBook> alertAdapter;
  private List<RemoteBook> remoteBookData = new ArrayList<>();
  private List<LocalBook> localBookData = new ArrayList<>();
  private Handler handler = new Handler();
  private AlertDialog.Builder builder;
  private AlertDialog alertDialog;
  private ProgressDialog dialog;
  private boolean initData = false;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_book, container, false);
    toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    toolbar.setTitle(getString(R.string.title_book));
    toolbar.inflateMenu(R.menu.book);
    toolbar.setOnMenuItemClickListener(this);

    //初始化本地保存的书箱信息
    localBookData = SharedPreferencesUtil.getLocalBooks(getActivity());

    refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
    listView = (ListView) view.findViewById(R.id.list_view);
    adapter = new MyBaseAdapter<LocalBook>(getActivity(), localBookData) {
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(context, convertView, parent, R.layout.list_view_item_book, position);
        LocalBook localBook = _data.get(position);
        holder.setText(R.id.name, localBook.getName())
            .setText(R.id.lastChapter, localBook.getLastChapter());
        holder.setNetImage(R.id.cover, localBook.getCover());
        return holder.getConvertView();
      }
    };
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(this);
    listView.setOnItemLongClickListener(this);
    refreshLayout.setOnRefreshListener(this);

    builder = new AlertDialog.Builder(getActivity());
    builder.setMessage(R.string.type_book);
    View alertView = LayoutInflater.from(getActivity()).inflate(R.layout.alert_choose_book, null);
    final AutoCompleteTextView bookNameTv = (AutoCompleteTextView) alertView.findViewById(R.id.bookName);
    alertAdapter = new MyBaseAdapterFilter<RemoteBook>(getActivity(), remoteBookData) {
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(context, convertView, parent, R.layout.list_view_item_alert_choose_book, position);
        RemoteBook remoteBook = remoteBookData.get(position);
        holder.setText(R.id.name, remoteBook.getName())
            .setText(R.id.author, remoteBook.getAuthor())
            .setText(R.id.catalogUrl, remoteBook.getCatalog_url());
        return holder.getConvertView();
      }

      @Override
      protected CharSequence myConvertResultToString(Object resultValue) {
        RemoteBook remoteBook = (RemoteBook) resultValue;
        return remoteBook.getName() + " - " + remoteBook.getAuthor();
      }

      @Override
      protected ArrayList<RemoteBook> dealList(int count, String prefixString, ArrayList<RemoteBook> unfilteredValues, ArrayList<RemoteBook> newValues) {
        for (int i = 0; i < count; i++) {
          RemoteBook pc = unfilteredValues.get(i);
          if (pc != null) {
            if (pc.getName() != null && pc.getName().contains(prefixString)) {
              newValues.add(pc);
            } else if (pc.getAuthor() != null && pc.getAuthor().contains(prefixString)) {
              newValues.add(pc);
            }
          }
        }
        return newValues;
      }
    };
    bookNameTv.setAdapter(alertAdapter);
    builder.setView(alertView);
    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        String bookName = bookNameTv.getText().toString();
        final String name = bookName.split(" - ")[0];
        final String author = bookName.split(" - ")[1];
        final RemoteBook remoteBook = findByNameAndAuthor(name, author);
        if (remoteBook == null) {
          Toast.makeText(getActivity(), R.string.alert_book_not_exist, Toast.LENGTH_SHORT).show();
        } else {
          handler.post(new Runnable() {
            @Override
            public void run() {
              refreshLayout.setRefreshing(true);
            }
          });
          JsoupUtil.asyncFetchCatalog(remoteBook.getCatalog_url(), new cn.tomoya.apps.util.Callback() {
            @Override
            public void output(Object result) {
              Map map = (Map) result;
              LocalBook localBook = new LocalBook();
              localBook.setAuthor(author);
              localBook.setName(name);
              localBook.setCatalogUrl(remoteBook.getCatalog_url());
              localBook.setCover(map.get("cover").toString());
              localBook.setLastChapter(map.get("lastChapter").toString());
              localBookData.add(localBook);

              SharedPreferencesUtil.saveLocalBooks(localBookData, getActivity());
              bookNameTv.setText("");
              onRefresh();
            }
          });
        }
      }
    });
    builder.setNegativeButton(android.R.string.no, null);
    alertDialog = builder.create();
    return view;
  }

  @Override
  public void fetchData() {
    refreshLayout.setRefreshing(true);
    localBookData.clear();
    localBookData.addAll(SharedPreferencesUtil.getLocalBooks(getActivity()));
    if (localBookData.size() == 0) {
      handler.post(new Runnable() {
        @Override
        public void run() {
          refreshLayout.setRefreshing(false);
        }
      });
    } else {
      handler.post(new Runnable() {
        @Override
        public void run() {
          adapter.notifyDataSetChanged();
          refreshLayout.setRefreshing(false);
        }
      });
    }
  }

  private void updateRemoteBooks() {
    remoteBookData.clear();
    Request request = new Request.Builder().url("http://git.oschina.net/tomoya/books/raw/master/books.json").build();
    OkHttpClient client = new OkHttpClient();
    Call call = client.newCall(request);
    call.enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        e.printStackTrace();
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        String body = response.body().string();
        remoteBookData.addAll((List<RemoteBook>) (GsonUtil.getInstance().fromJson(body, new TypeToken<List<RemoteBook>>() {
        }.getType())));
        initData = true;
        handler.post(new Runnable() {
          @Override
          public void run() {
            alertAdapter.notifyDataSetChanged();
            dialog.dismiss();
          }
        });
      }
    });
  }

  @Override
  public boolean onMenuItemClick(MenuItem item) {
    switch (item.getOrder()) {
      case 0:
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getString(R.string.loading));
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });
        dialog.show();
        updateRemoteBooks();
        return true;
      case 1:
        if (initData) {
          alertDialog.show();
          return true;
        } else {
          Toast.makeText(getActivity(), R.string.update_remote_books, Toast.LENGTH_SHORT).show();
          return false;
        }
    }
    return false;
  }

  @Override
  public void onRefresh() {
    fetchData();
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    LocalBook localBook = localBookData.get(position);
    Intent intent = new Intent(getActivity(), BookCatalogActivity.class);
    intent.putExtra("catalog_url", localBook.getCatalogUrl());
    intent.putExtra("name", localBook.getName());
    startActivity(intent);
  }

  private RemoteBook findByNameAndAuthor(String name, String author) {
    for (RemoteBook remoteBook : remoteBookData) {
      if (remoteBook.getName().equals(name) && remoteBook.getAuthor().equals(author)) {
        return remoteBook;
      }
    }
    return null;
  }

  @Override
  public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
    new AlertDialog.Builder(getActivity())
        .setIcon(R.drawable.ic_warning_red_24dp)
        .setTitle(R.string.system_warning)
        .setMessage(R.string.info_delete_book)
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            localBookData.remove(position);
            SharedPreferencesUtil.saveLocalBooks(localBookData, getActivity());
            onRefresh();
          }
        })
        .setNegativeButton(android.R.string.no, null)
        .show();
    return true;
  }
}
