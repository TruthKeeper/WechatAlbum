package com.tk.wechatalbum.activity;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tk.wechatalbum.R;
import com.tk.wechatalbum.adapter.AlbumAdapter;
import com.tk.wechatalbum.adapter.FloderAdapter;
import com.tk.wechatalbum.bean.AlbumFolderBean;
import com.tk.wechatalbum.callback.OnFloderListener;
import com.tk.wechatalbum.fragment.AlbumFragment;
import com.tk.wechatalbum.ui.FloderItemDecoration;
import com.tk.wechatalbum.utils.FloderUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tk.wechatalbum.R.id.shadow_layout;


/**
 * Created by TK on 2016/8/10.
 */
public class AlbumActivity extends AppCompatActivity implements OnFloderListener, FloderAdapter.onFloderClickListener, AlbumAdapter.OnAlbumSelectListener {


    @BindView(R.id.folder_recyclerview)
    RecyclerView folderRecyclerview;
    @BindView(R.id.folder_text)
    TextView folderText;
    @BindView(R.id.preview_text)
    TextView previewText;
    @BindView(R.id.preview_layout)
    LinearLayout previewLayout;
    @BindView(R.id.shadow)
    View shadow;
    @BindView(shadow_layout)
    LinearLayout shadowLayout;
    private AlbumFragment albumFragment = new AlbumFragment();
    private FloderAdapter floderAdapter;
    //文件夹list
    private List<AlbumFolderBean> albumFolderList = new ArrayList<AlbumFolderBean>();
    private ValueAnimator showAnim;
    private ValueAnimator dismiss;
    private ArgbEvaluator shadowArgb;
    private boolean animLock;
    private boolean shadowFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        ButterKnife.bind(this);
        shadowArgb = new ArgbEvaluator();
        albumFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.main_album, albumFragment).commit();
        albumFragment.setOnFloderListener(this);
        albumFragment.setOnAlbumSelectListener(this);
    }

    @OnClick({R.id.folder_layout, R.id.preview_layout, R.id.shadow})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.folder_layout:
                //文件夹
                if (animLock || albumFolderList.size() == 1) {
                    return;
                }
                startFloderAnim();
                break;
            case R.id.preview_layout:
                //预览

                break;
            case R.id.shadow:
                if (shadowFlag) {
                    startFloderAnim();
                }
                break;
        }
    }

    /**
     * 切入切出动画
     */
    private void startFloderAnim() {
        if (folderRecyclerview.getVisibility() == View.GONE) {
            showAnim.start();
        } else {
            dismiss.start();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (folderRecyclerview.getVisibility() == View.VISIBLE) {
                startFloderAnim();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onFloderComplete(List<AlbumFolderBean> albumFolderList) {
        //取的数据后的回调，初始化相应操作
        folderRecyclerview.setHasFixedSize(true);
        folderRecyclerview.addItemDecoration(new FloderItemDecoration(this));
        folderRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        this.albumFolderList = albumFolderList;
        floderAdapter = new FloderAdapter(this, albumFolderList);
        floderAdapter.setOnFloderClickListener(this);
        folderRecyclerview.setAdapter(floderAdapter);
        FloderUtils.setFloderHeight(folderRecyclerview, 5);
        showAnim = ValueAnimator.ofFloat(1f, 0f);
        showAnim.setDuration(300);
        showAnim.addUpdateListener(animation -> changeUI((float) animation.getAnimatedValue()));
        showAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                animLock = true;
                folderRecyclerview.setVisibility(View.VISIBLE);
                shadowLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animLock = false;
                shadowFlag = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animLock = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        dismiss = ValueAnimator.ofFloat(0f, 1f);
        dismiss.setDuration(showAnim.getDuration());
        dismiss.addUpdateListener(animation -> changeUI((float) animation.getAnimatedValue()));
        dismiss.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                animLock = true;
                shadowFlag = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animLock = false;
                shadowLayout.setVisibility(View.INVISIBLE);
                folderRecyclerview.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animLock = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    /**
     * 动画动态改变位置
     *
     * @param value
     */
    private void changeUI(float value) {
        LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) folderRecyclerview.getLayoutParams();
        p.setMargins(0, 0, 0, -(int) (value * p.height));
        folderRecyclerview.setLayoutParams(p);
        shadowLayout.setBackgroundColor((Integer) shadowArgb.evaluate(value, 0x50000000, Color.TRANSPARENT));
    }

    @Override
    public void onClick(int position, boolean change) {
        dismiss.start();
        if (change) {
            albumFragment.setAlbumList(albumFolderList.get(position).getAlbumList(), position == 0);
        }
    }

    @Override
    public void onCamera() {
        Log.e("onCamera", "onCamera");
    }

    @Override
    public void onClick(int position) {
        Log.e("onClick", "onClick=" + position);
    }

    @Override
    public void onSelect(int select) {
        if (select != 0) {
            previewText.setEnabled(true);
            previewText.setText("预览(" + select + ")");
        } else {
            previewText.setText("预览");
            previewText.setEnabled(false);
        }
    }
}
