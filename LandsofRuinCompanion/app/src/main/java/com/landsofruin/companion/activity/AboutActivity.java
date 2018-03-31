package com.landsofruin.companion.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;


public class AboutActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView aboutView = new WebView(this);
        aboutView.loadUrl("file:///android_asset/attribution.html");

        setContentView(aboutView);
    }


}
