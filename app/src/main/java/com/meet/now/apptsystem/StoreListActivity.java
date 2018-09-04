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
    GetRestInfo getRestInfo = new GetRestInfo();
    ArrayList<String> PlaceInfo;
    private JSONArray jsonArray = new JSONArray();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);
        listView = findViewById(R.id.store_listview);
        Intent intent = getIntent();

        String gu = intent.getStringExtra("gu");
        String dong = intent.getStringExtra("dong");
        String title = intent.getStringExtra("title");
        TextView textView = findViewById(R.id.hot_place_name);
        textView.setText(title);

        getRestInfo.execute(gu, dong, title);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                GetStoreList getStoreList = new GetStoreList();
                getStoreList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0, 1);
                getStoreList = new GetStoreList();
                getStoreList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 1, 2);
                getStoreList = new GetStoreList();
                getStoreList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 2, 3);
                getStoreList = new GetStoreList();
                getStoreList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 3, 4);
                getStoreList = new GetStoreList();
                getStoreList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 4, 5);
                getStoreList = new GetStoreList();
                getStoreList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 5, 6);
                getStoreList = new GetStoreList();
                getStoreList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 6, 7);
                getStoreList = new GetStoreList();
                getStoreList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 7, 8);
                getStoreList = new GetStoreList();
                getStoreList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 8, 9);
                getStoreList = new GetStoreList();
                getStoreList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 9, 10);

            }
        }, 2000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.w("Test", String.valueOf(jsonArray));
                StoreListAdapter storeListAdapter = new StoreListAdapter(StoreListActivity.this, jsonArray, R.layout.store_item);
                listView.setAdapter(storeListAdapter);
            }
        }, 6000);

    }

    public class GetStoreList extends AsyncTask<Integer, Void, Void>{
        @Override
        protected Void doInBackground(Integer... integers) {
            try {
                for (int i = integers[0]; i < integers[1]; i++) {
                    PlaceInfo = new ArrayList<>();
                    Document document1 = Jsoup.connect("https://www.mangoplate.com" + GetRestInfo.RestaurantURL.get(i)).get();

                    Elements title = document1.select("h1.restaurant_name");
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("title", title.text());

                    Elements titleDetail = document1.select("section.restaurant-detail td");
                    for (int j = 0; j < 6; j++) {
                        PlaceInfo.add(titleDetail.eq(j).text().trim());
                    }

                    jsonObject.put("addr", PlaceInfo.get(0));
                    jsonObject.put("PhoneNumber", PlaceInfo.get(1));
                    jsonObject.put("FoodType", PlaceInfo.get(2));
                    jsonObject.put("Price", PlaceInfo.get(3));
                    jsonObject.put("Parking", PlaceInfo.get(4));
                    jsonObject.put("Time", PlaceInfo.get(5));

                    jsonArray.put(jsonObject);
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
