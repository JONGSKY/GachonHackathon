package com.meet.now.apptsystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class DaumWebViewActivity extends AppCompatActivity {
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daum_web_view);

        // WebView 초기화
        init_webView();

        // 핸들러를 통한 JavaScript 이벤트 반응
        handler = new Handler();
    }


    @SuppressLint("SetJavaScriptEnabled")
    public void init_webView() {
        // WebView 설정
        WebView daum_webView = findViewById(R.id.daum_webview);

        // JavaScript 허용
        daum_webView.getSettings().setJavaScriptEnabled(true);

        // JavaScript의 window.open 허용
        daum_webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
        daum_webView.addJavascriptInterface(new AndroidBridge(), "GachonHackathon");

        // web client 를 chrome 으로 설정
        daum_webView.setWebChromeClient(new WebChromeClient());

        // webview url load. php 파일 주소
        daum_webView.loadUrl("http://brad903.cafe24.com/Daum_address.php");
    }


    private class AndroidBridge {

        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String result = String.format("%s %s", arg1, arg2);
                    Intent addressIntent = new Intent();
                    addressIntent.putExtra("userAddress", result);
                    setResult(1, addressIntent);
                    finish();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent addressIntent = new Intent();
        setResult(0, addressIntent);
        finish();
    }
}
