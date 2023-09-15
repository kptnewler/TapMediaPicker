package com.luck.picture.lib.adapter.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectorProviders;
import com.luck.picture.lib.style.SelectMainStyle;
import com.luck.picture.lib.utils.StyleUtils;

/**
 * @author：luck
 * @date：2021/11/20 3:54 下午
 * @describe：CameraViewHolder
 */
public class CameraViewHolder extends BaseRecyclerMediaHolder {

    public CameraViewHolder(View itemView) {
        super(itemView);
        ImageView tvCamera = itemView.findViewById(R.id.tvCamera);
        selectorConfig = SelectorProviders.getInstance().getSelectorConfig();
        SelectMainStyle adapterStyle = selectorConfig.selectorStyle.getSelectMainStyle();
        int background = adapterStyle.getAdapterCameraBackgroundColor();
        if (StyleUtils.checkStyleValidity(background)) {
            tvCamera.setBackgroundColor(background);
        }
    }

}
