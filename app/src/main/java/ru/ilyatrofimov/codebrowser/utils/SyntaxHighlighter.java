package ru.ilyatrofimov.codebrowser.utils;

import android.text.TextUtils;

/**
 * Util class to apply JS and CSS in order to highlight code
 *
 * @author Ilya Trofimov
 */
public final class SyntaxHighlighter {
    public static final String PRETTIFY_DIR = "file:///android_asset/";
    public static final String MIME_TYPE = "text/html";

    /**
     * Generates HTML page with highlighted code
     *
     * @param code code in any language
     * @return html page with highlighted code
     */
    public static String highlight(String code) {
        // Create new html
        String highlightedCode = "<html><head><title>Code Browser</title>";

        // Add css
        highlightedCode += "<link href='" + PRETTIFY_DIR + "prettify.css' rel='stylesheet' type='text/css'/>";
        // Add js
        highlightedCode += "<script src='" + PRETTIFY_DIR + "prettify.js' type='text/javascript'></script>";
        // Run js
        highlightedCode += "</head><body onload='prettyPrint()'><code class='prettyprint'>";
        // For raw code inserted between <code> </code> in order to highlight it
        highlightedCode += TextUtils.htmlEncode(code).replace("\n", "<br>");

        highlightedCode += "</code></html> ";

        return highlightedCode;
    }

    private SyntaxHighlighter() {
    }
}
