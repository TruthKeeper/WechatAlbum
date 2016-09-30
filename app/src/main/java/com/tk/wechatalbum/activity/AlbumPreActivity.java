package com.tk.wechatalbum.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tk.wechatalbum.R;
import com.tk.wechatalbum.adapter.AlbumPreAdapter;
import com.tk.wechatalbum.bean.AlbumBean;
import com.tk.wechatalbum.ui.AlbumCheckView;
import com.tk.wechatalbum.ui.ConfirmButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.senab.photoview.HackyViewPager;

import static com.tk.wechatalbum.Constants.*;

/**
 * Created by TK on 2016/9/29.
 * 相册预览List
 */

public class AlbumPreActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, AlbumPreAdapter.OnPhotoListener {
    @BindView(R.id.album_viewpager)
    HackyViewPager albumViewpager;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.confirm_btn)
    ConfirmButton confirmBtn;
    @BindView(R.id.header_layout)
    LinearLayout headerLayout;
    @BindView(R.id.bottom_layout)
    RelativeLayout bottomLayout;
    @BindView(R.id.check_view)
    AlbumCheckView checkView;
    private AlbumPreAdapter albumPreAdapter;
    private List<AlbumBean> albumList;
    private List<AlbumBean> checkList;

    private int limit;
    private int firstIndex;
    private boolean fullScreen = true;
    private ValueAnimator valueAnimator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_preview);
        ButterKnife.bind(this);
        initConstants();
        initAnim();
        headerLayout.setBackgroundDrawable(bottomLayout.getBackground());

        albumPreAdapter = new AlbumPreAdapter(this, albumList);
        albumViewpager.setAdapter(albumPreAdapter);
        albumViewpager.setCurrentItem(firstIndex, false);
        //初始化index
        title.setText((firstIndex + 1) + "/" + albumList.size());
        confirmBtn.setEnabled(true);
        refreshSelect(firstIndex);

        albumViewpager.setOffscreenPageLimit(3);
        albumViewpager.addOnPageChangeListener(this);
        albumPreAdapter.setOnPhotoListener(this);

    }

    /**
     * 接受，并处理参数
     */
    private void initConstants() {
        albumList = getIntent().getParcelableArrayListExtra(PreAlbumConstants.ALBUM_LIST);
        checkList = getIntent().getParcelableArrayListExtra(PreAlbumConstants.CHECK_LIST);
        firstIndex = getIntent().getIntExtra(PreAlbumConstants.INDEX, 0);
        limit = getIntent().getIntExtra(PreAlbumConstants.LIMIT, 0);
    }

    /**
     * 初始化值动画
     */
    private void initAnim() {
        valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        valueAnimator.setDuration(750);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) animation.getAnimatedValue();
                RelativeLayout.LayoutParams headP = (RelativeLayout.LayoutParams) headerLayout.getLayoutParams();
                RelativeLayout.LayoutParams bottomP = (RelativeLayout.LayoutParams) bottomLayout.getLayoutParams();
                if (fullScreen) {
                    //消失
                    headP.setMargins(0, (int) (-headP.height * f), 0, 0);
                    bottomP.setMargins(0, 0, 0, (int) (-bottomP.height * f));
                } else {
                    //显示
                    headP.setMargins(0, (int) (-headP.height * (1 - f)), 0, 0);
                    bottomP.setMargins(0, 0, 0, (int) (-bottomP.height * (1 - f)));
                }
                headerLayout.setLayoutParams(headP);
                bottomLayout.setLayoutParams(bottomP);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                fullScreen = !fullScreen;
                //// TODO: 2016/9/29 界面闪动，记录上次位移的矩阵？
//                if (fullScreen) {
//                    findViewById(android.R.id.content).setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//                } else {
//                    findViewById(android.R.id.content).setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
//                }
//                AlbumUtils.needFullScreen(AlbumPreActivity.this, fullScreen);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    @OnClick({R.id.back, R.id.confirm_btn, R.id.check_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                refreshOnResult(false);
                break;
            case R.id.confirm_btn:
                refreshOnResult(true);
                break;
            case R.id.check_layout:
                if (checkView.isChecked()) {
                    /// /反选
                    checkList.remove(albumList.get(albumViewpager.getCurrentItem()));
                    refreshSelect(albumViewpager.getCurrentItem());
                } else {
                    if (checkList.size() < limit) {
                        checkList.add(albumList.get(albumViewpager.getCurrentItem()));
                        refreshSelect(albumViewpager.getCurrentItem());
                    } else {
                        Toast.makeText(this, "您最多只能选" + limit + "张照片", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            refreshOnResult(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 回调刷新点击的项目
     *
     * @param finish 直接完成
     */
    private void refreshOnResult(boolean finish) {
        Intent data = new Intent();
        data.putParcelableArrayListExtra(PreAlbumConstants.CHECK_LIST, new ArrayList<>(checkList));
        data.putExtra(PreAlbumConstants.FINISH, finish);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    /**
     * 刷新选中数目
     *
     * @param position
     */
    private void refreshSelect(int position) {
        checkView.setChecked(checkList.contains(albumList.get(position)));
        if (checkList.size() == 0) {
            confirmBtn.setText("完成");
        } else {
            confirmBtn.setText("完成(" + checkList.size() + "/" + limit + ")");
        }
    }

    @Override
    public void onClick(int position) {
        //点击屏幕隐藏ui
        if (!valueAnimator.isRunning()) {
            valueAnimator.start();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        title.setText((position + 1) + "/" + albumList.size());
        refreshSelect(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }


}
