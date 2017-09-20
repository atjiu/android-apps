package cn.tomoya.apps.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.tomoya.apps.R;
import cn.tomoya.apps.viewholder.MyBaseFragment;

/**
 * Created by tomoya on 4/1/17.
 */

public class NewsFragment extends MyBaseFragment implements
    TabLayout.OnTabSelectedListener,
    ViewPager.OnPageChangeListener {

  private Toolbar toolbar;
  private TabLayout tabLayout;
  private ViewPager viewPager;
  private ITNewsFragment itNewsFragment = new ITNewsFragment();
  private OtherNewsFragment otherNewsFragment = new OtherNewsFragment();

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_news, container, false);
    toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    toolbar.setTitle(getString(R.string.title_news));

    tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
    viewPager = (ViewPager) view.findViewById(R.id.viewPager);

    tabLayout.addOnTabSelectedListener(this);
    viewPager.addOnPageChangeListener(this);
    viewPager.setAdapter(new FragmentStatePagerAdapter(getFragmentManager()) {
      @Override
      public Fragment getItem(int position) {
        switch (position) {
          case 0:
            return itNewsFragment;
          case 1:
            return otherNewsFragment;
        }
        return null;
      }

      @Override
      public int getCount() {
        return 2;
      }
    });

    return view;
  }

  @Override
  public void fetchData() {

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
