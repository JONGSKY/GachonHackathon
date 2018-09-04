package com.meet.now.apptsystem;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Async_GetPlaceInfo extends AsyncTask<String, Void, Void>{

    private ArrayList<String> PlaceInfo;
    static public JSONArray jsonArray;

    @Override
    protected Void doInBackground(String... urlInfo) {

        String url = urlInfo[0];
        jsonArray = GetRestInfo.jsonArray;

        try {
            PlaceInfo = new ArrayList<>();
            Document document1 = Jsoup.connect("https://www.mangoplate.com" + url).get();

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

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
