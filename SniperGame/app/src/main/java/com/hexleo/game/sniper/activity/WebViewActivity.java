package com.hexleo.game.sniper.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.hexleo.game.sniper.R;

/**
 * Created by hexleo on 2017/9/3.
 */

public class WebViewActivity extends Activity {

    private View close;
    private WebView webView;
    private ProgressBar progressBar;

    public static void start(Context context) {
        context.startActivity(new Intent(context, WebViewActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        close = findViewById(R.id.close);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBar.setProgress(0);
        webView = (WebView) findViewById(R.id.web_view);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        webView.loadUrl("https://hexleo.github.io/sniper_game/info.html");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
            }
        });
    }
}
