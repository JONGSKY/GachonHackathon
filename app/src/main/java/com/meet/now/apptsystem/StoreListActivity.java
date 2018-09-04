package com.meet.now.apptsystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.meet.now.apptsystem.GetRestInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

@SuppressLint("Registered")
public class StoreListActivity extends AppCompatActivity {
    ListView listView;

    StoreListAdapter storeListAdapter;
    String title;
    JSONArray jsonArray;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);
        listView = findViewById(R.id.store_listview);
        Intent intent = getIntent();

        // 지금 사용안하고 있음
        String gu = intent.getStringExtra("gu");
        String dong = intent.getStringExtra("dong");

        title = intent.getStringExtra("title");
        TextView textView = findViewById(R.id.hot_place_name);
        textView.setText(title);

        GetRestInfo getRestInfo = new GetRestInfo();
        getRestInfo.execute(title);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                jsonArray = Async_GetPlaceInfo.jsonArray;
                storeListAdapter = new StoreListAdapter(StoreListActivity.this, jsonArray, R.layout.store_item);
                listView.setAdapter(storeListAdapter);
            }
        }, 2500);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent storeInfo = new Intent(StoreListActivity.this, StoreDetail.class);
                storeInfo.putExtra("placeName", title);
                storeInfo.putExtra("storeInfo", storeListAdapter.getItem(position).toString());
                startActivity(storeInfo);
            }
        });

    }

}
