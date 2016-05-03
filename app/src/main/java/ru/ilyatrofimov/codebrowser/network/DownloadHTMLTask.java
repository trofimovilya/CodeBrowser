package ru.ilyatrofimov.codebrowser.network;

import android.os.AsyncTask;
import org.greenrobot.eventbus.EventBus;
import ru.ilyatrofimov.codebrowser.network.events.HtmlRetrievedEvent;
import ru.ilyatrofimov.codebrowser.network.events.ProgressChangedEvent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLOutput;

public class DownloadHTMLTask extends AsyncTask<URL, Integer, String> {
    @Override
    protected String doInBackground(URL... urls) {
        try {
            return downloadUrl(urls[0]);
        } catch (IOException e) {
            return "Unable to retrieve web page. URL may be invalid.";
        }
    }

    @Override
    protected void onPostExecute(String htmlCode) {
        EventBus.getDefault().post(new HtmlRetrievedEvent(htmlCode));
    }

    private String downloadUrl(URL url) throws IOException {
        InputStream is = null;

        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            int size = conn.getContentLength();
            is = conn.getInputStream();
            // Convert the InputStream into a string
            String contentAsString = readIt(is);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    private String readIt(InputStream stream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;


        while ((length = stream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }

        return result.toString("UTF-8");
    }
}