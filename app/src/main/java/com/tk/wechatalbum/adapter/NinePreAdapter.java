package com.tk.wechatalbum.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tk.wechatalbum.R;
import com.tk.wechatalbum.bean.AlbumBean;

import java.io.File;
import java.util.List;

/**
 * Created by TK on 2016/8/10.
 * 预览Adapter
 */
public class NinePreAdapter extends RecyclerView.Adapter<ViewHolder> {
    public static final int ITEM_TYPE = 100;
    public static final int ADD_TYPE = 101;
    public static final int MAX = 9;
    private Context mContext;
    private LayoutInflater mInflater;
    private List<AlbumBean> mList;
    private int size;
    private OnPreClickListener onPreClickListener;

    public NinePreAdapter(Context mContext, List<AlbumBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
        this.mInflater = LayoutInflater.from(mContext);
        this.size = mContext.getResources().getDimensionPixelOffset(R.dimen.small_avatar);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE) {
            return new ItemHolder(mInflater.inflate(R.layout.nine_pre, parent, false));
        } else {
            return new AddHolder(mInflater.inflate(R.layout.nine_add, parent, false));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() >= MAX ? MAX : mList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.size() >= MAX) {
            return ITEM_TYPE;
        } else if (position != getItemCount() - 1) {
            return ITEM_TYPE;
        }
        return ADD_TYPE;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            Glide.with(mContext).load(new File(mList.get(position).getPath()))
                    .asBitmap()
                    .into((ImageView) holder.itemView);
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        public ItemHolder(View itemView) {
            super(itemView);
            if (onPreClickListener != null) {
                itemView.setOnClickListener(v -> {
                    onPreClickListener.onPre(getAdapterPosition());
                });
            }
        }
    }

    class AddHolder extends RecyclerView.ViewHolder {
        public AddHolder(View itemView) {
            super(itemView);
            if (onPreClickListener != null) {
                itemView.setOnClickListener(v -> {
                    onPreClickListener.onInsert(MAX - mList.size());
                });
            }
        }
    }

    public void setOnPreClickListener(OnPreClickListener onPreClickListener) {
        this.onPreClickListener = onPreClickListener;
    }

    public interface OnPreClickListener {
        void onPre(int position);

        void onInsert(int other);
    }
}
