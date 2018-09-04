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

    static public JSONArray jsonArray;

    public Void dataClear() {
        jsonArray = new JSONArray();
        return null;
    }

    @Override
    protected Void doInBackground(String... strings) {
        dataClear();

        String HotPlace = strings[0];

        try {
            Document document = Jsoup.connect("https://www.mangoplate.com/search/" + HotPlace).get();
            Elements element = document.select(".restaurant-item .only-desktop_not");

            for (Element e : element) {
                Async_GetPlaceInfo getPlaceinfo = new Async_GetPlaceInfo();
                getPlaceinfo.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, e.attr("href"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
