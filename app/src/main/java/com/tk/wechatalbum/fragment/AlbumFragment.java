package com.tk.wechatalbum.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.tk.wechatalbum.R;
import com.tk.wechatalbum.adapter.AlbumAdapter;
import com.tk.wechatalbum.adapter.AlbumAdapter.OnAlbumSelectListener;
import com.tk.wechatalbum.bean.AlbumBean;
import com.tk.wechatalbum.bean.AlbumFolderBean;
import com.tk.wechatalbum.callback.OnFolderListener;
import com.tk.wechatalbum.callback.OnLoadAlbumListener;
import com.tk.wechatalbum.ui.AlbumItemDecoration;
import com.tk.wechatalbum.utils.AlbumUtils;
import com.tk.wechatalbum.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.tk.wechatalbum.Constants.PhotoPickConstants;

/**
 * Created by TK on 2016/9/24.
 */

public class AlbumFragment extends Fragment implements OnLoadAlbumListener {

    @BindView(R.id.photo_tip)
    TextView photoTip;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    private GridLayoutManager gridLayoutManager;
    private Unbinder unbinder;
    private Context mContext;
    private AlbumAdapter albumAdapter;
    //recyclerview list
    private List<AlbumBean> albumList = new ArrayList<AlbumBean>();
    private Bundle bundle;
    private AlphaAnimation showAnim;
    private AlphaAnimation dismissAnim;
    private OnFolderListener onFolderListener;
    private OnAlbumSelectListener onAlbumSelectListener;
    private OnScrollListener onScrollListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        bundle = getArguments();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showAnim = new AlphaAnimation(0f, 1f);
        showAnim.setDuration(750);
        dismissAnim = new AlphaAnimation(1f, 0f);
        dismissAnim.setDuration(750);
        gridLayoutManager = new GridLayoutManager(mContext, 3);
        recyclerview.setLayoutManager(gridLayoutManager);
        recyclerview.setHasFixedSize(true);
        recyclerview.addItemDecoration(new AlbumItemDecoration(mContext, 3));
        albumAdapter = new AlbumAdapter(mContext,
                albumList,
                bundle.getInt(PhotoPickConstants.CHECK_LIMIT),
                bundle.getBoolean(PhotoPickConstants.IS_SINGLE));
        albumAdapter.setOnAlbumSelectListener(onAlbumSelectListener);
        recyclerview.setAdapter(albumAdapter);
        onScrollListener = new OnScrollListener();
        recyclerview.addOnScrollListener(onScrollListener);
        //初始化数据源
        AlbumUtils.initAlbumData(getActivity(), this);
    }

    /**
     * load加载完毕，只调用一次
     *
     * @param albumList
     * @param albumFolderList
     */
    @Override
    public void onComplete(List<AlbumBean> albumList, List<AlbumFolderBean> albumFolderList) {
        this.albumList.clear();
        this.albumList.addAll(albumList);
        //防止界面闪烁，后续设置
        albumAdapter.setShowCamera(bundle.getBoolean(PhotoPickConstants.SHOW_CAMERA, false));
        albumAdapter.notifyDataSetChanged();
        if (onFolderListener != null) {
            onFolderListener.onFolderComplete(albumFolderList);
        }
    }

    @Override
    public void onDestroyView() {
        if (onScrollListener != null) {
            recyclerview.removeOnScrollListener(onScrollListener);
            onScrollListener = null;
        }
        super.onDestroyView();
        unbinder.unbind();

    }

    /**
     * 得到选中的album集合
     *
     * @return
     */
    public List<AlbumBean> getSelectList() {
        return albumAdapter.getCheckList();
    }

    /**
     * 切换list集合
     *
     * @param albumList
     * @param firstIndex
     */
    public void setAlbumList(List<AlbumBean> albumList, boolean firstIndex) {
        this.albumList.clear();
        this.albumList.addAll(albumList);
        albumAdapter.setShowCamera(firstIndex && bundle.getBoolean(PhotoPickConstants.SHOW_CAMERA, false));
        albumAdapter.notifyDataSetChanged();
        recyclerview.scrollToPosition(0);
    }

    private class OnScrollListener extends RecyclerView.OnScrollListener {
        public OnScrollListener() {
            super();
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            //顶部时间条的显示
            //// TODO: 2016/9/28
            if (newState == RecyclerView.SCROLL_STATE_IDLE
                    && photoTip.getVisibility() == View.VISIBLE) {
                photoTip.setVisibility(View.GONE);
                photoTip.clearAnimation();
                photoTip.startAnimation(dismissAnim);
            } else if (photoTip.getVisibility() == View.GONE
                    && albumList.size() != 0) {
                photoTip.setVisibility(View.VISIBLE);
                photoTip.clearAnimation();
                photoTip.startAnimation(showAnim);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int position = gridLayoutManager.findFirstVisibleItemPosition();
            if (albumList.size() != 0) {
                photoTip.setText(DateUtils.getDateStr(albumList.get(position).getDate()));
            }
        }
    }

    public List<AlbumBean> getAlbumList() {
        return albumList;
    }

    public void setCheckList(List<AlbumBean> checkList) {
        albumAdapter.setCheckList(checkList);
    }

    public void setOnAlbumSelectListener(OnAlbumSelectListener onAlbumSelectListener) {
        this.onAlbumSelectListener = onAlbumSelectListener;
    }

    public void setOnFolderListener(OnFolderListener onFolderListener) {
        this.onFolderListener = onFolderListener;
    }
}
