package com.meet.now.apptsystem;

import android.graphics.PointF;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class AddressToGeocode {

    private static final String YOUR_CLIENT_ID = "L5FRpfj4Qo90C0P53Sgo";
    private static final String YOUR_CLIENT_SECRET = "sVbMyitQbO";

    public static PointF getGeocode(String address) {

        PointF point = new PointF();

        try {
            String addr = URLEncoder.encode(address, "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/map/geocode?query=" + addr; //json
            //String apiURL = "https://openapi.naver.com/v1/map/geocode.xml?query=" + addr; // xml
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

            Float coordX;
            Float coordY;
            JSONObject jsonObject = new JSONObject(response.toString());

            JSONArray jsonArray = new JSONObject(jsonObject.get("result").toString()).getJSONArray("items");

            JSONObject object = new JSONObject(jsonArray.getJSONObject(0).get("point").toString());
            coordX = Float.parseFloat(object.getString("x"));
            coordY = Float.parseFloat(object.getString("y"));

            point.set(coordX, coordY);

            return point;

        } catch (Exception e) {
            point.set(200f, 200f);  // 잘못된 주소 예외처리
            return point;

        }
    }

}
