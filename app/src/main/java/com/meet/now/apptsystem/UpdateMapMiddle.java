package com.meet.now.apptsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/*
centerPlace의 추천지역 반경을 변경한다.
500, 750, 1000, 1250m
 */
public class UpdateMapMiddle extends AppCompatActivity implements View.OnClickListener {
    public static final String INTENT_RESULT = "result";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.dialog_map_middle_point);

        Button button1 = findViewById(R.id.btn_middle1);
        Button button2 = findViewById(R.id.btn_middle2);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        int id = v.getId();
        Log.e("id", id+"");
        switch (id) {
            case R.id.btn_middle1:
                intent.putExtra(INTENT_RESULT,"1");
                break;
            case R.id.btn_middle2:
                intent.putExtra(INTENT_RESULT,"2");
                break;
            default:
                break;
        }
        setResult(RESULT_OK, intent);
        finish();

    }
}
