package com.tk.wechatalbum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tk.wechatalbum.activity.AlbumActivity;

import java.io.File;

/**
 * Created by TK on 2016/9/26.
 */

public class PhotoPick {
    public static final String TAG = "PhotoPick";
    public static final int CAMERA_REQUEST = 1111;
    public static final int ALBUM_REQUEST = 2222;
    public static final int DEFAULT_LIMIT = 9;
    public static final String START_CAMERA = "start_camera";
    public static final String CHECK_LIMIT = "check_limit";
    public static final String SHOW_CAMERA = "show_camera";
    public static final String NEED_CLIP = "need_clip";

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Bundle bundle;

        public Builder() {
            bundle = new Bundle();
            //默认单选，不显示拍照，不裁剪,选择相册模式
            bundle.putInt(CHECK_LIMIT, 1);
            bundle.putBoolean(SHOW_CAMERA, false);
            bundle.putBoolean(NEED_CLIP, false);
            bundle.putBoolean(START_CAMERA, false);
        }

        /**
         * 设置选择相册限制
         *
         * @param limit
         * @return
         */
        public Builder setPhotoCheckLimit(int limit) {
            bundle.putInt(CHECK_LIMIT, limit);
            return this;
        }

        /**
         * 相册首格是否显示拍照
         *
         * @return
         */
        public Builder showCamera() {
            bundle.putBoolean(SHOW_CAMERA, true);
            return this;
        }

        /**
         * 是否需要裁剪，仅限单选
         *
         * @return
         */
        public Builder needClip() {
            bundle.putBoolean(NEED_CLIP, true);
            return this;
        }

        /**
         * 拍照模式，默认为相册模式
         *
         * @return
         */
        public Builder takePhoto() {
            bundle.putBoolean(START_CAMERA, true);
            return this;
        }

        /**
         * 开启，
         *
         * @param activity
         * @return true 可添加动画切换效果
         */
        public boolean checkAndStart(Activity activity) {
            if (bundle.getBoolean(START_CAMERA)) {
                //拍照模式无视其他参数

                return true;
            }
            int l = bundle.getInt(CHECK_LIMIT);
            if (l < 1 || l > DEFAULT_LIMIT || (l > 1 && bundle.getBoolean(NEED_CLIP)) || activity == null) {
                //不支持多选加裁剪
                Log.e(TAG, "build failure,check your parameter");
                return false;
            }
            Intent intent = new Intent(activity, AlbumActivity.class);
            intent.putExtras(bundle);
            activity.startActivityForResult(intent, ALBUM_REQUEST);
            return true;
        }
    }

    /**
     * 接受回调，内部匹配
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @param onPhotoListener
     */
    public static final void onActivityResult(int requestCode, int resultCode, Intent data, OnPhotoListener onPhotoListener) {
        if (onPhotoListener == null) {
            return;
        }

    }

    public interface OnPhotoListener {
        void startCompress();

        void onComplete(File file);

        void onFailure(Throwable e);
    }
}
