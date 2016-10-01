package com.tk.wechatalbum.activity;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import com.tk.wechatalbum.adapter.FolderAdapter;
import com.tk.wechatalbum.bean.AlbumBean;
import com.tk.wechatalbum.bean.AlbumFolderBean;
import com.tk.wechatalbum.callback.OnFolderListener;
import com.tk.wechatalbum.fragment.AlbumFragment;
import com.tk.wechatalbum.ui.ConfirmButton;
import com.tk.wechatalbum.ui.FolderItemDecoration;
import com.tk.wechatalbum.utils.FolderUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tk.wechatalbum.Constants.PhotoPickConstants;
import static com.tk.wechatalbum.Constants.PreAlbumConstants;


/**
 * Created by TK on 2016/8/10.
 */
public class AlbumActivity extends AppCompatActivity implements OnFolderListener, FolderAdapter.onFolderClickListener, AlbumAdapter.OnAlbumSelectListener {

    @BindView(R.id.confirm_btn)
    ConfirmButton confirmBtn;
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
    @BindView(R.id.shadow_layout)
    LinearLayout shadowLayout;
    private AlbumFragment albumFragment = new AlbumFragment();
    private FolderAdapter folderAdapter;
    //文件夹list
    private List<AlbumFolderBean> albumFolderList = new ArrayList<AlbumFolderBean>();
    private ValueAnimator showAnim;
    private ValueAnimator dismiss;
    private ArgbEvaluator shadowArgb;
    private boolean animLock;
    private boolean shadowFlag;
    private Bundle bundle;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        ButterKnife.bind(this);
        initConstants();

