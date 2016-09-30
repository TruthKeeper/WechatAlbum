package com.tk.wechatalbum.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tk.wechatalbum.R;
import com.tk.wechatalbum.adapter.NinePreAdapter;
import com.tk.wechatalbum.bean.AlbumBean;
import com.tk.wechatalbum.PhotoPick;
import com.tk.wechatalbum.ui.SelectBottomDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final int LIMIT = 9;
    private static final int ALBUM_REQUEST = 100;
    private static final int CAMERA_REQUEST = 101;
    @BindView(R.id.iv_show)
    ImageView ivShow;
    @BindView(R.id.upload_size)
    TextView uploadSize;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    private SelectBottomDialog selectBottomDialog;
    private NinePreAdapter ninePreAdapter;
    private List<AlbumBean> preList = new ArrayList<AlbumBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ninePreAdapter = new NinePreAdapter(this, preList);
        recyclerview.setHasFixedSize(true);
        recyclerview.setAdapter(ninePreAdapter);
        recyclerview.setLayoutManager(new GridLayoutManager(this, 4));
        ninePreAdapter.setOnPreClickListener(new NinePreAdapter.OnPreClickListener() {
            @Override
            public void onPre(int position) {
                //预览视图
            }

            @Override
            public void onInsert(int other) {
                //显示拍照，不裁剪
                PhotoPick.builder()
                        .setPhotoCheckLimit(other)
                        .checkAndStart(MainActivity.this);
            }
        });
        selectBottomDialog = new SelectBottomDialog(this);
        selectBottomDialog.setOnSelectListener(new SelectBottomDialog.OnSelectListener() {
            @Override
            public void onCamera() {

            }

            @Override
            public void onAlbum() {

            }
        });
    }

    @OnClick({R.id.iv_show, R.id.btn_camera_plus, R.id.btn_album_plus})
    public void onClick(View view) {
        switch (view.getId()) {
            //权限
            case R.id.iv_show:

                break;
            case R.id.btn_camera_plus:
                //拍照+裁剪
                PhotoPick.builder()
                        .needClip()
                        .takePhoto()
                        .checkAndStart(this);
                break;
            case R.id.btn_album_plus:
                //相册，裁剪
                PhotoPick.builder()
                        .showCamera(false)
                        .needClip()
                        .setPhotoCheckLimit(1)
                        .checkAndStart(this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PhotoPick.onActivityResult(requestCode, resultCode, data, new PhotoPick.OnPhotoListener() {
            @Override
            public void startCompress() {

            }

            @Override
            public void onComplete(File file) {

            }

            @Override
            public void onFailure(Throwable e) {

            }
        });

        if (requestCode == ALBUM_REQUEST) {
//            Glide.with(this).load(new File(data.getStringExtra("path")))
//                    .asBitmap()
//                    .into(ivShow);
        } else if (requestCode == CAMERA_REQUEST) {

        }
    }
}
