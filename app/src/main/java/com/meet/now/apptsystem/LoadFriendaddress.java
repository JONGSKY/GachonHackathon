package com.meet.now.apptsystem;

import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.Log;

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

class LoadFriendaddress extends AsyncTask<String, Void, Void> {

    private AsyncNullListener asyncNullListener;
    public static List<MapApptfriend> mapApptfriendList;
    private String target;

    LoadFriendaddress(AsyncNullListener asyncNullListener){
        this.asyncNullListener = asyncNullListener;
    }
    @Override
    protected void onPreExecute() {
        target = "http://brad903.cafe24.com/LoadFriendaddress.php";
        mapApptfriendList = new ArrayList<>();
        if(asyncNullListener != null) asyncNullListener.taskComplete();
    }

    @Override
    protected Void doInBackground(String... Apptinfo) {
        try {
            URL url = new URL(target);
            Map<String, Object> params = new LinkedHashMap<>();

            params.put("userID", MyApplication.userID);
            params.put("apptNo", Apptinfo[0]);

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
            String friendID, friendNickname, userNickname, friendAddress;
            while (count < jsonArray.length()) {
                JSONObject object = jsonArray.getJSONObject(count);
                friendID = null;
                friendNickname = null;
                userNickname = object.getString("userNickname");
                friendAddress = object.getString("userAddress");

                PointF point = AddressToGeocode.getGeocode(friendAddress);

                MapApptfriend mapApptfriend = new MapApptfriend(friendID, friendNickname, userNickname, friendAddress, point);
                mapApptfriendList.add(mapApptfriend);
                count++;
            }

            // 해당 로그인 유저 userID, userAddress 리스트에 삽입
//            PointF point = AddressToGeocode.getGeocode(MyApplication.Address);
//            MapApptfriend mapApptfriend = new MapApptfriend(MyApplication.userID, null, "나", MyApplication.Address, point);
//            Log.w("BugTest", String.valueOf(mapApptfriend));
//            mapApptfriendList.add(mapApptfriend);
            Log.w("ApptMemberNumber", String.valueOf(mapApptfriendList));
            Log.w("ApptMember", mapApptfriendList.get(0).friendID +" "+
                    mapApptfriendList.get(0).friendAddress +" "+ mapApptfriendList.get(0).point.x +" "+ mapApptfriendList.get(0).point.y);
            Log.w("ApptMember", mapApptfriendList.get(1).friendID +" "+
                    mapApptfriendList.get(1).friendAddress +" "+ mapApptfriendList.get(1).point.x +" "+ mapApptfriendList.get(1).point.y);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

}