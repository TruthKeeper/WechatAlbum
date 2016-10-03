package com.tk.wechatalbum;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.tk.wechatalbum.activity.AlbumActivity;
import com.tk.wechatalbum.bean.AlbumBean;
import com.tk.wechatalbum.callback.PhotoCallback;
import com.tk.wechatalbum.utils.CameraUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.tk.wechatalbum.Constants.PhotoPickConstants.CHECK_LIMIT;
import static com.tk.wechatalbum.Constants.PhotoPickConstants.DEFAULT_LIMIT;
import static com.tk.wechatalbum.Constants.PhotoPickConstants.IS_SINGLE;
import static com.tk.wechatalbum.Constants.PhotoPickConstants.NEED_CLIP;
import static com.tk.wechatalbum.Constants.PhotoPickConstants.RESULT_DATA;
import static com.tk.wechatalbum.Constants.PhotoPickConstants.SHOW_CAMERA;
import static com.tk.wechatalbum.Constants.PhotoPickConstants.START_CAMERA;

/**
 * Created by TK on 2016/9/26.
 */

public final class PhotoPick {
    public static final String TAG = "PhotoPick";
    public static final int CLIPE_NULL = 1;
    public static final int CLIPE_DEFAULT = 1;
    public static final int CLIPE_CIRCLE = 2;
    public static int themeColor = 0xFF45C01A;
    //开启拍照支线
    private static boolean startCamera;
    //拍照标志
    private static int cameraFlag;

    //调用拍照功能生成的临时输出文件流
    public static File tempCameraFile;
    //调用裁剪功能生成的临时输出文件流
    public static File tempCropFile;
    //拍照+裁剪时临时保存
    private static int tempRequestCode;

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Bundle bundle;


        public Builder() {
            bundle = new Bundle();
            bundle.putBoolean(START_CAMERA, false);
            bundle.putBoolean(SHOW_CAMERA, true);
            bundle.putBoolean(IS_SINGLE, true);
            bundle.putInt(CHECK_LIMIT, 1);
            bundle.putBoolean(NEED_CLIP, false);
        }

        /**
         * 拍照模式，默认相册模式,开启后只关注是否需要裁剪
         *
         * @return
         */
        public Builder takePhoto() {
            bundle.putBoolean(START_CAMERA, true);
            return this;
        }

        /**
         * 相册首格是否显示拍照，默认true
         *
         * @return
         */
        public Builder showCamera(boolean show) {
            bundle.putBoolean(SHOW_CAMERA, show);
            return this;
        }

        /**
         * 设置是否单选模式，默认true
         *
         * @param asSingle
         * @return
         */
        public Builder asSingle(boolean asSingle) {
            bundle.putBoolean(IS_SINGLE, asSingle);
            return this;
        }

        /**
         * 设置多选时选择数目限制，单选时无效
         *
         * @param limit
         * @return
         */
        public Builder setPhotoCheckLimit(int limit) {
            bundle.putInt(CHECK_LIMIT, limit);
            return this;
        }

        /**
         * 是否需要裁剪，默认false，仅限单选模式有用
         *
         * @return
         */
        public Builder needClip() {
            bundle.putBoolean(NEED_CLIP, true);
            return this;
        }


        /**
         * 设置风格颜色，默认微信绿
         *
         * @return
         */
        public Builder setThemeColor(int themeColor) {
            PhotoPick.themeColor = themeColor;
            return this;
        }

