package com.example.comg3.toomuchstuff;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by comg3 on 18/05/2018.
 */

public class webView extends AppCompatActivity {


    // create webview
    private WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);

        //find the web view by id
        webview = (WebView) findViewById(R.id.webView);
        //create new web view client
        webview.setWebViewClient(new WebViewClient());

        //load the url of the web view
        webview.loadUrl("http://comg3.sci-project.lboro.ac.uk/appDev/userGuide.html");
        //enable javascript
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    public void onBackPressed(){
        if(webview.canGoBack()){
            webview.goBack();
        } else {
            super.onBackPressed();
        }
    }


}
