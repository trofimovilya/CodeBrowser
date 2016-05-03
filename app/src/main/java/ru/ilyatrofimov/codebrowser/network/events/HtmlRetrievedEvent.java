package ru.ilyatrofimov.codebrowser.network.events;

/**
 * Created by ILYATTR on 01/05/16.
 */
public class HtmlRetrievedEvent {
    private String htmlCode;

    public HtmlRetrievedEvent(String htmlCode) {
        this.htmlCode = htmlCode;
    }

    public String getHtmlCode() {
        return this.htmlCode;
    }
}