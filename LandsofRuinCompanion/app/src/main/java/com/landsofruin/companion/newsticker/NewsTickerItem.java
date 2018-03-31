package com.landsofruin.companion.newsticker;

/**
 * Created by juhani on 19/12/2016.
 */

public class NewsTickerItem {
    private String date;
    private String url;
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
