package com.tk.wechatalbum.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.tk.wechatalbum.bean.AlbumBean;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by TK on 2016/9/29.
 * viewpager的预览adapter
 */

public class AlbumPreAdapter extends PagerAdapter {
    private LinkedList<PhotoView> mCacheList = new LinkedList<PhotoView>();
    private List<AlbumBean> albumList;
    private Context mContext;
    private OnPhotoListener onPhotoListener;

    public AlbumPreAdapter(Context mContext, List<AlbumBean> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public int getCount() {
        return albumList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final PhotoView photoView;
        if (mCacheList.size() == 0) {
            photoView = new PhotoView(mContext);
        } else {
            //从缓存集合中取
            photoView = mCacheList.removeFirst();
        }
        photoView.setBackgroundColor(Color.BLACK);
        final PhotoViewAttacher attacher = new PhotoViewAttacher(photoView);
        Glide.with(mContext)
                .load(new File(albumList.get(position).getPath()))
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        photoView.setImageDrawable(resource);
                        attacher.update();
                    }
                });
        attacher.setOnViewTapListener((view, x, y) -> {
            if (onPhotoListener != null) {
                onPhotoListener.onClick(position);
            }
        });
        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mCacheList.add((PhotoView) object);
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setOnPhotoListener(OnPhotoListener onPhotoListener) {
        this.onPhotoListener = onPhotoListener;
    }

    public interface OnPhotoListener {
        void onClick(int position);
    }
}
