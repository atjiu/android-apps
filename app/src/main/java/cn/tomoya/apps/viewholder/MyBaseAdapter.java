package cn.tomoya.apps.viewholder;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by eebn on 3/30/2017.
 */

public abstract class MyBaseAdapter<T> extends BaseAdapter {

  protected List<T> _data;
  protected Context context;

  public MyBaseAdapter(Context context, List<T> data) {
    this._data = data;
    this.context = context;
  }

  @Override
  public int getCount() {
    return _data.size();
  }

  @Override
  public Object getItem(int position) {
    return _data.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public abstract View getView(int position, View convertView, ViewGroup parent);

  protected static class ViewHolder {

    private SparseArray<View> views;
    private int position;
    private View convertView;
    private Context context;

    public ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
      this.context = context;
      this.position = position;
      this.views = new SparseArray<>();
      convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
      convertView.setTag(this);
    }

    public static ViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
      if (convertView == null) {
        return new ViewHolder(context, parent, layoutId, position);
      } else {
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.position = position;
        return holder;
      }
    }

    public <T extends View> T getView(int viewId) {
      View view = views.get(viewId);
      if (view == null) {
        view = convertView.findViewById(viewId);
        views.put(viewId, view);
      }
      return (T) view;
    }

    public View getConvertView() {
      return convertView;
    }

    public int getPosition() {
      return position;
    }

    public ViewHolder setText(int viewId, String text) {
      TextView textView = getView(viewId);
      textView.setText(text);
      return this;
    }

    public ViewHolder setNetImage(int viewId, String url) {
      ImageView imageView = getView(viewId);
      Glide
          .with(context)
          .load(url)
          .centerCrop()
//        .placeholder(R.drawable.loading_spinner)
          .crossFade()
          .into(imageView);
      return this;
    }

    public ViewHolder setDrawableImage(int viewId, int drawable) {
      ImageView imageView = getView(viewId);
      imageView.setBackgroundResource(drawable);
      return this;
    }
  }

}
