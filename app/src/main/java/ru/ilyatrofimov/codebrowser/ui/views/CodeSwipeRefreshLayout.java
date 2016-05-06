package ru.ilyatrofimov.codebrowser.ui.views;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

/**
 * Swipe to refresh layout that allows to store WebView
 *
 * @author Ilya Trofimov
 */
public class CodeSwipeRefreshLayout extends SwipeRefreshLayout {
    private CanChildScrollUpCallback mCanChildScrollUpCallback;

    public CodeSwipeRefreshLayout(Context context) {
        super(context);
    }

    public CodeSwipeRefreshLayout(Context context, AttributeSet attrs) {
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
