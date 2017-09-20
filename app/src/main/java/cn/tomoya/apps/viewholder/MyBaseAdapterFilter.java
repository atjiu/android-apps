package cn.tomoya.apps.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eebn on 2017/4/5.
 */

public abstract class MyBaseAdapterFilter<T> extends MyBaseAdapter<T> implements Filterable {

  private ArrayList<T> mUnfilteredData;
  private ArrayFilter arrayFilter;

  public MyBaseAdapterFilter(Context context, List<T> data) {
    super(context, data);
  }

  @Override
  public Filter getFilter() {
    if (arrayFilter == null) {
      arrayFilter = new ArrayFilter();
    }
    return arrayFilter;
  }

  @Override
  public abstract View getView(int position, View convertView, ViewGroup parent);

  private class ArrayFilter extends Filter {

    @Override
    protected FilterResults performFiltering(CharSequence prefix) {
      FilterResults results = new FilterResults();
      if (mUnfilteredData == null) {
        mUnfilteredData = new ArrayList<>(_data);
      }
      if (prefix == null || prefix.length() == 0) {
        ArrayList<T> list = mUnfilteredData;
        results.values = list;
        results.count = list.size();
      } else {
        String prefixString = prefix.toString().toLowerCase();
        ArrayList<T> unfilteredValues = mUnfilteredData;
        int count = unfilteredValues.size();
        ArrayList<T> newValues = new ArrayList<>(count);
        newValues = dealList(count, prefixString, unfilteredValues, newValues);
        results.values = newValues;
        results.count = newValues.size();
      }
      return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
      if (results != null && results.count > 0) {//有符合过滤规则的数据
        _data.clear();
        _data.addAll((List<T>) results.values);
        notifyDataSetChanged();
      } else {//没有符合过滤规则的数据
        notifyDataSetInvalidated();
      }
    }

    @Override
    public CharSequence convertResultToString(Object resultValue) {
      return myConvertResultToString(resultValue);
    }
  }

  protected abstract CharSequence myConvertResultToString(Object resultValue);

  protected abstract ArrayList<T> dealList(int count, String prefixString, ArrayList<T> unfilteredValues, ArrayList<T> newValues);
}
