package ru.ilyatrofimov.codebrowser.network;

/**
 * Simple POJO to store network response
 *
 * @author Ilya Trofimov
 */
public class NetResponse {
    public static final int ERROR_CODE = -1;

    private int responseCode; // Header's response code
    private String htmlCode; // HTML Code of web-page

    public NetResponse(int responseCode) {
        this(responseCode, "");
    }

    public NetResponse(int responseCode, String htmlCode) {
        this.responseCode = responseCode;
        this.htmlCode = htmlCode == null ? "" : htmlCode;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getHtml() {
        return htmlCode;
    }
}
