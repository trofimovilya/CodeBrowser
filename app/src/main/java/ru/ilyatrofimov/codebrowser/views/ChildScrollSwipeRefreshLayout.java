package ru.ilyatrofimov.codebrowser.views;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

/**
 * Created by ILYATTR on 01/05/16.
 */
public class ChildScrollSwipeRefreshLayout extends SwipeRefreshLayout {
    private CanChildScrollUpCallback mCanChildScrollUpCallback;

    public ChildScrollSwipeRefreshLayout(Context context) {
        super(context);
    }

    public ChildScrollSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public interface CanChildScrollUpCallback {
        boolean canSwipeRefreshChildScrollUp();
    }

    public void setCanChildScrollUpCallback(CanChildScrollUpCallback canChildScrollUpCallback) {
        mCanChildScrollUpCallback = canChildScrollUpCallback;
    }

    @Override
    public boolean canChildScrollUp() {
        if (mCanChildScrollUpCallback != null) {
            return mCanChildScrollUpCallback.canSwipeRefreshChildScrollUp();
        }

        return super.canChildScrollUp();
    }
}
