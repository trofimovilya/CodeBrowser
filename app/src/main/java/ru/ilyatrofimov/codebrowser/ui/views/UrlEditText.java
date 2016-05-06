package ru.ilyatrofimov.codebrowser.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * URL EditText View
 *
 * @author Ilya Trofimov
 */
public class UrlEditText extends EditText {
    OnGoPressedListener onGoPressedListener;

    public UrlEditText(Context context) {
        super(context);
    }

    public UrlEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UrlEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        // Clear focus on Back button pressed
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            this.clearFocusAndHideKeyboard();
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onEditorAction(int actionCode) {
        // Run callback on IME Go pressed
        if (actionCode == EditorInfo.IME_ACTION_GO && onGoPressedListener != null) {
            onGoPressedListener.onGoPressed(this);
        }

        super.onEditorAction(actionCode);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Run callback on hardware enter pressed
        if (keyCode == KeyEvent.KEYCODE_ENTER && onGoPressedListener != null) {
            onGoPressedListener.onGoPressed(this);
        }

        return super.onKeyDown(keyCode, event);
    }

    public void setOnGoPressedListener(OnGoPressedListener listener) {
        onGoPressedListener = listener;
    }

    public void clearFocusAndHideKeyboard() {
        this.clearFocus();
        InputMethodManager imm = (InputMethodManager) this.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
    }

    public interface OnGoPressedListener {
        void onGoPressed(UrlEditText editText);
    }
}
