package com.luck.picture.lib.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.SelectorConfig;
import com.luck.picture.lib.config.SelectorProviders;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.luck.picture.lib.style.SelectMainStyle;
import com.luck.picture.lib.utils.StyleUtils;
import com.luck.picture.lib.utils.ValueOf;

/**
 * @author：luck
 * @date：2021/11/21 11:28 下午
 * @describe：CompleteSelectView
 */
public class CompleteSelectView extends FrameLayout {
    private TextView tvComplete;
    private SelectorConfig config;

    public CompleteSelectView(Context context) {
        super(context);
        init();
    }

    public CompleteSelectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CompleteSelectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflateLayout();
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tvComplete = findViewById(R.id.ps_tv_complete);
        config = SelectorProviders.getInstance().getSelectorConfig();
    }

    protected void inflateLayout() {
        inflate(getContext(), R.layout.ps_complete_selected_layout, this);
    }

    /**
     * 选择结果发生变化
     */
    @SuppressLint("SetTextI18n")
    public void setSelectedChange(boolean isPreview) {
        if (config.getSelectCount() > 0) {
            setVisibility(VISIBLE);
        } else {
            setVisibility(GONE);
            tvComplete.setText(getContext().getString(R.string.ps_select_bottom_next) + "(" + config.getSelectCount() + ")");
        }
    }
}
