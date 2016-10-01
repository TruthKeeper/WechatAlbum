package com.tk.wechatalbum.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tk.wechatalbum.R;
import com.tk.wechatalbum.bean.AlbumFolderBean;
import com.tk.wechatalbum.ui.FolderCheckView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by TK on 2016/9/27.
 * 文件夹adapter
 */

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ItemHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<AlbumFolderBean> mList;
    private int index = 0;
    private onFolderClickListener onFolderClickListener;

    public FolderAdapter(Context mContext, List<AlbumFolderBean> mList) {
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
                .into(holder.item);
        if (position == 0) {
            holder.folderSize.setVisibility(View.GONE);
        } else {
            holder.folderSize.setVisibility(View.VISIBLE);
            holder.folderSize.setText(mList.get(position).getAlbumList().size() + "张");
        }
        holder.folderName.setText(mList.get(position).getFolderName());
        holder.indicator.setVisibility(position == index ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item)
        ImageView item;
        @BindView(R.id.folder_name)
        TextView folderName;
        @BindView(R.id.folder_size)
        TextView folderSize;
        @BindView(R.id.indicator)
        FolderCheckView indicator;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (onFolderClickListener != null) {
                itemView.setOnClickListener(v -> {
                    boolean change = index != getAdapterPosition();
                    onFolderClickListener.onClick(getAdapterPosition(), change);
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

    public void setOnFolderClickListener(onFolderClickListener onFolderClickListener) {
        this.onFolderClickListener = onFolderClickListener;
    }

    public interface onFolderClickListener {
        void onClick(int position, boolean change);
    }

}
