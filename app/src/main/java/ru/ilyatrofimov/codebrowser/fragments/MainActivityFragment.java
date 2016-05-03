package ru.ilyatrofimov.codebrowser.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import ru.ilyatrofimov.codebrowser.R;
import ru.ilyatrofimov.codebrowser.network.events.HtmlRetrievedEvent;
import ru.ilyatrofimov.codebrowser.views.ChildScrollSwipeRefreshLayout;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener
        , ChildScrollSwipeRefreshLayout.CanChildScrollUpCallback {

    WebView mCode;
    private ChildScrollSwipeRefreshLayout mSwipeRefreshLayout;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mSwipeRefreshLayout = (ChildScrollSwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setCanChildScrollUpCallback(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mCode = (WebView) view.findViewById(R.id.web_view);
        mCode.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background));
        setRetainInstance(true);
        return view;
    }


    @Subscribe
    public void onHtmlRetrieved(HtmlRetrievedEvent event) {
        // Configure the webview
        WebSettings s = mCode.getSettings();
        s.setSupportZoom(true);
        s.setBuiltInZoomControls(true);
        s.setDisplayZoomControls(false);
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);
        s.setJavaScriptEnabled(true);

        String contentString = "";
        contentString += "<html><head><title>" + "KEK" + "</title>";
        contentString += "<link href='file:///android_asset/prettify.css' rel='stylesheet' type='text/css'/> ";
        contentString += "<script src='file:///android_asset/prettify.js' type='text/javascript'></script> ";
        contentString += "</head><body onload='prettyPrint()'><code class='" + "prettyprint" + "'>";
        contentString += TextUtils.htmlEncode(event.getHtmlCode()).replace("\n", "<br>");
        contentString += "</code> </html> ";
        mCode.loadDataWithBaseURL("file:///android_asset/", contentString, "text/html", "UTF-8", "");
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public boolean canSwipeRefreshChildScrollUp() {
        return mCode.getScrollY() > 0;
    }

    @Override
    public void onRefresh() {

    }
}
