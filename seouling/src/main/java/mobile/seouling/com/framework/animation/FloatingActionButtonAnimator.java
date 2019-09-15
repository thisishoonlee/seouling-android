package mobile.seouling.com.framework.animation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class FloatingActionButtonAnimator {
    private static final int FAB_APPEARANCE_ANIM_DURATION = 200;

    @Nullable
    private FloatingActionButton mFab;

    private Animator mFabAnimator;

    private boolean mFabSet;
    private int mFabIconResId;

    public FloatingActionButtonAnimator(@Nullable FloatingActionButton fab) {
        mFab = fab;
    }

    public void setFabIcon(int resId, View.OnClickListener clickListener) {
        if (mFab == null) { return; }
        boolean wasFabSet = mFabSet;
        mFabSet = true;
        // ensure mFabView
        if (!wasFabSet) {
            if (resId != 0) {
                mFab.show();
                mFab.setImageResource(resId);
            } else {
                mFab.hide();
                mFab.setImageDrawable(null);
            }
            mFabIconResId = resId;
            mFab.setOnClickListener(clickListener);
            return;
        }
        mFab.setOnClickListener(clickListener);
        if (mFabAnimator != null) {
            mFabAnimator.end();
        }
        if (mFabIconResId != resId || mFab.getVisibility() != View.VISIBLE) {
            ArrayList<Animator> anims = new ArrayList<>();
            boolean disappear = mFabIconResId != 0 && mFab.getVisibility() == View.VISIBLE;
            boolean appear = resId != 0;
            if (disappear) {
                anims.add(createFabAnimator(false, resId));
            }
            if (appear) {
                anims.add(createFabAnimator(true, resId));
            }
            if (!anims.isEmpty()) {
                AnimatorSet animSet = new AnimatorSet();
                animSet.playSequentially(anims);
                animSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mFab.setClickable(false);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (mFabAnimator == animation) {
                            mFab.setClickable(true);
                            mFabAnimator = null;
                        }
                    }
                });
                mFabAnimator = animSet;
                animSet.start();
            }
            mFabIconResId = resId;
//        } else if (resId != 0 && ViewHelper.getTranslationY(mFab) != 0) {
//            ObjectAnimator anim = ObjectAnimator.ofFloat(mFab, "translationY", 0);
//            anim.setDuration(FAB_APPEARANCE_ANIM_DURATION);
//            mFabAnimator = anim;
//            anim.start();
        }
    }

    private Animator createFabAnimator(boolean appear, int resId) {
        if (appear) {
            AnimatorSet appearAnim = new AnimatorSet();
            appearAnim.play(ObjectAnimator.ofFloat(mFab, "scaleX", 0, 1))
                    .with(ObjectAnimator.ofFloat(mFab, "scaleY", 0, 1));
            appearAnim.setDuration(FAB_APPEARANCE_ANIM_DURATION);
            appearAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (mFab == null) { return; }
                    mFab.setImageResource(resId);
                    mFab.show();
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }
            });
            return appearAnim;
        } else {
            AnimatorSet disappearAnim = new AnimatorSet();
            disappearAnim.play(ObjectAnimator.ofFloat(mFab, "scaleX", 1, 0))
                    .with(ObjectAnimator.ofFloat(mFab, "scaleY", 1, 0));
            disappearAnim.setDuration(FAB_APPEARANCE_ANIM_DURATION);
            disappearAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mFab == null) { return; }
                    mFab.hide();
                }
            });
            return disappearAnim;
        }
    }

    public void destroy() {
        if (mFabAnimator != null) {
            mFabAnimator.cancel();
            mFabAnimator = null;
        }
        mFab = null;
    }
}
