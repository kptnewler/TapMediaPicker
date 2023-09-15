package com.luck.picture.lib.config;

import android.content.Context;

import com.luck.picture.lib.PictureSelectorFragment;
import com.luck.picture.lib.PictureSelectorPreviewFragment;
import com.luck.picture.lib.R;
import com.luck.picture.lib.adapter.PictureAlbumAdapter;
import com.luck.picture.lib.adapter.PictureImageGridAdapter;
import com.luck.picture.lib.adapter.PicturePreviewAdapter;
import com.luck.picture.lib.adapter.holder.PreviewGalleryAdapter;

/**
 * @author：luck
 * @date：2021/12/23 10:50 上午
 * @describe：InjectResourceSource
 */
public final class InjectResourceSource {
    public static final int DEFAULT_LAYOUT_RESOURCE = 0;

    public static final int MAIN_SELECTOR_LAYOUT_RESOURCE = 1;

    public static final int PREVIEW_LAYOUT_RESOURCE = 2;

    public static final int MAIN_ITEM_IMAGE_LAYOUT_RESOURCE = 3;

    public static final int MAIN_ITEM_VIDEO_LAYOUT_RESOURCE = 4;

    public static final int MAIN_ITEM_AUDIO_LAYOUT_RESOURCE = 5;

    public static final int ALBUM_ITEM_LAYOUT_RESOURCE = 6;

    public static final int PREVIEW_ITEM_IMAGE_LAYOUT_RESOURCE = 7;

    public static final int PREVIEW_ITEM_VIDEO_LAYOUT_RESOURCE = 8;

    public static final int PREVIEW_GALLERY_ITEM_LAYOUT_RESOURCE = 9;

    public static final int PREVIEW_ITEM_AUDIO_LAYOUT_RESOURCE = 10;

    public static int getLayoutResource(Context context, int resourceSource, SelectorConfig selectorConfig) {
        if (selectorConfig != null && selectorConfig.onLayoutResourceListener != null) {
            return selectorConfig.onLayoutResourceListener.getLayoutResourceId(context, resourceSource);
        }
        return DEFAULT_LAYOUT_RESOURCE;
    }
}
