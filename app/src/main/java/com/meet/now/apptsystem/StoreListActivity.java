package com.meet.now.apptsystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.meet.now.apptsystem.GetRestInfo;

@SuppressLint("Registered")
public class StoreListActivity extends AppCompatActivity {
    ListView listView;
    private String gu;
    private String dong;
    private String title;
    GetRestInfo getRestInfo = new GetRestInfo();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);

        listView = findViewById(R.id.store_listview);
        Intent intent = getIntent();

        gu = intent.getStringExtra("gu");
        dong = intent.getStringExtra("dong");
        title = intent.getStringExtra("title");
        TextView textView = findViewById(R.id.hot_place_name);
        textView.setText(title);

        getRestInfo.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, gu, dong, title);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.w("Test", String.valueOf(getRestInfo.jsonArray));

                StoreListAdapter storeListAdapter = new StoreListAdapter(StoreListActivity.this, getRestInfo.jsonArray, R.layout.store_item);
                listView.setAdapter(storeListAdapter);
            }
        }, 10000);


    }
}
