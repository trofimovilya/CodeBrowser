package ru.ilyatrofimov.codebrowser.network;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import java.io.IOException;
import java.net.URL;

/**
 * Since the main requirement was not to use third-party libraries I decided to go with
 * good old HttpUrlConnection with AsyncTask.
 * To connect AsyncTask with UI I've implemented TaskFragment pattern
 * http://www.androiddesignpatterns.com/2013/04/retaining-objects-across-config-changes.html
 *
 * @author Ilya Trofimov
 */
public class DownloaderTaskFragment extends android.support.v4.app.Fragment {
    public static final String TAG = DownloaderTaskFragment.class.getCanonicalName();

    private DownloaderListener mDownloaderListener;
    private DownloaderTask mTask;
    private boolean mRunning;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof DownloaderListener) {
            mDownloaderListener = (DownloaderListener) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop();
    }

    /**
     * Starts loading
     *
     * @param url url to load
     */
    public void start(String url) {
        if (!mRunning) {
            mTask = new DownloaderTask();
            mTask.execute(url.trim());
            mRunning = true;
        }
    }

    /**
     * Stops loading
     */
    public void stop() {
        if (mRunning) {
            mTask.cancel(true);
            mTask = null;
            mRunning = false;
        }
    }

    public boolean isRunning() {
        return mRunning;
    }

    private class DownloaderTask extends AsyncTask<String, Void, NetResponse> {
        @Override
        protected void onPreExecute() {
            mRunning = true;
        }

        @Override
        protected NetResponse doInBackground(String... urls) {
            String url = urls[0];

            try {
                return RawHtmlDownloader.downloadHtml(new URL(url));
            } catch (IOException | IllegalStateException e) {
                return tryWithHttp(url);
            }
        }

        // As some sites require "www", while others don't work at all with "www"
        // So if a site doesn't respond the task will try to load page with different URL-pattern

        private NetResponse tryWithHttp(String url) {
            try {
                return RawHtmlDownloader.downloadHtml(new URL("http://" + url));
            } catch (IOException | IllegalStateException e) {
                return tryWithHttpWWW(url);
            }
        }

        private NetResponse tryWithHttpWWW(String url) {
            try {
                return RawHtmlDownloader.downloadHtml(new URL("http://www." + url));
            } catch (IOException | IllegalStateException e) {
                return tryWithHttps(url);
            }
        }

        private NetResponse tryWithHttps(String url) {
            try {
                return RawHtmlDownloader.downloadHtml(new URL("https://" + url));
            } catch (IOException | IllegalStateException e) {
                return tryWithHttpsWWW(url);
            }
        }

        private NetResponse tryWithHttpsWWW(String url) {
            try {
                return RawHtmlDownloader.downloadHtml(new URL("https://www." + url));
            } catch (IOException | IllegalStateException e) {
                return new NetResponse(NetResponse.ERROR_CODE);
            }
        }

        @Override
        protected void onCancelled() {
            mDownloaderListener.onLoadingStopped();
            mRunning = false;
        }

        @Override
        protected void onPostExecute(NetResponse htmlCode) {
            mDownloaderListener.onCodeLoaded(htmlCode);
            mRunning = false;
        }
    }

    public interface DownloaderListener {
        void onCodeLoaded(NetResponse result);

        void onLoadingStopped();
    }
}
