package cn.tomoya.apps.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import cn.tomoya.apps.R;
import cn.tomoya.apps.model.book.LocalBook;
import cn.tomoya.apps.util.Callback;
import cn.tomoya.apps.util.FormatUtil;
import cn.tomoya.apps.util.JsoupUtil;
import cn.tomoya.apps.util.SharedPreferencesUtil;

/**
 * Created by tomoya on 4/6/17.
 */

public class ReadBookActivity extends BaseActivity implements
    SwipeRefreshLayout.OnRefreshListener,
    View.OnClickListener {

  private Toolbar toolbar;
  private ScrollView scrollView;
  private SwipeRefreshLayout refreshLayout;
  private TextView contentTv;
  private Button prevBtn;
  private Button nextBtn;
  private List<LocalBook> localBookData = new ArrayList<>();
  private LocalBook localBook;
  private int catalogIndex;

  private String href, catalog, name;
  private boolean order;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_read_book);

    catalog = getIntent().getStringExtra("catalog");
    href = getIntent().getStringExtra("href");
    name = getIntent().getStringExtra("name");
    order = getIntent().getBooleanExtra("order", false);

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle(catalog);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ReadBookActivity.this.finish();
      }
    });
    scrollView = (ScrollView) findViewById(R.id.scroll_view);
    refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
    refreshLayout.setOnRefreshListener(this);
    contentTv = (TextView) findViewById(R.id.contentTv);
    prevBtn = (Button) findViewById(R.id.prevBtn);
    nextBtn = (Button) findViewById(R.id.nextBtn);
    prevBtn.setOnClickListener(this);
    nextBtn.setOnClickListener(this);
    //初始化本地保存的书箱信息
    localBookData = SharedPreferencesUtil.getLocalBooks(this);
    localBook = FormatUtil.findByName(localBookData, name);
    catalogIndex = FormatUtil.findByCatalog(localBook.getCatalogs(), catalog);
    initData();
  }

  private void initData() {
    refreshLayout.setRefreshing(true);
    JsoupUtil.fetchBody(href, new Callback() {
      @Override
      public void output(final Object result) {
        for (LocalBook book : localBookData) {
          if (book.getName().equals(name)) {
            book.setCurrentReadCatalog(catalog);
            break;
          }
        }
        SharedPreferencesUtil.saveLocalBooks(localBookData, ReadBookActivity.this);
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            System.out.println(result.toString());
            if (href.contains(JsoupUtil.BOLUOXS)) {
              String content = ((Element) result).getElementById("book_text").html();
              contentTv.setText(Html.fromHtml(content));
            } else if (href.contains(JsoupUtil.SANJIANGGE)) {
              String content = ((Element) result).getElementById("content").html();
              contentTv.setText(Html.fromHtml(content));
            } else if (href.contains(JsoupUtil.QU_LA)) {
              String content = ((Element) result).getElementById("content").html();
              contentTv.setText(Html.fromHtml(content));
            } else if (href.contains(JsoupUtil.MEIYUXS)) {
              String content = ((Element) result).getElementById("content").html();
              contentTv.setText(Html.fromHtml(content));
            }
            refreshLayout.setRefreshing(false);
            toolbar.setTitle(catalog);
            showHideBtn();
          }
        });
      }
    });
  }

  @Override
  public void onRefresh() {
    initData();
  }

  @Override
  public void onClick(View v) {
    scrollView.scrollTo(0, 0);
    switch (v.getId()) {
      case R.id.prevBtn:
        catalogIndex--;
        catalog = localBook.getCatalogs().get(catalogIndex).get("catalog").toString();
        href = localBook.getCatalogs().get(catalogIndex).get("href").toString();
        initData();
        break;
      case R.id.nextBtn:
        catalogIndex++;
        catalog = localBook.getCatalogs().get(catalogIndex).get("catalog").toString();
        href = localBook.getCatalogs().get(catalogIndex).get("href").toString();
        initData();
        break;
    }
  }

  private void showHideBtn() {
    if (order) {
      if (catalogIndex == 0) {
        prevBtn.setVisibility(View.GONE);
        nextBtn.setVisibility(View.VISIBLE);
      } else if (catalogIndex == localBook.getCatalogs().size() - 1) {
        prevBtn.setVisibility(View.VISIBLE);
        nextBtn.setVisibility(View.GONE);
      } else {
        prevBtn.setVisibility(View.VISIBLE);
        nextBtn.setVisibility(View.VISIBLE);
      }
    } else {
      if (catalogIndex == 0) {
        prevBtn.setVisibility(View.GONE);
        nextBtn.setVisibility(View.VISIBLE);
      } else if (catalogIndex == localBook.getCatalogs().size() - 1) {
        prevBtn.setVisibility(View.VISIBLE);
        nextBtn.setVisibility(View.GONE);
      } else {
        prevBtn.setVisibility(View.VISIBLE);
        nextBtn.setVisibility(View.VISIBLE);
      }
    }
  }
}