        shadowArgb = new ArgbEvaluator();
        //继续传递
        albumFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.main_album, albumFragment).commit();
        albumFragment.setOnFolderListener(this);
        albumFragment.setOnAlbumSelectListener(this);

    }

    /**
     * 接收并处理PhotoPick的配置
     */
    private void initConstants() {
        bundle = getIntent().getExtras();
        if (bundle.getBoolean(PhotoPickConstants.IS_SINGLE)) {
            //单选模式
            previewLayout.setVisibility(View.GONE);
            confirmBtn.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.back, R.id.confirm_btn, R.id.folder_layout, R.id.preview_layout, R.id.shadow})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.confirm_btn:
                //完成选择，回调
                Intent intent = new Intent();
                List<AlbumBean> checkList = albumFragment.getSelectList();
                if (bundle.getInt(PhotoPickConstants.CHECK_LIMIT) == 1
                        || checkList.size() == 1) {
                    intent.putExtra(PhotoPickConstants.RESULT_SINGLE, true);
                    intent.putExtra(PhotoPickConstants.RESULT_DATA, checkList.get(0).getPath());
                } else {
                    intent.putExtra(PhotoPickConstants.RESULT_SINGLE, false);
                    intent.putParcelableArrayListExtra(PhotoPickConstants.RESULT_DATA, new ArrayList<>(checkList));
                }
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            case R.id.folder_layout:
                //文件夹
                if (animLock || albumFolderList.size() == 1) {
                    return;
                }
                startFolderAnim();
                break;
            case R.id.preview_layout:
                //预览已选中的Album List
                if (albumFragment.getSelectList().size() == 0) {
                    return;
                }
                Intent preIntent = new Intent(this, AlbumPreActivity.class);
                preIntent.putParcelableArrayListExtra(PreAlbumConstants.ALBUM_LIST, new ArrayList<>(albumFragment.getSelectList()));
                preIntent.putParcelableArrayListExtra(PreAlbumConstants.CHECK_LIST, new ArrayList<>(albumFragment.getSelectList()));
                preIntent.putExtra(PreAlbumConstants.INDEX, 0);
                preIntent.putExtra(PreAlbumConstants.LIMIT, bundle.getInt(PhotoPickConstants.CHECK_LIMIT));
                startActivityForResult(preIntent, PreAlbumConstants.PRE_REQUEST);
                break;
            case R.id.shadow:
                if (shadowFlag) {
                    startFolderAnim();
                }
                break;
        }
    }

    /**
     * 切入切出动画
     */
    private void startFolderAnim() {
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
                startFolderAnim();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onFolderComplete(List<AlbumFolderBean> albumFolderList) {
        //取的数据后的回调，初始化相应操作
        folderRecyclerview.setHasFixedSize(true);
        folderRecyclerview.addItemDecoration(new FolderItemDecoration(this));
        folderRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        this.albumFolderList = albumFolderList;
        folderAdapter = new FolderAdapter(this, albumFolderList);
        folderAdapter.setOnFolderClickListener(this);
        folderRecyclerview.setAdapter(folderAdapter);
        //// TODO: 2016/9/30 顶破天？
        FolderUtils.setFolderHeight(folderRecyclerview, 5);
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
        handler.postDelayed(() -> {
            dismiss.start();
            if (change) {
                folderText.setText(albumFolderList.get(position).getFolderName());
                albumFragment.setAlbumList(albumFolderList.get(position).getAlbumList(), position == 0);
            }
        }, 350);

    }

    @Override
    public void onCamera() {
        Log.e("onCamera", "onCamera");
    }

    @Override
    public void onClick(int position) {
        if (bundle.getInt(PhotoPickConstants.CHECK_LIMIT) != 1) {
            //预览AlbumList
            Intent intent = new Intent(this, AlbumPreActivity.class);
            intent.putParcelableArrayListExtra(PreAlbumConstants.ALBUM_LIST, new ArrayList<>(albumFragment.getAlbumList()));
            intent.putParcelableArrayListExtra(PreAlbumConstants.CHECK_LIST, new ArrayList<>(albumFragment.getSelectList()));
            intent.putExtra(PreAlbumConstants.INDEX, position);
            intent.putExtra(PreAlbumConstants.LIMIT, bundle.getInt(PhotoPickConstants.CHECK_LIMIT));
            startActivityForResult(intent, PreAlbumConstants.PRE_REQUEST);
        } else {
            if (bundle.getBoolean(PhotoPickConstants.NEED_CLIP, false)) {
                //裁剪后再回调

            } else {
                //直接回调
                Intent data = new Intent();
                data.putExtra(PhotoPickConstants.RESULT_SINGLE, true);
                data.putExtra(PhotoPickConstants.RESULT_DATA, albumFragment.getAlbumList().get(position).getPath());
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        }
    }

    @Override
    public void onSelect(int select) {
        //点击触发刷新ui
        if (select != 0) {
            previewText.setText("预览(" + select + ")");
            if (bundle.getInt(PhotoPickConstants.CHECK_LIMIT, 1) == 1 && select == 1) {
                confirmBtn.setText("完成");
            } else {
                confirmBtn.setText("完成(" + select + "/" + bundle.getInt(PhotoPickConstants.CHECK_LIMIT) + ")");
            }
            previewText.setEnabled(true);
            confirmBtn.setEnabled(true);
        } else {
            previewText.setEnabled(false);
            confirmBtn.setEnabled(false);
            previewText.setText("预览");
            confirmBtn.setText("完成");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PreAlbumConstants.PRE_REQUEST) {
            if (data.getBooleanExtra(PreAlbumConstants.FINISH, false)) {
                //完成了选择
                List<AlbumBean> checkList = data.getParcelableArrayListExtra(PreAlbumConstants.CHECK_LIST);
                Intent intent = new Intent();
                if (checkList.size() == 1) {
                    intent.putExtra(PhotoPickConstants.RESULT_SINGLE, true);
                    intent.putExtra(PhotoPickConstants.RESULT_DATA, checkList.get(0).getPath());
                } else {
                    intent.putExtra(PhotoPickConstants.RESULT_SINGLE, false);
                    intent.putParcelableArrayListExtra(PhotoPickConstants.RESULT_DATA, new ArrayList<>(checkList));
                }
                setResult(Activity.RESULT_OK, intent);
                finish();
            } else {
                //刷新更改的
                List<AlbumBean> checkList = data.getParcelableArrayListExtra(PreAlbumConstants.CHECK_LIST);
                albumFragment.setCheckList(checkList);
                onSelect(checkList.size());
            }
        }
    }


}
