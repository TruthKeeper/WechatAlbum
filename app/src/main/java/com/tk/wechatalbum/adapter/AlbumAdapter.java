package com.tk.wechatalbum.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tk.wechatalbum.R;
import com.tk.wechatalbum.bean.AlbumBean;
import com.tk.wechatalbum.ui.AlbumCheckView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by TK on 2016/8/10.
 * 相册的Adapter
 */
public class AlbumAdapter extends RecyclerView.Adapter<ViewHolder> {
    public static final int CAMERA_TYPE = 1000;
    public static final int ITEM_TYPE = 1001;
    private Context mContext;
    private LayoutInflater mInflater;
    private List<AlbumBean> mList;
    private boolean single;
    private int limit;
    private boolean showCamera;
    private List<AlbumBean> checkList;
    private OnAlbumSelectListener onAlbumSelectListener;
    private int size;


    public AlbumAdapter(Context mContext, List<AlbumBean> mList, boolean showCamera, int limit) {
        this.mContext = mContext;
        this.mList = mList;
        this.showCamera = showCamera;
        this.limit = limit;
        if (limit == 1) {
            single = true;
        } else {
            single = false;
            checkList = new ArrayList<AlbumBean>();
        }
        this.mInflater = LayoutInflater.from(mContext);
        size = mContext.getResources().getDisplayMetrics().widthPixels / 3;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CAMERA_TYPE) {
            return new CameraHolder(mInflater.inflate(R.layout.album_camera_layout, parent, false));
        }
        if (single) {
            return new SingleHolder(mInflater.inflate(R.layout.album_single_layout, parent, false));
        } else {
            return new MultiHolder(mInflater.inflate(R.layout.album_multi_layout, parent, false));
        }
    }

    @Override
    public int getItemCount() {
        return showCamera ? mList.size() + 1 : mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (showCamera && position == 0) {
            return CAMERA_TYPE;
        }
        return ITEM_TYPE;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof CameraHolder) {
            return;
        }
        AlbumBean bean = showCamera ? mList.get(position - 1) : mList.get(position);
        if (single) {
            Glide.with(mContext)
                    .load(bean.getPath())
                    .asBitmap()
                    .override(size, size)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into((ImageView) holder.itemView);
        } else {
            MultiHolder multiHolder = (MultiHolder) holder;
            Glide.with(mContext)
                    .load(bean.getPath())
                    .asBitmap()
                    .override(size, size)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(multiHolder.item);
            if (checkHasInsert(bean)) {
                multiHolder.cover.setVisibility(View.VISIBLE);
                multiHolder.indicator.setChecked(true);
            } else {
                multiHolder.cover.setVisibility(View.GONE);
                multiHolder.indicator.setChecked(false);
            }
        }
    }

    class CameraHolder extends RecyclerView.ViewHolder {

        public CameraHolder(View itemView) {
            super(itemView);
            itemView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
            if (onAlbumSelectListener != null) {
                itemView.setOnClickListener(v -> onAlbumSelectListener.onCamera());
            }
        }
    }

    class SingleHolder extends RecyclerView.ViewHolder {

        public SingleHolder(View itemView) {
            super(itemView);
            itemView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
            if (onAlbumSelectListener != null) {
                itemView.setOnClickListener(v -> onAlbumSelectListener.onClick(getAdapterPosition()));
            }
        }
    }

    class MultiHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item)
        ImageView item;
        @BindView(R.id.cover)
        View cover;
        @BindView(R.id.indicator)
        AlbumCheckView indicator;

        public MultiHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
            if (onAlbumSelectListener != null) {
                item.setOnClickListener(v -> onAlbumSelectListener.onClick(showCamera ? getAdapterPosition() - 1 : getAdapterPosition()));
            }
            indicator.setOnClickListener(v -> {
                if (indicator.isChecked()) {
                    //反选
                    indicator.setChecked(false);
                    checkList.remove(mList.get(showCamera ? getAdapterPosition() - 1 : getAdapterPosition()));
                    cover.setVisibility(View.GONE);
                    if (onAlbumSelectListener != null) {
                        onAlbumSelectListener.onSelect(checkList.size());
                    }
                } else {
                    if (checkList.size() < limit) {
                        //还可以选择
                        indicator.setChecked(true);
                        checkList.add(mList.get(showCamera ? getAdapterPosition() - 1 : getAdapterPosition()));
                        cover.setVisibility(View.VISIBLE);
                        if (onAlbumSelectListener != null) {
                            onAlbumSelectListener.onSelect(checkList.size());
                        }
                    } else {
                        //不可以选择了
                    }
                }
            });
        }
    }

    /**
     * 判断集合中是否已经添加
     *
     * @return
     */
    private boolean checkHasInsert(AlbumBean bean) {
        for (AlbumBean b : checkList) {
            if (b.equals(bean)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 取得选中的集合
     *
     * @return
     */
    public List<AlbumBean> getCheckList() {
        return checkList;
    }

    /**
     * 切换时设置是否显示首格拍照
     *
     * @param showCamera
     */
    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public void setOnAlbumSelectListener(OnAlbumSelectListener onAlbumSelectListener) {
        this.onAlbumSelectListener = onAlbumSelectListener;
    }

    public interface OnAlbumSelectListener {
        //点击拍照
        void onCamera();

        //点击照片回调，预览或者裁剪
        void onClick(int position);

        //选择时回调总选择数
        void onSelect(int select);
    }
}
