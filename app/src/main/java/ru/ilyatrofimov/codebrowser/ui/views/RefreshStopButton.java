package ru.ilyatrofimov.codebrowser.ui.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import ru.ilyatrofimov.codebrowser.R;

/**
 * Refresh/Stop Button View
 *
 * @author Ilya Trofimov
 */
public class RefreshStopButton extends ImageButton {
    public static final int REFRESH_STATE = 1;
    public static final int STOP_STATE = 2;

    private Drawable mRefreshDrawable;
    private Drawable mStopDrawable;
    private int mState;
    private int mAnimDuration;

    public RefreshStopButton(Context context) {
        super(context);
        init();
    }

    public RefreshStopButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreshStopButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        this.mState = RefreshStopButton.REFRESH_STATE;
        this.mAnimDuration = getResources().getInteger(R.integer.refresh_icon_switch_anim_duration) / 2;
        this.mRefreshDrawable = ContextCompat.getDrawable(this.getContext(), R.drawable.refresh);
        this.mStopDrawable = ContextCompat.getDrawable(this.getContext(), R.drawable.stop);
    }

    public void setState(int state) {
        if (this.mState != state) {
            switch (state) {
                case REFRESH_STATE:
                    switchDrawableWithAnimation(mStopDrawable, mRefreshDrawable);
                    this.mState = REFRESH_STATE;
                    break;
                case STOP_STATE:
                    switchDrawableWithAnimation(mRefreshDrawable, mStopDrawable);
                    this.mState = STOP_STATE;
                    break;
            }
        }
    }

    private void switchDrawableWithAnimation(Drawable drawableFrom, final Drawable drawableTo) {
        this.setImageDrawable(drawableFrom);

        AnimatorSet hideAnim = prepareAnimatorSet(this, true, mAnimDuration);
        hideAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                RefreshStopButton.this.setImageDrawable(drawableTo);
                AnimatorSet showAnim
                        = prepareAnimatorSet(RefreshStopButton.this, false, mAnimDuration);
                showAnim.start();
            }
        });

        hideAnim.start();
    }

    /**
     * Prepares set of animations
     *
     * @param target object that will be animated
     * @param hide play disappearing animation if True, appearing animation otherwise
     * @param duration duration of the animations in ms
     * @return AnimatorSet
     */
    private AnimatorSet prepareAnimatorSet(Object target, boolean hide, int duration) {
        AnimatorSet animSet = new AnimatorSet();
        animSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animSet.setDuration(duration);
        animSet.playTogether(
                ObjectAnimator.ofFloat(target, "alpha", hide ? 1f : 0.5f, hide ? 0.5f : 1f),
                ObjectAnimator.ofFloat(target, "rotation", hide ? 0f : 180f, hide ? 180f : 360f),
                ObjectAnimator.ofFloat(target, "scaleX", hide ? 1f : 0.5f, hide ? 0.5f : 1f),
                ObjectAnimator.ofFloat(target, "scaleY", hide ? 1f : 0.5f, hide ? 0.5f : 1f)
        );

        return animSet;
    }
}
