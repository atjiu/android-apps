package cn.tomoya.apps.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import cn.tomoya.apps.R;
import cn.tomoya.apps.fragment.V2exHotFragment;
import cn.tomoya.apps.fragment.V2exLatestFragment;

/**
 * Created by tomoya on 3/25/17.
 */

public class V2exActivity extends BaseActivity implements TabLayout.OnTabSelectedListener, ViewPager.OnPageChangeListener {

  private Toolbar toolbar;
  private TabLayout tabLayout;
  private ViewPager viewPager;
  private V2exLatestFragment v2exLatestFragment = new V2exLatestFragment();
  private V2exHotFragment v2exHotFragment = new V2exHotFragment();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_v2ex);

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle(R.string.v2ex);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        V2exActivity.this.finish();
      }
    });

    tabLayout = (TabLayout) findViewById(R.id.tabLayout);
    viewPager = (ViewPager) findViewById(R.id.viewPager);

    tabLayout.addOnTabSelectedListener(this);
    viewPager.addOnPageChangeListener(this);

    viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
      @Override
      public Fragment getItem(int position) {
        switch (position) {
          case 0:
            return v2exLatestFragment;
          case 1:
            return v2exHotFragment;
        }
        return null;
      }

      @Override
      public int getCount() {
        return 2;
      }
    });

  }

  @Override
  public void onTabSelected(TabLayout.Tab tab) {
    viewPager.setCurrentItem(tab.getPosition());
  }

  @Override
  public void onTabUnselected(TabLayout.Tab tab) {

  }

  @Override
  public void onTabReselected(TabLayout.Tab tab) {

  }

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

  }

  @Override
  public void onPageSelected(int position) {
    tabLayout.getTabAt(position).select();
  }

  @Override
  public void onPageScrollStateChanged(int state) {

  }
}