package com.tk.wechatalbum;


import static com.tk.wechatalbum.Constants.PhotoPickConstants.CHECK_LIMIT;
import static com.tk.wechatalbum.Constants.PreAlbumConstants.INDEX;

/**
 * Created by TK on 2016/9/30.
 */

public final class Constants {
    /**
     * PhotoPick参数
     */
    public static final class PhotoPickConstants {
        public static final int CAMERA_REQUEST = 10000;
        public static final int ALBUM_REQUEST = 10001;
        public static final int DEFAULT_LIMIT = 9;
        public static final String START_CAMERA = "start_camera";
        public static final String IS_SINGLE = "is_single";
        public static final String CHECK_LIMIT = "check_limit";
        public static final String SHOW_CAMERA = "show_camera";
        public static final String NEED_CLIP = "need_clip";
        //回调图片结果 flag=true data为单张，false，data为list，仅对结果处理，不与IS_SINGLE对应
        public static final String RESULT_SINGLE = "result_single";
        public static final String RESULT_DATA = "result_data";
    }

    /**
     * 相册 to 相册预览传递参数
     */
    public static final class PreAlbumConstants {
        public static final int PRE_REQUEST = 10002;
        //预览列表
        public static final String ALBUM_LIST = "album_list";
        //选中列表
        public static final String CHECK_LIST = "check_list";
        //当前index
        public static final String INDEX = "index";
        //选择限制
        public static final String LIMIT = CHECK_LIMIT;
        //回调是否完成选择
        public static final String FINISH = "finish";
    }


    /**
     * 相册 to 普通预览传递参数
     */
    public static final String PRE_LIST = "preview_list";
    public static final String PRE_INDEX = INDEX;
    //回调修改结果
    public static final String RES_LIST = "result_list";

}
