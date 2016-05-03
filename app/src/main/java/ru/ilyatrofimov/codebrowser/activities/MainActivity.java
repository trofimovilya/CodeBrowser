package ru.ilyatrofimov.codebrowser.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import ru.ilyatrofimov.codebrowser.R;
import ru.ilyatrofimov.codebrowser.network.DownloadHTMLTask;
import ru.ilyatrofimov.codebrowser.network.events.HtmlRetrievedEvent;
import ru.ilyatrofimov.codebrowser.views.RefreshStopButton;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    DownloadHTMLTask downloadHTMLTask;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.card_url_container) CardView mUrlContainer;
    @BindView(R.id.edit_url) EditText mUrlEditText;
    @BindView(R.id.btn_refresh_stop) RefreshStopButton mRefreshStopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        mUrlEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    try {
                        downloadHTMLTask = new DownloadHTMLTask();
                        downloadHTMLTask.execute(new URL(mUrlEditText.getText().toString()));
                        mRefreshStopButton.setState(RefreshStopButton.STOP_STATE);
                        mProgressBar.setVisibility(View.VISIBLE);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });
    }


    @OnClick(R.id.btn_refresh_stop)
    public void refreshStopClicked() {
    }

    @Subscribe
    public void onHtmlRetrieved(HtmlRetrievedEvent event) {
        mRefreshStopButton.setState(RefreshStopButton.REFRESH_STATE);
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
}
