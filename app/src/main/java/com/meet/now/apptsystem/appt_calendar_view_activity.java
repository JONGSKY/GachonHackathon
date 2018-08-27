package com.meet.now.apptsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class appt_calendar_view_activity extends AppCompatActivity{
    public static AppCompatActivity apptCalenderActivity;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appt_calendar_view);
        // 캘린더뷰 객체 생성
        apptCalenderActivity = appt_calendar_view_activity.this;

        // 리스트뷰 종료
        appt_list_view_activity apptListActivity = (appt_list_view_activity)appt_list_view_activity.apptListActivity;
        if(apptListActivity != null) apptListActivity.finish();

        ImageButton backButton = (ImageButton) findViewById(R.id.btn_back_calender);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ImageButton imageButton = findViewById(R.id.list_change_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), appt_list_view_activity.class);
                startActivity(intent);
            }
        });
    }
}