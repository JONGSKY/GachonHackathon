package com.meet.now.apptsystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class appt_detail_date_activity extends AppCompatActivity{

    private static final String TAG_JSON = "response";
    private static final String TAG_Date = "Date";
    private JSONArray jsonArray_intent;
    private JSONArray TodayApptArray = new JSONArray();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appt_detail_date);

        Intent intent = getIntent();
        TextView textView = findViewById(R.id.Date);
        ListView appt_detail_listView = findViewById(R.id.appt_detail_listview);

        String Array = intent.getStringExtra("Appt_Info");
        String date = intent.getStringExtra("Date");

        String[] Date_Split = date.split("-");

        textView.setText(Date_Split[1]+"월"+Date_Split[2]+"일");

        try {
            JSONObject jsonObject = new JSONObject(Array);
            jsonArray_intent = jsonObject.getJSONArray(TAG_JSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        for(int j=0; j < jsonArray_intent.length(); j++){
            try {
                JSONObject item = jsonArray_intent.getJSONObject(j);
                if(date.equals(item.getString(TAG_Date))){
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
                Intent mapIntent = new Intent(getApplicationContext(), ApptCenterplaceActivity.class);
                try {
                    JSONObject selectedAppt = TodayApptArray.getJSONObject(i);
                    mapIntent.putExtra("apptNo", selectedAppt.getString("ApptNo"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(mapIntent);
            }
        });
    }

}
