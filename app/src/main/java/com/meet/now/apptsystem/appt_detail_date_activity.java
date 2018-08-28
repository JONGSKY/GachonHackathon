package com.meet.now.apptsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class appt_detail_date_activity extends AppCompatActivity{
    private ListView appt_detail_listView;
    private String mJsonString;

    private static final String TAG_JSON = "response";
    private static final String TAG_Appt_Name = "ApptName";
    private static final String TAG_Date = "Date";
    private static final String TAG_RelationGroup = "RelationGroup";
    private static final String TAG_ApptTime = "ApptTime";
    private static final String TAG_ApptPlace = "ApptPlace";
    private JSONArray jsonArray_intent;
    private JSONArray TodayApptArray = new JSONArray();
    private JSONObject jsonObject;
    private String Date;
    private String Date1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appt_detail_date);

        Intent intent = getIntent();
        TextView textView = findViewById(R.id.Date);
        appt_detail_listView = findViewById(R.id.appt_detail_listview);

        String Array = intent.getStringExtra("Appt_Info");
        Date = intent.getStringExtra("Date");

        String[] Date_Split = Date.split("-");

        textView.setText(Date_Split[1]+"월"+Date_Split[2]+"일");

        try {
            jsonObject = new JSONObject(Array);
            jsonArray_intent = jsonObject.getJSONArray(TAG_JSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        int count = 0;
        for(int j=0; j < jsonArray_intent.length(); j++){
            try {
                JSONObject item = jsonArray_intent.getJSONObject(j);
                Log.w(Date, item.getString(TAG_Date));
                if(Date.equals(item.getString(TAG_Date))){
                    count++;
                    TodayApptArray.put(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //어댑터 설정
        appt_detail_date_adapter adapter = new appt_detail_date_adapter(appt_detail_date_activity.this, R.layout.appt_item, TodayApptArray);
        appt_detail_listView.setAdapter(adapter);
        appt_detail_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent test = new Intent(getApplicationContext(), ApptCenterplaceActivity.class);
                startActivity(test);
            }
        });
    }

    public String method(String str) {
        if (str.length() > 0 && str.charAt(str.length()-1)=='x') {
            str = str.substring(0, str.length()-1);
        }
        return str;
    }
}
