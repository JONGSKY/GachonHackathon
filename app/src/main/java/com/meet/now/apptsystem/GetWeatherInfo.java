package com.meet.now.apptsystem;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class GetWeatherInfo extends AsyncTask<String, Void, Void> {

    final public static String appID = "57481aa4bd0135ebcaf1129b81dc7314";
    String dateStr;
    String Icon;
    Bitmap myBitmap;

    public AsyncWeather delegate = null;

    public GetWeatherInfo(AsyncWeather delegate) {
        this.delegate = delegate;
    }

    @Override
    protected Void doInBackground(String... strings) {
        Icon = "";
        try {
            // build a URL
            String s = "http://api.openweathermap.org/data/2.5/forecast?q=Seoul,kr&mode=json&appid=" + appID;
            URL url = new URL(s);

            // read from the URL
            Scanner scan = new Scanner(url.openStream());
            String str = new String();
            while (scan.hasNext())
                str += scan.nextLine();
            scan.close();

            // build a JSON object
            JSONObject obj = new JSONObject(str);
            if (!obj.getString("cod").equals("200"))
                return null;

            if(strings[0].split(" ").length == 1){  // 날짜 데이터만 들어왔을 떄
                dateStr =  strings[0] + " 09:00:00";
            }

            JSONArray array = new JSONArray(obj.getString("list"));
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);

                String dataStr = object.getString("dt_txt").toString();
                if (dateStr.equals(dataStr)) {
                    JSONArray weatherIcon = object.getJSONArray("weather");
                    Icon = weatherIcon.getJSONObject(0).getString("icon");
                }
            }

            if(Icon.equals("")) {
                return null;
            }

            URL imageUrl = new URL("http://openweathermap.org/img/w/" + Icon + ".png");
            HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            myBitmap = BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        delegate.processFinish(myBitmap);
    }

    private String getTime(String time) {
        String[] timeArray = time.split(":");
        int nowHour = Integer.parseInt(timeArray[0]);
        String nearTime = null;
        switch (nowHour / 3) {
            case 0:
                nearTime = "00:00:00";
                break;
            case 1:
                nearTime = "03:00:00";
                break;
            case 2:
                nearTime = "06:00:00";
                break;
            case 3:
                nearTime = "09:00:00";
                break;
            case 4:
                nearTime = "12:00:00";
                break;
            case 5:
                nearTime = "15:00:00";
                break;
            case 6:
                nearTime = "18:00:00";
                break;
            case 7:
                nearTime = "21:00:00";
                break;
            default:
                nearTime = "12:00:00";
        }

        return nearTime;
    }

}
