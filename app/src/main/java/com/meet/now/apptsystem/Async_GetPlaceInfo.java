package com.meet.now.apptsystem;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Async_GetPlaceInfo extends AsyncTask<String, Void, Void>{

    public AsyncStorelist delegate = null;

    public Async_GetPlaceInfo(AsyncStorelist delegate){
        this.delegate = delegate;
    }

    private ArrayList<String> PlaceInfo;
    static public JSONArray jsonArray;

    @Override
    protected Void doInBackground(String... urlInfo) {

        String url = urlInfo[0];
        jsonArray = StoreListActivity.jsonArray;

        try {
            PlaceInfo = new ArrayList<>();
            Document document1 = Jsoup.connect("https://www.mangoplate.com" + url).get();

            Elements title = document1.select("h1.restaurant_name");
            Elements pic = document1.select("img.center-croping");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("title", title.text());

            URL imageUrl = new URL(pic.eq(0).attr("src"));
            HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            jsonObject.put("pic", getStringFromBitmap(myBitmap));

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

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        delegate.LoadStorefinish();
    }

    public static String getStringFromBitmap(Bitmap bitmapPicture) {
        /*
         * This functions converts Bitmap picture to a string which can be
         * JSONified.
         * */
        final int COMPRESSION_QUALITY = 10;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

}
