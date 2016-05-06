package ru.ilyatrofimov.codebrowser.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Util class to fetch page source code
 *
 * @author Ilya Trofimov
 */
public final class RawHtmlDownloader {
    public static final String ENCODING = "UTF-8";

    private static final int READ_TIMEOUT = 5000; // milliseconds
    private static final int CONNECT_TIMEOUT = 8000; // milliseconds
    private static final int READ_BUFFER_SZ = 1024; // bytes
    private static final String REQUEST_METHOD = "GET";

    public static NetResponse downloadHtml(URL url) throws IOException {
        InputStream inputStream = null;

        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setRequestMethod(REQUEST_METHOD);
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (!(responseCode >= 200 && responseCode < 300)) {
                throw new IllegalStateException("Bad HTTP response code");
            }

            inputStream = conn.getInputStream();
            String htmlCode = readSteam(inputStream);

            return new NetResponse(responseCode, htmlCode);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private static String readSteam(InputStream stream) throws IOException {
        ByteArrayOutputStream rawBytes = new ByteArrayOutputStream();
        byte[] buffer = new byte[READ_BUFFER_SZ];
        int length;

        while ((length = stream.read(buffer)) != -1) {
            rawBytes.write(buffer, 0, length);
        }

        return rawBytes.toString(ENCODING);
    }

    private RawHtmlDownloader() {
    }
}
