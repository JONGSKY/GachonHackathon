package com.meet.now.apptsystem;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

public class DaumWebViewActivity extends AppCompatActivity {
    private WebView daum_webView;
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


    public void init_webView() {
        // WebView 설정
        daum_webView = (WebView) findViewById(R.id.daum_webview);

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
                    Intent addressIntent = new Intent(DaumWebViewActivity.this, RegisterActivity.class);
                    Intent regisintent = getIntent();
                    addressIntent.putExtra("userID", regisintent.getStringExtra("userID"));
                    addressIntent.putExtra("userPassword", regisintent.getStringExtra("userPassword"));
                    addressIntent.putExtra("userNickname", regisintent.getStringExtra("userNickname"));
                    addressIntent.putExtra("userAddress", result);
                    addressIntent.putExtra("userAge", regisintent.getIntExtra("userAge", 0));
                    addressIntent.putExtra("userGender", regisintent.getIntExtra("userGender", 0));
                    addressIntent.putExtra("validate", regisintent.getBooleanExtra("validate", false));
                    startActivity(addressIntent);
                    finish();
                }
            });
        }
    }
}
