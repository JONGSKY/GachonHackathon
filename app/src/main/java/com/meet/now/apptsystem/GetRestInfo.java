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

public class GetRestInfo {

    private JSONArray JsonArray = new JSONArray();

    public JSONArray RestInfo(String Gu, String Dong){

        GetData getData = new GetData();
        getData.execute(Gu, Dong);
        return JsonArray;
    }

    @SuppressLint("StaticFieldLeak")
    private class GetData extends AsyncTask<String, Void, JSONArray>{
        @Override
        protected JSONArray doInBackground(String... strings) {
            ArrayList<String> RestaurantURL = new ArrayList<>();
            JSONArray jsonArray = new JSONArray();
            ArrayList<String> PlaceInfo = new ArrayList<>();
            String Gu = strings[0];
            String Dong = strings[1];

            try {
                Document document = Jsoup.connect("https://www.mangoplate.com/search/"+Gu+"%20"+Dong).get();
                Elements element = document.select(".restaurant-item .only-desktop_not");

                int count = 0;
                for(Element e : element){
                    RestaurantURL.add(e.attr("href"));
                    Log.w(String.valueOf(count), RestaurantURL.get(count));
                    count++;
                }

                int count1 = 0;
                for(int i = 0; i < RestaurantURL.size(); i++){
                    Document document1 = Jsoup.connect("https://www.mangoplate.com"+RestaurantURL.get(i)).get();

                    Elements title = document1.select("h1.restaurant_name");
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("title", title.text());

                    Elements titleDetail = document1.select("section.restaurant-detail td");
                    for(int j=0; j<6; j++){
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
                Log.w(String.valueOf(count1), String.valueOf(jsonArray));

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return jsonArray;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);

            JsonArray = jsonArray;
        }
    }
}
