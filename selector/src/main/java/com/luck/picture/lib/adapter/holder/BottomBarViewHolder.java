package com.luck.picture.lib.adapter.holder;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.SelectorConfig;
import com.luck.picture.lib.config.SelectorProviders;
import com.luck.picture.lib.entity.LocalMedia;
import com.tap.intl.lib.intl_widget.widget.image.TapImagery;

public class BottomBarViewHolder extends RecyclerView.ViewHolder {
    private SelectorConfig config = SelectorProviders.getInstance().getSelectorConfig();
    private TapImagery ivPreview;
    private ImageView bg;
    private ImageView ivDelete;

    private LocalMedia data;

    private OnDeletePreviewListener onDeletePreviewListener;

    public boolean isShowDelete;

    public void setOnDeletePreviewListener(OnDeletePreviewListener onDeletePreviewListener) {
        this.onDeletePreviewListener = onDeletePreviewListener;
    }

    public BottomBarViewHolder(@NonNull View itemView) {
        super(itemView);

        ivPreview = itemView.findViewById(R.id.iv_preview);
        bg = itemView.findViewById(R.id.iv_background);
        ivDelete = itemView.findViewById(R.id.iv_delete);

        if (data != null && onDeletePreviewListener != null) {
            onDeletePreviewListener.delete(data);
        }
    }

    public void setData(LocalMedia localMedia) {
        if (localMedia == null) return;

        ivPreview.loadUrl(localMedia.getPath(), tapImagery -> null);

        if (localMedia.isPreview()) {
            bg.setVisibility(View.VISIBLE);
        } else  {
            bg.setVisibility(View.GONE);
        }

        if (isShowDelete) {
            ivDelete.setVisibility(View.VISIBLE);
        } else {
            ivDelete.setVisibility(View.GONE);
        }
    }

    public interface OnDeletePreviewListener {
        void delete(LocalMedia localMedia);
    }
}
