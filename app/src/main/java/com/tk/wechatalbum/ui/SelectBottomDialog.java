package com.tk.wechatalbum.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.tk.wechatalbum.R;

/**
 * Created by TK on 2016/8/9.
 */
public class SelectBottomDialog extends BottomSheetDialog {
    private View contentView;
    private OnSelectListener onSelectListener;

    public SelectBottomDialog(Context context) {
        this(context, 0);
    }

    public SelectBottomDialog(Context context, int theme) {
        super(context, theme);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_test, null);
        setContentView(contentView);
        contentView.findViewById(R.id.btn_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSelectListener != null) {
                    onSelectListener.onCamera();
                }
            }
        });
        contentView.findViewById(R.id.btn_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSelectListener != null) {
                    onSelectListener.onAlbum();
                }
            }
        });
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public interface OnSelectListener {
        void onCamera();

        void onAlbum();
    }
}
