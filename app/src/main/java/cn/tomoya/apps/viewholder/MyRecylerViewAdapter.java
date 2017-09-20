package cn.tomoya.apps.viewholder;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public abstract class MyRecylerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  protected List<T> list;
  private Context context;
  protected LayoutInflater inflater;
  private OnItemClickListener onItemClickListener;

  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  public MyRecylerViewAdapter(Context context, List<T> list) {
    this.context = context;
    this.list = list;
    inflater = LayoutInflater.from(context);
  }

  @Override
  public abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

  @Override
  @SuppressWarnings("unchecked")
  public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
    ((TypeViewHolder) holder).bindHolder(list.get(position));
    //添加点击事件
    if (onItemClickListener != null) {
      holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          int position = holder.getLayoutPosition();
          onItemClickListener.onItemClick(holder.itemView, position);
        }
      });
      holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
          int position = holder.getLayoutPosition();
          onItemClickListener.onItemLongClick(holder.itemView, position);
          return false;
        }
      });
    }
  }

  @Override
  public abstract int getItemViewType(int position);

  @Override
  public int getItemCount() {
    return list.size();
  }


  /**
   * -----------------------------------------------------------------------------------------------
   */
  public abstract class TypeViewHolder extends RecyclerView.ViewHolder {

    private View convertView;
    private SparseArray<View> views = new SparseArray<>();

    public TypeViewHolder(View itemView) {
      super(itemView);
      this.convertView = itemView;
    }

    @SuppressWarnings("unchecked")
    public <V extends View> V getView(int viewId) {
      View view = views.get(viewId);
      if (view == null) {
        view = convertView.findViewById(viewId);
        views.put(viewId, view);
      }
      return (V) view;
    }

    public TypeViewHolder setText(int viewId, String text) {
      TextView textView = getView(viewId);
      textView.setText(text);
      return this;
    }

    public TypeViewHolder setBackgroundColor(int viewId, int colorId) {
      ImageView imageView = getView(viewId);
      imageView.setBackgroundResource(colorId);
      return this;
    }

    public TypeViewHolder setNetImage(int viewId, String url) {
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

    public abstract void bindHolder(T model);
  }

  /**
   * -----------------------------------------------------------------------------------------------
   */
  public interface OnItemClickListener {
    void onItemClick(View view, int position);

    void onItemLongClick(View view, int position);
  }

  /**
   * -----------------------------------------------------------------------------------------------
   */
  public static class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private final int[] ATTRS = new int[]{
        android.R.attr.listDivider
    };

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    private Drawable mDivider;

    private int mOrientation;

    public DividerItemDecoration(Context context, int orientation) {
      final TypedArray a = context.obtainStyledAttributes(ATTRS);
      mDivider = a.getDrawable(0);
      a.recycle();
      setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
      if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
        throw new IllegalArgumentException("invalid orientation");
      }
      mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
      if (mOrientation == VERTICAL_LIST) {
        drawVertical(c, parent);
      } else {
        drawHorizontal(c, parent);
      }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
      final int left = parent.getPaddingLeft();
      final int right = parent.getWidth() - parent.getPaddingRight();

      final int childCount = parent.getChildCount();
      for (int i = 0; i < childCount; i++) {
        final View child = parent.getChildAt(i);
        android.support.v7.widget.RecyclerView v = new android.support.v7.widget.RecyclerView(parent.getContext());
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
            .getLayoutParams();
        final int top = child.getBottom() + params.bottomMargin;
        final int bottom = top + mDivider.getIntrinsicHeight();
        mDivider.setBounds(left, top, right, bottom);
        mDivider.draw(c);
      }
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
      final int top = parent.getPaddingTop();
      final int bottom = parent.getHeight() - parent.getPaddingBottom();

      final int childCount = parent.getChildCount();
      for (int i = 0; i < childCount; i++) {
        final View child = parent.getChildAt(i);
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
            .getLayoutParams();
        final int left = child.getRight() + params.rightMargin;
        final int right = left + mDivider.getIntrinsicHeight();
        mDivider.setBounds(left, top, right, bottom);
        mDivider.draw(c);
      }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
      if (mOrientation == VERTICAL_LIST) {
        outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
      } else {
        outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
      }
    }
  }
}