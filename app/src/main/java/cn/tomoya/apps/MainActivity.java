package cn.tomoya.apps;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.Toast;

import cn.tomoya.apps.fragment.BookFragment;
import cn.tomoya.apps.fragment.NewsFragment;
import cn.tomoya.apps.fragment.TodoFragment;

public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {

  private long firstBackPressedTime = 0;

  private ViewPager viewPager;
  private BottomNavigationView navigation;
  private NewsFragment newsFragment = new NewsFragment();
  private TodoFragment todoFragment = new TodoFragment();
  private BookFragment bookFragment = new BookFragment();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    viewPager = findViewById(R.id.home_view_pager);
    viewPager.setOffscreenPageLimit(2);
    viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

      @Override
      public Fragment getItem(int position) {
        switch (position) {
          case 0:
            return todoFragment;
          case 1:
            viewPager.setCurrentItem(position);
            return newsFragment;
          case 2:
            return bookFragment;
        }
        return null;
      }

      @Override
      public int getCount() {
        return 3;
      }

    });
    viewPager.addOnPageChangeListener(this);
    navigation = findViewById(R.id.navigation);
    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        MainActivity.this.setTitle(getString(R.string.title_news));
      }
    });
  }

  private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
      = new BottomNavigationView.OnNavigationItemSelectedListener() {

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
      viewPager.setCurrentItem(item.getOrder());
      return true;
    }

  };

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

  }

  @Override
  public void onPageSelected(int position) {
    navigation.getMenu().getItem(position).setChecked(true);
  }

  @Override
  public void onPageScrollStateChanged(int state) {
  }

  @Override
  public void onBackPressed() {
    long secondBackPressedTime = System.currentTimeMillis();
    if (secondBackPressedTime - firstBackPressedTime > 1500) {
      Toast.makeText(this, R.string.press_back_again_to_exit, Toast.LENGTH_SHORT).show();
      firstBackPressedTime = secondBackPressedTime;
    } else {
      super.onBackPressed();
    }
  }

}
