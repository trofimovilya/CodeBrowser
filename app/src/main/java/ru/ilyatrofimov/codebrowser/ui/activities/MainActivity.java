package ru.ilyatrofimov.codebrowser.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionManager;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import ru.ilyatrofimov.codebrowser.R;
import ru.ilyatrofimov.codebrowser.network.DownloaderTaskFragment;
import ru.ilyatrofimov.codebrowser.network.NetResponse;
import ru.ilyatrofimov.codebrowser.network.RawHtmlDownloader;
import ru.ilyatrofimov.codebrowser.ui.views.CodeSwipeRefreshLayout;
import ru.ilyatrofimov.codebrowser.ui.views.RefreshStopButton;
import ru.ilyatrofimov.codebrowser.ui.views.UrlEditText;
import ru.ilyatrofimov.codebrowser.utils.SyntaxHighlighter;

/**
 * @author Ilya Trofimov
 */
public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener
        , CodeSwipeRefreshLayout.CanChildScrollUpCallback
        , DownloaderTaskFragment.DownloaderListener {

    private DownloaderTaskFragment mDownloaderTaskFragment;

    private CardView mUrlContainer;
    private UrlEditText mUrlEditText;
    private RefreshStopButton mRefreshStopButton;
    private ProgressBar mProgressBar;
    private LinearLayout mToolbarContentContainer;
    private ImageButton mClearImgButton;
    private CodeSwipeRefreshLayout mSwipeRefreshLayout;
    private View mDimLayer;
    private WebView mWebView;
    private LinearLayout mContentPlaceholder;
    private TextView mPlaceholderText;
    private ImageView mPlaceholderImageView;

    private String mUrlToLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        initFragmentTask();
        initViews();
        initWebView();

        if (savedInstanceState != null && mWebView != null) {
            mWebView.restoreState(savedInstanceState);
        }
    }

    private void initFragmentTask() {
        FragmentManager fm = getSupportFragmentManager();
        mDownloaderTaskFragment
                = (DownloaderTaskFragment) fm.findFragmentByTag(DownloaderTaskFragment.TAG);

        if (mDownloaderTaskFragment == null) {
            mDownloaderTaskFragment = new DownloaderTaskFragment();
            fm.beginTransaction().add(mDownloaderTaskFragment, DownloaderTaskFragment.TAG).commit();
        }
    }

    private void initViews() {
        mUrlContainer = (CardView) findViewById(R.id.card_url_container);
        mUrlEditText = (UrlEditText) findViewById(R.id.edit_url);
        mRefreshStopButton = (RefreshStopButton) findViewById(R.id.btn_refresh_stop);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mToolbarContentContainer = (LinearLayout) findViewById(R.id.toolbar_content_container);
        mClearImgButton = (ImageButton) findViewById(R.id.img_btn_clear);
        mDimLayer = findViewById(R.id.dim_layer);
        mContentPlaceholder = (LinearLayout) findViewById(R.id.content_placeholder);
        mPlaceholderText = (TextView) findViewById(R.id.text_placeholder);
        mPlaceholderImageView = (ImageView) findViewById(R.id.img_placeholder);
        mSwipeRefreshLayout = (CodeSwipeRefreshLayout) findViewById(R.id.swipe_refresh_container);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setCanChildScrollUpCallback(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        mUrlEditText.setOnGoPressedListener(new UrlEditText.OnGoPressedListener() {
            @Override
            public void onGoPressed(UrlEditText urlEditText) {
                mUrlToLoad = urlEditText.getText().toString();
                startCodeLoading();
            }
        });

        mUrlEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                onUrlFocusChanged(hasFocus);
            }
        });

        mRefreshStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefreshStopButtonClicked();
            }
        });

        mClearImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUrlEditText.setText("");
            }
        });
    }

    private void initWebView() {
        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.setBackgroundColor(ContextCompat.getColor(this, R.color.background));

        WebSettings webViewSettings = mWebView.getSettings();
        webViewSettings.setJavaScriptEnabled(true);
        webViewSettings.setSupportZoom(true);
        webViewSettings.setBuiltInZoomControls(true);
        webViewSettings.setDisplayZoomControls(false);
        webViewSettings.setUseWideViewPort(true);
        webViewSettings.setLoadWithOverviewMode(true);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                onLoadingStopped();
            }
        });
    }

    private void onUrlFocusChanged(final boolean hasFocus) {
        // Start disappearing animation of URL EditText
        Animator hideAnim = ObjectAnimator.ofFloat(mUrlEditText, "alpha", 1f, 0f);
        hideAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // On disappearing animation ends expanding or collapsing animation will be started
                expandCollapseUrlContainer(hasFocus);
            }
        });
        int hideDuration = getResources().getInteger(R.integer.url_hide_anim_duration);
        hideAnim.setDuration(hideDuration).start();
    }

    private void expandCollapseUrlContainer(boolean hasFocus) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Animate view changes
            TransitionManager.beginDelayedTransition(mToolbarContentContainer);
        }

        int containerMargin;
        int containerWidth;
        float containerWeight;

        if (hasFocus) { // Expanding
            containerMargin = 0;
            containerWidth = LinearLayout.LayoutParams.MATCH_PARENT;
            containerWeight = 0f;
            mClearImgButton.setVisibility(View.VISIBLE);
            ObjectAnimator.ofFloat(mDimLayer, "alpha", 0f, 1f).start();
        } else { // Collapsing
            containerMargin = getResources().getDimensionPixelSize(R.dimen.spacing_small);
            containerWidth = 0;
            containerWeight = 1f;
            mClearImgButton.setVisibility(View.GONE);
            ObjectAnimator.ofFloat(mDimLayer, "alpha", 1f, 0f).start();
            mUrlEditText.clearFocusAndHideKeyboard();
        }

        // Apply new params to the URL container
        LinearLayout.LayoutParams params
                = (LinearLayout.LayoutParams) mUrlContainer.getLayoutParams();
        params.width = containerWidth;
        params.weight = containerWeight;
        params.leftMargin = containerMargin;
        params.topMargin = containerMargin;
        params.bottomMargin = containerMargin;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            params.setMarginStart(containerMargin);
        }
        mUrlContainer.setLayoutParams(params);

        int showDuration = getResources().getInteger(R.integer.url_show_anim_duration);
        ObjectAnimator.ofFloat(mUrlEditText, "alpha", 0f, 1f).setDuration(showDuration).start();
        mDimLayer.setClickable(hasFocus); // Dim layer should be clickable when it's visible
    }

    /**
     * Start loading and make loading indicators visible
     */
    private void startCodeLoading() {
        if (mUrlEditText == null || mUrlToLoad.trim().isEmpty()) {
            return;
        }

        mDownloaderTaskFragment.start(mUrlToLoad);

        mUrlEditText.clearFocusAndHideKeyboard();
        mRefreshStopButton.setState(RefreshStopButton.STOP_STATE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCodeLoaded(NetResponse response) {
        if (response.getResponseCode() != NetResponse.ERROR_CODE) { // Code successfully loaded
            String highlightedCode = SyntaxHighlighter.highlight(response.getHtml());
            // Send highlighted code to render in WebView
            mWebView.loadDataWithBaseURL(SyntaxHighlighter.PRETTIFY_DIR, highlightedCode
                    , SyntaxHighlighter.MIME_TYPE, RawHtmlDownloader.ENCODING, "");
            mContentPlaceholder.setVisibility(View.GONE);
        } else { // There was an error
            //  Display error page
            mPlaceholderImageView.setImageDrawable(ContextCompat
                    .getDrawable(this, R.drawable.error));
            mPlaceholderText.setText(R.string.placeholder_error);
            mContentPlaceholder.setVisibility(View.VISIBLE);
            onLoadingStopped();
        }
    }

    /**
     * Stops loading indicators
     * <p/>
     * Will be called when loading process interrupts or there is an error or
     * page is successfully rendered
     */
    @Override
    public void onLoadingStopped() {
        mRefreshStopButton.setState(RefreshStopButton.REFRESH_STATE);
        mProgressBar.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void onRefreshStopButtonClicked() {
        if (mDownloaderTaskFragment.isRunning()) {
            mDownloaderTaskFragment.stop();
            onLoadingStopped();
        } else {
            startCodeLoading();
        }
    }

    @Override
    public void onRefresh() {
        if (mUrlToLoad == null || mUrlToLoad.trim().isEmpty()) {
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }

        startCodeLoading();
    }

    @Override
    public boolean canSwipeRefreshChildScrollUp() {
        return (mWebView.getScrollY() > 0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mWebView.saveState(outState);
        super.onSaveInstanceState(outState);
    }
}
