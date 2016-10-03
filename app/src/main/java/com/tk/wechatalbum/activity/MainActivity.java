package com.tk.wechatalbum.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tk.wechatalbum.PhotoPick;
import com.tk.wechatalbum.R;
import com.tk.wechatalbum.adapter.NinePreAdapter;
import com.tk.wechatalbum.bean.AlbumBean;
import com.tk.wechatalbum.callback.CompressCallback;
import com.tk.wechatalbum.callback.PhotoCallback;
import com.tk.wechatalbum.ui.SelectBottomDialog;
import com.tk.wechatalbum.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.checkbox)
    CheckBox checkbox;
    @BindView(R.id.iv_show)
    ImageView ivShow;
    @BindView(R.id.upload_size)
    TextView uploadSize;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    private SelectBottomDialog selectBottomDialog;
    private NinePreAdapter ninePreAdapter;
    private List<File> preList = new ArrayList<File>();
    private List<AlbumBean> checkList = new ArrayList<AlbumBean>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        ninePreAdapter = new NinePreAdapter(this, preList);
        recyclerview.setHasFixedSize(true);
        recyclerview.setAdapter(ninePreAdapter);
        recyclerview.setLayoutManager(new GridLayoutManager(this, 4));
        ninePreAdapter.setOnPreClickListener(new NinePreAdapter.OnPreClickListener() {
            @Override
            public void onPre(int position) {
                //预览视图
                preList.remove(position);
                ninePreAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onInsert(int other) {
                //显示拍照，不裁剪
                PhotoPick.builder()
                        .asSingle(false)
                        .setPhotoCheckLimit(other)
                        .checkAndStart(MainActivity.this, 3);
            }
        });
        selectBottomDialog = new SelectBottomDialog(this);
        selectBottomDialog.setOnSelectListener(new SelectBottomDialog.OnSelectListener() {
            @Override
            public void onCamera() {
                //拍照+裁剪
                if (PhotoPick.builder()
                        .takePhoto()
                        .needClip()
                        .checkAndStart(MainActivity.this, 1)) {
                    selectBottomDialog.dismiss();
                }
            }

            @Override
            public void onAlbum() {
                //相册，裁剪
                if (PhotoPick.builder()
                        .asSingle(true)
                        .showCamera(false)
                        .needClip()
                        .checkAndStart(MainActivity.this, 2)) {
                    selectBottomDialog.dismiss();
                }
            }
        });
    }

    @OnClick({R.id.iv_show, R.id.btn_camera_plus, R.id.btn_album_plus})
    public void onClick(View view) {
        switch (view.getId()) {
            //权限
            case R.id.iv_show:
                selectBottomDialog.show();
                break;
            case R.id.btn_camera_plus:
                //拍照+裁剪
                PhotoPick.builder()
                        .takePhoto()
                        .needClip()
                        .checkAndStart(this, 1);
                break;
            case R.id.btn_album_plus:
                //相册，裁剪
                PhotoPick.builder()
                        .asSingle(true)
                        .showCamera(false)
                        .needClip()
                        .checkAndStart(this, 2);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
            case 2:
                PhotoPick.onActivityResult(this, resultCode, data, checkbox.isChecked() ?
                        new AlbumCompressCallBack(this) : new AlbumCallBack());
                break;
            case 3:
                PhotoPick.onActivityResult(this, resultCode, data, checkbox.isChecked() ?
                        new FriendCompressCallBack(this) : new FriendCallBack());
                break;
        }

    }

    private class AlbumCallBack implements PhotoCallback {
        @Override
        public void onComplete(File source) {
            Glide.with(MainActivity.this)
                    .load(source)
                    .asBitmap()
                    .into(ivShow);
            uploadSize.setText(FileUtils.getImaSize(source));
        }

        @Override
        public void onComplete(List<File> sourceList) {
        }
    }

    private class AlbumCompressCallBack extends CompressCallback {

        public AlbumCompressCallBack(Context mContext) {
            super(mContext);
        }

        @Override
        protected void onFailure(Throwable throwable) {

        }

        @Override
        protected void onSuccess(File compressFile) {
            Glide.with(MainActivity.this)
                    .load(compressFile)
                    .asBitmap()
                    .into(ivShow);
            uploadSize.setText(FileUtils.getImaSize(compressFile));
        }

        @Override
        protected void onSuccess(List<File> compressFileList) {
        }

        @Override
        protected void onStart() {
            progressDialog.show();
        }

        @Override
        protected void onFinish() {
            progressDialog.dismiss();
        }
    }

    private class FriendCallBack implements PhotoCallback {
        @Override
        public void onComplete(File source) {
            preList.add(source);
            ninePreAdapter.notifyDataSetChanged();
        }

        @Override
        public void onComplete(List<File> sourceList) {
            preList.addAll(sourceList);
            ninePreAdapter.notifyDataSetChanged();
        }
    }

    private class FriendCompressCallBack extends CompressCallback {

        public FriendCompressCallBack(Context mContext) {
            super(mContext);
        }

        @Override
        protected void onFailure(Throwable throwable) {

        }

        @Override
        protected void onSuccess(File compressFile) {
            preList.add(compressFile);
            ninePreAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onSuccess(List<File> compressFileList) {
            preList.addAll(compressFileList);
            ninePreAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onStart() {
            progressDialog.show();
        }

        @Override
        protected void onFinish() {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("main", "onDestroy");
    }
}
