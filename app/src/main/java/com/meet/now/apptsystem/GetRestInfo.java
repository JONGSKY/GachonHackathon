package com.meet.now.apptsystem;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

@SuppressLint("StaticFieldLeak")
public class GetRestInfo extends AsyncTask<String, Void, Void> {

    static public ArrayList<String> RestaurantURL;

    public static Void RestaurantURLClear(){
        RestaurantURL = new ArrayList<>();
        return null;
    }

    @Override
    protected Void doInBackground(String... strings) {
        RestaurantURLClear();

        String Gu = strings[0];
        String Dong = strings[1];
        String HotPlace = strings[2];

        try {
            Document document = Jsoup.connect("https://www.mangoplate.com/search/" + Gu + "%20" + Dong + "%20" + HotPlace).get();
            Elements element = document.select(".restaurant-item .only-desktop_not");

            int count = 0;
            for (Element e : element) {
                RestaurantURL.add(e.attr("href"));
                Log.w(String.valueOf(count), RestaurantURL.get(count));
                count++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
