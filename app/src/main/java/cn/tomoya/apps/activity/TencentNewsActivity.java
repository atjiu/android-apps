package cn.tomoya.apps.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import cn.tomoya.apps.R;
import cn.tomoya.apps.fragment.TencentHuaTiFragment;
import cn.tomoya.apps.fragment.TencentMilFragment;
import cn.tomoya.apps.fragment.TencentNewsFragment;
import cn.tomoya.apps.fragment.TencentSheHuiFragment;

/**
 * Created by tomoya on 3/25/17.
 */

public class TencentNewsActivity extends BaseActivity implements TabLayout.OnTabSelectedListener, ViewPager.OnPageChangeListener {

  private Toolbar toolbar;
  private TabLayout tabLayout;
  private ViewPager viewPager;
  private TencentNewsFragment tencentNewsFragment = new TencentNewsFragment();
  private TencentSheHuiFragment tencentSheHuiFragment = new TencentSheHuiFragment();
  private TencentMilFragment tencentMilFragment = new TencentMilFragment();
  private TencentHuaTiFragment tencentHuaTiFragment = new TencentHuaTiFragment();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tencentnews);

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle(R.string.tencentnews);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        TencentNewsActivity.this.finish();
      }
    });

    tabLayout = (TabLayout) findViewById(R.id.tabLayout);
    viewPager = (ViewPager) findViewById(R.id.viewPager);

    tabLayout.addOnTabSelectedListener(this);
    viewPager.addOnPageChangeListener(this);
    viewPager.setOffscreenPageLimit(3);
    viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
      @Override
      public Fragment getItem(int position) {
        switch (position) {
          case 0:
            return tencentNewsFragment;
          case 1:
            return tencentSheHuiFragment;
          case 2:
            return tencentMilFragment;
          case 3:
            return tencentHuaTiFragment;
        }
        return null;
      }

      @Override
      public int getCount() {
        return 4;
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
