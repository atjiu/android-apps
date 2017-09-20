package cn.tomoya.apps.viewholder;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by tomoya on 4/1/17.
 */

public abstract class MyBaseFragment extends Fragment {

  protected boolean isViewInitiated;
  protected boolean isVisibleToUser;
  protected boolean isDataInitiated;

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    isViewInitiated = true;
    prepareFetchData();
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    this.isVisibleToUser = isVisibleToUser;
    prepareFetchData();
  }

  public abstract void fetchData();

  public boolean prepareFetchData() {
    return prepareFetchData(false);
  }

  public boolean prepareFetchData(boolean forceUpdate) {
    if (isVisibleToUser && isViewInitiated && (!isDataInitiated || forceUpdate)) {
      fetchData();
      isDataInitiated = true;
      return true;
    }
    return false;
  }
}
