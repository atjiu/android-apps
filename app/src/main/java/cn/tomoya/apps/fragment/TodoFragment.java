package cn.tomoya.apps.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.tomoya.apps.R;
import cn.tomoya.apps.activity.WeatherActivity;
import cn.tomoya.apps.util.SharedPreferencesUtil;
import cn.tomoya.apps.viewholder.MyBaseFragment;

/**
 * Created by tomoya on 4/1/17.
 */

public class TodoFragment extends MyBaseFragment implements
    Toolbar.OnMenuItemClickListener, AdapterView.OnItemLongClickListener,
    SwipeRefreshLayout.OnRefreshListener {

  private Toolbar toolbar;
  private ListView listView;
  private ArrayAdapter<String> adapter;
  private SwipeRefreshLayout refreshLayout;
  private List<String> localTodoData = new ArrayList<>();
  private Handler handler = new Handler();

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_todo, container, false);
    toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    toolbar.setTitle(getString(R.string.title_todo));
    toolbar.inflateMenu(R.menu.todo);
    toolbar.setOnMenuItemClickListener(this);

    listView = (ListView) view.findViewById(R.id.list_view);
    refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
    adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, localTodoData);
    listView.setAdapter(adapter);
    listView.setOnItemLongClickListener(this);
    refreshLayout.setOnRefreshListener(this);

    return view;
  }

  @Override
  public void fetchData() {
    localTodoData.clear();
    refreshLayout.setRefreshing(true);
    localTodoData.addAll(SharedPreferencesUtil.getLocalTodos(getActivity()));
    Collections.reverse(localTodoData);
    handler.post(new Runnable() {
      @Override
      public void run() {
        adapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
      }
    });
  }

  @Override
  public boolean onMenuItemClick(MenuItem item) {
    switch (item.getOrder()) {
      case 0:
        Intent intent = new Intent(getActivity(), WeatherActivity.class);
        startActivity(intent);
        return true;
      case 1:
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View alertDialogView = LayoutInflater.from(getActivity()).inflate(R.layout.alert_add_todo, null);
        builder.setView(alertDialogView);
        final EditText contentEt = (EditText) alertDialogView.findViewById(R.id.contentEt);
        builder.setMessage(R.string.please_type);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            String todoContent = contentEt.getText().toString();
            if (todoContent.length() > 0) {
              localTodoData.add(todoContent);
              SharedPreferencesUtil.saveLocalTodos(localTodoData, getActivity());
              contentEt.setText("");
              fetchData();
            }
          }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
        return true;
      default:
        return false;
    }
  }

  @Override
  public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
    new android.app.AlertDialog.Builder(getActivity())
        .setIcon(R.drawable.ic_warning_red_24dp)
        .setTitle(R.string.system_warning)
        .setMessage(R.string.info_delete_todo)
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            localTodoData.remove(position);
            SharedPreferencesUtil.saveLocalTodos(localTodoData, getActivity());
            fetchData();
          }
        })
        .setNegativeButton(android.R.string.no, null)
        .show();
    return true;
  }

  @Override
  public void onRefresh() {
    fetchData();
  }
}
