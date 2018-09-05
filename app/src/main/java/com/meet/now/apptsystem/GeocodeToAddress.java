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

    public AddressAsyncResponse delegate = null;

    public GeocodeToAddress(AddressAsyncResponse delegate){
        this.delegate = delegate;
    }

    static final String YOUR_CLIENT_ID = "L5FRpfj4Qo90C0P53Sgo";
    static final String YOUR_CLIENT_SECRET = "sVbMyitQbO";
    HashMap<String, String> addressData;

        @Override
    protected HashMap<String, String> doInBackground(String... geocodeInfo) {

        String geocode = geocodeInfo[0];
        String clientId = YOUR_CLIENT_ID;//애플리케이션 클라이언트 아이디값";
        String clientSecret = YOUR_CLIENT_SECRET;//애플리케이션 클라이언트 시크릿값";
        addressData = new HashMap<String, String>();

        try {
            String geo = URLEncoder.encode(geocode, "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/map/reversegeocode?query=" + geo; // jsons
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            int responseCode = con.getResponseCode();  // 멈추는 시점
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            JSONObject jsonObject = new JSONObject(response.toString());

            JSONArray jsonArray = new JSONObject(jsonObject.get("result").toString()).getJSONArray("items");

            JSONObject object = new JSONObject(jsonArray.getJSONObject(0).get("addrdetail").toString());

            String address = jsonArray.getJSONObject(0).getString("address").toString();
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
    @Override
    protected void onPostExecute(HashMap<String, String> stringStringHashMap) {
        super.onPostExecute(stringStringHashMap);

        delegate.processFinish(addressData);
    }
}
