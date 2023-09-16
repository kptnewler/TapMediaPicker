package com.luck.picture.lib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.SelectorProviders;
import com.luck.picture.lib.style.BottomNavBarStyle;
import com.luck.picture.lib.utils.StyleUtils;

/**
 * @author：luck
 * @date：2021/11/17 10:46 上午
 * @describe：PreviewBottomNavBar
 */
public class PreviewBottomNavBar extends BottomNavBar {
    protected ImageView tvImageEditor;

    protected ImageView tvImageDelete;

    public PreviewBottomNavBar(Context context) {
        super(context);
    }

    public PreviewBottomNavBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PreviewBottomNavBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        super.init();
        tvImageEditor = findViewById(R.id.iv_editor);
        tvImageDelete = findViewById(R.id.iv_delete);

        tvImageEditor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomNavBarListener != null) {
                    bottomNavBarListener.onEditImage();
                }
            }
        });
        tvImageDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomNavBarListener != null) {
                    bottomNavBarListener.onGlobalDelete();
                }
            }
        });
    }

    public void isDisplayEditor(boolean isHasVideo) {
        tvImageEditor.setVisibility(config.onEditMediaEventListener != null && !isHasVideo ? View.VISIBLE : GONE);
    }

    @Override
    protected void inflateLayout() {
        inflate(getContext(), R.layout.ps_media_preview_bottom_nav_layout, this);
    }

    public ImageView getEditor() {
        return tvImageEditor;
    }
}
