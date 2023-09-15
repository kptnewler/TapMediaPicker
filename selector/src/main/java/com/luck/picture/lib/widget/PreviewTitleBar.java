package com.luck.picture.lib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.SelectorProviders;
import com.luck.picture.lib.style.TitleBarStyle;
import com.luck.picture.lib.utils.StyleUtils;

/**
 * @author：luck
 * @date：2021/11/19 4:38 下午
 * @describe：PreviewTitleBar
 */
public class PreviewTitleBar extends TitleBar {

    public PreviewTitleBar(Context context) {
        super(context);
    }

    public PreviewTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PreviewTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        super.init();
        rlAlbumBg.setOnClickListener(null);
        viewAlbumClickArea.setOnClickListener(null);
        rlAlbumBg.setBackgroundResource(R.drawable.ps_ic_trans_1px);
        ivArrow.setVisibility(GONE);
        viewAlbumClickArea.setVisibility(GONE);
        ivLeftBack.setImageResource(com.tap.intl.lib.intl_widget.R.drawable.ico_20_top_bars_backward_left);
    }
}
