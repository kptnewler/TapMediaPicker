package com.luck.picture.lib.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.luck.picture.lib.R;
import com.luck.picture.lib.adapter.holder.PreviewGalleryAdapter;
import com.luck.picture.lib.basic.PictureCommonFragment;
import com.luck.picture.lib.config.SelectorConfig;
import com.luck.picture.lib.decoration.WrapContentLinearLayoutManager;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.utils.ActivityCompatHelper;

import java.util.Collections;
import java.util.List;

/**
 * @author：luck
 * @date：2021/11/17 10:46 上午
 * @describe：BottomNavBar
 */
public class BottomNavBar extends ConstraintLayout {
    protected SelectorConfig config;

    public PreviewGalleryAdapter mGalleryAdapter;

    public RecyclerView rvPreviewList;

    protected OnBottomNavBarListener bottomNavBarListener;

    protected List<LocalMedia> previewList;

    boolean needScaleBig;
    boolean needScaleSmall;

    public FragmentActivity activity;

    public BottomNavBar(Context context) {
        super(context);
        init();
    }

    public BottomNavBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomNavBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        inflateLayout();
        setClickable(true);
        setFocusable(true);
        setBackgroundColor(ContextCompat.getColor(getContext(), com.tap.intl.lib.intl_widget.R.color.black_opacity10));
    }

    public void setData(FragmentActivity activity, List<LocalMedia> previewList, boolean isShowDelete) {
        this.activity = activity;
        this.previewList = previewList;
        if (previewList != null) {
            initPreviewSelectGallery();
        }
    }

    protected void initPreviewSelectGallery() {
        if (rvPreviewList == null) return;
        mGalleryAdapter = new PreviewGalleryAdapter(config, true);
        rvPreviewList.setAdapter(mGalleryAdapter);
        WrapContentLinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(getContext()) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                super.smoothScrollToPosition(recyclerView, state, position);
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return 300F / displayMetrics.densityDpi;
                    }
                };
                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }
        };
        rvPreviewList.setLayoutManager(layoutManager);
        RecyclerView.ItemAnimator itemAnimator = rvPreviewList.getItemAnimator();
        if (itemAnimator != null) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
        }
        layoutManager.setOrientation(WrapContentLinearLayoutManager.HORIZONTAL);
        rvPreviewList.setLayoutManager(layoutManager);
        if (config.getSelectCount() > 0) {
            rvPreviewList.setLayoutAnimation(AnimationUtils
                    .loadLayoutAnimation(getContext(), R.anim.ps_anim_layout_fall_enter));
        }
        if (config.getSelectCount() > 0) {
            rvPreviewList.setVisibility(View.VISIBLE);
        } else {
            rvPreviewList.setVisibility(View.INVISIBLE);
        }
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            }

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                viewHolder.itemView.setAlpha(0.7F);
                return makeMovementFlags(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                try {
                    //得到item原来的position
                    int fromPosition = viewHolder.getAdapterPosition();
                    //得到目标position
                    int toPosition = target.getAdapterPosition();
                    if (fromPosition < toPosition) {
                        for (int i = fromPosition; i < toPosition; i++) {
                            Collections.swap(mGalleryAdapter.getData(), i, i + 1);
                            Collections.swap(config.getSelectedResult(), i, i + 1);
                            Collections.swap(previewList, i, i + 1);

                        }
                    } else {
                        for (int i = fromPosition; i > toPosition; i--) {
                            Collections.swap(mGalleryAdapter.getData(), i, i - 1);
                            Collections.swap(config.getSelectedResult(), i, i - 1);
                            Collections.swap(previewList, i, i - 1);
                        }
                    }
                    mGalleryAdapter.notifyItemMoved(fromPosition, toPosition);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (needScaleBig) {
                    needScaleBig = false;
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(
                            ObjectAnimator.ofFloat(viewHolder.itemView, "scaleX", 1.0F, 1.1F),
                            ObjectAnimator.ofFloat(viewHolder.itemView, "scaleY", 1.0F, 1.1F));
                    animatorSet.setDuration(50);
                    animatorSet.setInterpolator(new LinearInterpolator());
                    animatorSet.start();
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            needScaleSmall = true;
                        }
                    });
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
            }

            @Override
            public long getAnimationDuration(@NonNull RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
                return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                viewHolder.itemView.setAlpha(1.0F);
                if (needScaleSmall) {
                    needScaleSmall = false;
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(
                            ObjectAnimator.ofFloat(viewHolder.itemView, "scaleX", 1.1F, 1.0F),
                            ObjectAnimator.ofFloat(viewHolder.itemView, "scaleY", 1.1F, 1.0F));
                    animatorSet.setInterpolator(new LinearInterpolator());
                    animatorSet.setDuration(50);
                    animatorSet.start();
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            needScaleBig = true;
                        }
                    });
                }
                super.clearView(recyclerView, viewHolder);
                mGalleryAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                int position = mGalleryAdapter.getLastCheckPosition();
                bottomNavBarListener.swap(position);
                if (config.selectorStyle.getSelectMainStyle().isSelectNumberStyle()) {
                    if (!ActivityCompatHelper.isDestroy(activity)) {
                        List<Fragment> fragments = activity.getSupportFragmentManager().getFragments();
                        for (int i = 0; i < fragments.size(); i++) {
                            Fragment fragment = fragments.get(i);
                            if (fragment instanceof PictureCommonFragment) {
                                ((PictureCommonFragment) fragment).sendChangeSubSelectPositionEvent(true);
                            }
                        }
                    }
                }
            }
        });
        mItemTouchHelper.attachToRecyclerView(rvPreviewList);
        mGalleryAdapter.setItemLongClickListener(new PreviewGalleryAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(RecyclerView.ViewHolder holder, int position, View v) {
                Vibrator vibrator = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(50);
                if (mGalleryAdapter.getItemCount() != config.maxSelectNum) {
                    mItemTouchHelper.startDrag(holder);
                    return;
                }
                if (holder.getLayoutPosition() != mGalleryAdapter.getItemCount() - 1) {
                    mItemTouchHelper.startDrag(holder);
                }
            }
        });

        mGalleryAdapter.setItemOnDeleteListener(new PreviewGalleryAdapter.OnItemDeleteListener() {
            @Override
            public void onItemDelete(LocalMedia localMedia, int pos, View view) {
                bottomNavBarListener.onDeleteSelectedImage(localMedia, pos);
            }
        });
    }


    protected void inflateLayout() {
    }

    /**
     * 预览NarBar的功能事件回调
     *
     * @param listener
     */
    public void setOnBottomNavBarListener(OnBottomNavBarListener listener) {
        this.bottomNavBarListener = listener;
    }

    public static class OnBottomNavBarListener {

        /**
         * 编辑图片
         */
        public void onEditImage() {

        }


        public void onDeleteSelectedImage(LocalMedia localMedia, int pos) {

        }

        public void onGlobalDelete() {

        }

        public void swap(int pos) {

        }
    }
}
