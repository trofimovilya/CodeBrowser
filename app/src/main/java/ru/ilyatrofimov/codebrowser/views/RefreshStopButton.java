package ru.ilyatrofimov.codebrowser.views;

import android.animation.Animator;
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
 * Created by ILYATTR on 02/05/16.
 */
public class RefreshStopButton extends ImageButton {
    public static final int REFRESH_STATE = 1;
    public static final int STOP_STATE = 2;

    private Drawable mRefreshDrawable;
    private Drawable mStopDrawable;
    private int mState;

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

    public int getState() {
        return this.mState;
    }

    private void switchDrawableWithAnimation(Drawable drawableFrom, final Drawable drawableTo) {
        this.setImageDrawable(drawableFrom);

        AnimatorSet hideAnimator = prepareAnimatorSet(this, true);
        hideAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                RefreshStopButton.this.setImageDrawable(drawableTo);

                AnimatorSet showAnimator = prepareAnimatorSet(RefreshStopButton.this, false);
                showAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        hideAnimator.start();
    }

    private AnimatorSet prepareAnimatorSet(Object target, boolean hide) {
        AnimatorSet animSet = new AnimatorSet();
        animSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animSet.setDuration(200);
        animSet.playTogether(
                ObjectAnimator.ofFloat(target, "alpha", hide ? 1f : 0.5f, hide ? 0.5f : 1f),
                ObjectAnimator.ofFloat(target, "rotation", hide ? 0f : 180f, hide ? 180f : 360f),
                ObjectAnimator.ofFloat(target, "scaleX", hide ? 1f : 0.5f, hide ? 0.5f : 1f),
                ObjectAnimator.ofFloat(target, "scaleY", hide ? 1f : 0.5f, hide ? 0.5f : 1f)
        );

        return animSet;
    }
}
