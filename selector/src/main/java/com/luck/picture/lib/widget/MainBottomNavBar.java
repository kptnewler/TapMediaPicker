package com.luck.picture.lib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.luck.picture.lib.R;

public class MainBottomNavBar extends BottomNavBar {
    public CompleteSelectView completeSelectView;
    public MainBottomNavBar(Context context) {
        super(context);
    }

    public MainBottomNavBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainBottomNavBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        super.init();
        completeSelectView = findViewById(R.id.complete_select_view);
        rvPreviewList = findViewById(R.id.rv_preview);
    }

    @Override
    protected void inflateLayout() {
        inflate(getContext(), R.layout.ps_media_preview_bottom_nav_layout, this);
    }
}
