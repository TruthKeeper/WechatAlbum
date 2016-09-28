package com.tk.wechatalbum.callback;

import com.tk.wechatalbum.bean.AlbumFolderBean;

import java.util.List;

/**
 * Created by TK on 2016/9/27.
 */

public interface OnFloderListener {
    void onFloderComplete(List<AlbumFolderBean> albumFolderList);
}
