package com.tk.wechatalbum.callback;

import com.tk.wechatalbum.bean.AlbumFolderBean;

import java.util.List;

/**
 * Created by TK on 2016/9/27.
 * 文件夹数据回调
 */

public interface OnFolderListener {

    void onFolderComplete(List<AlbumFolderBean> albumFolderList);
}
