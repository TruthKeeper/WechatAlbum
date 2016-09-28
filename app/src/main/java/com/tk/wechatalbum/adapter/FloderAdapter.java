package com.tk.wechatalbum.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tk.wechatalbum.R;
import com.tk.wechatalbum.bean.AlbumFolderBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by TK on 2016/9/27.
 */

public class FloderAdapter extends RecyclerView.Adapter<FloderAdapter.ItemHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<AlbumFolderBean> mList;
    private int index = 0;
    private onFloderClickListener onFloderClickListener;

    public FloderAdapter(Context mContext, List<AlbumFolderBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(mInflater.inflate(R.layout.folder_select_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        Glide.with(mContext)
                .load(mList.get(position).getIndexPath())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.item);
        if (position == 0) {
            holder.floderSize.setVisibility(View.GONE);
        } else {
            holder.floderSize.setVisibility(View.VISIBLE);
            holder.floderSize.setText(mList.get(position).getAlbumList().size() + "张");
        }
        holder.floderName.setText(mList.get(position).getFolderName());
        holder.indicator.setVisibility(position == index ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item)
        ImageView item;
        @BindView(R.id.floder_name)
        TextView floderName;
        @BindView(R.id.floder_size)
        TextView floderSize;
        @BindView(R.id.indicator)
        View indicator;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (onFloderClickListener != null) {
                itemView.setOnClickListener(v -> {
                    boolean change = index != getAdapterPosition();
                    onFloderClickListener.onClick(getAdapterPosition(), change);
                    if (!change) {
                        return;
                    }
                    indicator.setVisibility(View.VISIBLE);
                    int old = index;
                    index = getAdapterPosition();
                    notifyItemChanged(old);
                });
            }
        }
    }

    public void setOnFloderClickListener(FloderAdapter.onFloderClickListener onFloderClickListener) {
        this.onFloderClickListener = onFloderClickListener;
    }

    public interface onFloderClickListener {
        void onClick(int position, boolean change);
    }

}
