package com.meet.now.apptsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.app.Activity;

/*
centerPlace의 추천지역 반경을 변경한다.
500, 750, 1000, 1250m
 */
public class UpdateMapDistance extends AppCompatActivity implements View.OnClickListener {
    public static final String INTENT_RESULT = "result";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.dialog_map_distance);

        Button button1 = findViewById(R.id.btn_dis1);
        Button button2 = findViewById(R.id.btn_dis2);
        Button button3 = findViewById(R.id.btn_dis3);
        Button button4 = findViewById(R.id.btn_dis4);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        int id = v.getId();
        Log.e("id", id+"");
        switch (id) {
            case R.id.btn_dis1:
                intent.putExtra(INTENT_RESULT,"500");
                break;
            case R.id.btn_dis2:
                intent.putExtra(INTENT_RESULT,"750");
                break;
            case R.id.btn_dis3:
                intent.putExtra(INTENT_RESULT,"1000");
                break;
            case R.id.btn_dis4:
                intent.putExtra(INTENT_RESULT,"1250");
                break;
            default:
                break;
        }
        setResult(RESULT_OK, intent);
        finish();

    }
}