        /**
         * 开启，走你
         *
         * @param activity
         * @return true 可添加动画切换效果
         */
        public boolean checkAndStart(Activity activity, int requestCode) {
            Intent intent = null;
            if (bundle.getBoolean(START_CAMERA)) {
                //拍照
                startCamera(activity, requestCode, bundle.getBoolean(NEED_CLIP));
                return true;
            }
            startCamera = false;
            if (bundle.getBoolean(IS_SINGLE)) {
                //单选
                intent = new Intent(activity, AlbumActivity.class);
                intent.putExtras(bundle);
                activity.startActivityForResult(intent, requestCode);
                return true;
            }
            int limit = bundle.getInt(CHECK_LIMIT);
            if (limit < 1
                    || limit > DEFAULT_LIMIT
                    || (limit > 1 && bundle.getBoolean(NEED_CLIP))
                    || activity == null) {
                //配置错误
                Log.e(TAG, "build failure,check your parameter");
                return false;
            }
            intent = new Intent(activity, AlbumActivity.class);
            intent.putExtras(bundle);
            activity.startActivityForResult(intent, requestCode);
            return true;
        }
    }

    /**
     * 调用Android系统拍照
     *
     * @param activity
     * @param requestCode
     */
    public static final void startCamera(Activity activity, int requestCode, boolean needCrop) {
        startCamera = true;
        if (needCrop) {
            tempRequestCode = requestCode;
            cameraFlag = 2;
        } else {
            cameraFlag = 1;
        }
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(activity.getPackageManager()) != null) {
            // 创建临时文件，并设置系统相机拍照后的输出路径
            try {
                tempCameraFile = CameraUtils.createTmpFile(activity);
                if (tempCameraFile != null && tempCameraFile.exists()) {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempCameraFile));
                    activity.startActivityForResult(cameraIntent, requestCode);
                } else {
                    Toast.makeText(activity.getApplicationContext(), "创建缓存文件失败", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(activity.getApplicationContext(), "创建缓存文件失败", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity.getApplicationContext(), "您的手机不支持相机", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 调用Android系统裁剪
     *
     * @param activity
     * @param source
     * @param requestCode
     */
    public static final void startCrop(Activity activity, File source, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(Uri.fromFile(source), "image/*");
        intent.putExtra("crop", "true");
        //默认正方形
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("return-data", true);
        File file = new File(Environment.getExternalStorageDirectory() + "/tempCrop/");
        if (!file.exists()) {
            file.mkdirs();
        }
        tempCropFile = new File(file,
                System.currentTimeMillis() + ".jpeg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(tempCropFile));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 接受回调，内部匹配,PhotoPick不关心requestCode(*^__^*)
     *
     * @param activity
     * @param resultCode
     * @param data
     * @param callback
     */
    public static final void onActivityResult(Activity activity, int resultCode, Intent data, PhotoCallback callback) {
        if (resultCode != Activity.RESULT_OK
                || callback == null) {
            startCamera = false;
            cameraFlag = 0;
            //清理临时文件
            if (tempCameraFile != null && tempCameraFile.exists()) {
                tempCameraFile.delete();
            }
            if (tempCropFile != null && tempCropFile.exists()) {
                tempCropFile.delete();
            }
            return;
        }

        if (startCamera) {
            //拍照支线中
            if (cameraFlag == 1) {
                //无须裁剪
                callback.onComplete(tempCameraFile);
                startCamera = false;
                cameraFlag = 0;
            } else if (cameraFlag == 2) {
                //wait裁剪
                startCrop(activity, tempCameraFile, tempRequestCode);
                cameraFlag = 3;
            } else if (cameraFlag == 3) {
                //裁剪完毕
                callback.onComplete(tempCropFile);
                startCamera = false;
                cameraFlag = 0;
            }
            return;
        }

        if (data.getBooleanExtra(Constants.PhotoPickConstants.RESULT_SINGLE, false)) {
            callback.onComplete(new File(data.getStringExtra(RESULT_DATA)));
        } else {
            List<AlbumBean> checkList = data.getParcelableArrayListExtra(RESULT_DATA);
            List<File> sourceList = new ArrayList<File>();
            for (int i = 0; i < checkList.size(); i++) {
                sourceList.add(new File(checkList.get(i).getPath()));
            }
            callback.onComplete(sourceList);
        }
    }


}
