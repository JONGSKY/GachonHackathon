package com.meet.now.apptsystem;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class GeocodeToAddress extends AsyncTask<String, Void, HashMap<String, String>> {

    AsyncListener listener;
    private static final String YOUR_CLIENT_ID = "L5FRpfj4Qo90C0P53Sgo";
    private static final String YOUR_CLIENT_SECRET = "sVbMyitQbO";

    GeocodeToAddress(){}
    GeocodeToAddress(AsyncListener listener){
        this.listener = listener;
    }


    @Override
    protected void onPostExecute(HashMap<String, String> hashMap) {
        super.onPostExecute(hashMap);
        if(this.listener != null){
            listener.taskComplete(hashMap);
        }
    }

    @Override
    protected HashMap<String, String> doInBackground(String... strings) {
        HashMap<String, String> addressData = new HashMap<>();
        String geocode = strings[0];


        try {
            String geo = URLEncoder.encode(geocode, "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/map/reversegeocode?query=" + geo; // jsons
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", YOUR_CLIENT_ID);
            con.setRequestProperty("X-Naver-Client-Secret", YOUR_CLIENT_SECRET);
            int responseCode = con.getResponseCode();  // 멈추는 시점
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            JSONObject jsonObject = new JSONObject(response.toString());

            JSONArray jsonArray = new JSONObject(jsonObject.get("result").toString()).getJSONArray("items");

            JSONObject object = new JSONObject(jsonArray.getJSONObject(0).get("addrdetail").toString());

            String address = jsonArray.getJSONObject(0).getString("address");
            String gu = object.getString("sigugun");
            String dong = object.getString("dongmyun");

            addressData.put("address", address);
            addressData.put("gu", gu);
            addressData.put("dong", dong);

            Log.e("address", address);
            return addressData;

        } catch (Exception e) {
            e.printStackTrace();
            return null;  // 잘못된 좌표로 오류가 발생할 경우
        }
    }
}
