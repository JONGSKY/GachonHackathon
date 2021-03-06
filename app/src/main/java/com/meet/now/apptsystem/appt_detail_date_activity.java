package com.meet.now.apptsystem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

public class appt_detail_date_activity extends AppCompatActivity{
    private ListView appt_detail_listView;

    private static final String TAG_JSON = "response";
    private static final String TAG_Date = "Date";
    private JSONArray jsonArray_intent;
    private JSONArray TodayApptArray = new JSONArray();
    private JSONObject jsonObject;
    private String Date;
    appt_detail_date_adapter adapter;

    public static Context CONTEXT;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appt_detail_date);

        CONTEXT = this;

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
                if(Date.equals(item.getString(TAG_Date))){
                    count++;
                    TodayApptArray.put(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        //어댑터 설정
        adapter = new appt_detail_date_adapter(appt_detail_date_activity.this, R.layout.appt_item, TodayApptArray);
        adapter.notifyDataSetChanged();
        appt_detail_listView.setAdapter(adapter);
        appt_detail_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent mapIntent = new Intent(appt_detail_date_activity.this, ApptCenterplaceActivity.class);
                try {
                    JSONObject selectedAppt = TodayApptArray.getJSONObject(i);
                    mapIntent.putExtra("apptNo", selectedAppt.getString("ApptNo"));
                    mapIntent.putExtra("apptPosition", i);
                    mapIntent.putExtra("apptName", selectedAppt.getString("ApptName"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivityForResult(mapIntent, 1);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 1){
            int position = data.getIntExtra("apptPosition", 0);
            int memberCount = data.getIntExtra("memberCount", 0);
            String finalApptPlace = data.getStringExtra("finalApptPlace");
            try {
                TodayApptArray.getJSONObject(position).put("MemberNo", memberCount);
                if(!finalApptPlace.equals("null")){
                    TodayApptArray.getJSONObject(position).put("ApptPlace", finalApptPlace);
                }
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(this, appt_list_view_activity.class);
        startActivity(backIntent);
        finish();
    }
}
