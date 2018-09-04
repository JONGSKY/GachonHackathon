package com.meet.now.apptsystem;

import android.graphics.PointF;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class LoadNonaddress extends AsyncTask<String, Void, Void> {

    public static List<MapApptfriend> mapApptNonList;
    private String target;

    @Override
    protected void onPreExecute() {
        target = "http://brad903.cafe24.com/LoadNonaddress.php";
        mapApptNonList = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(String... Apptinfo) {
        try {
            URL url = new URL(target);
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("ApptNo", Apptinfo[0]);

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);

            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (int c; (c = in.read()) >= 0; )
                sb.append((char) c);
            String response = sb.toString().trim();

            in.close();
            conn.disconnect();

            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("response");
            int count = 0;
            String userNickname, friendAddress;
            while (count < jsonArray.length()) {
                JSONObject object = jsonArray.getJSONObject(count);
                userNickname = object.getString("NonNickName");
                friendAddress = object.getString("NonAddr");
                PointF point = AddressToGeocode.getGeocode(friendAddress);
                MapApptfriend mapApptfriend = new MapApptfriend(null, "비회원", userNickname, friendAddress, point);
                mapApptNonList.add(mapApptfriend);
                count++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

}
