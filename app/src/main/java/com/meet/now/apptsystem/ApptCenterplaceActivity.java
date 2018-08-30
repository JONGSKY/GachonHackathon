package com.meet.now.apptsystem;


import android.content.Intent;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapView;

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

import static com.meet.now.apptsystem.MainActivity.userID;
import static com.meet.now.apptsystem.MainActivity.userAddress;

public class ApptCenterplaceActivity extends NMapActivity {

    private ViewGroup mapLayout;
    private NMapView mMapView;
    static private List<MapApptfriend> mapApptfriendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appt_centerplace);

        mapApptfriendList = new ArrayList<MapApptfriend>();

        Intent intent = getIntent();
        String apptNo = intent.getStringExtra("apptNo");

        new LoadFriendaddress().execute(apptNo);

        init();

    }

    private void init(){

        mapLayout = findViewById(R.id.map_view);

        mMapView = new NMapView(this);
        mMapView.setClientId(getResources().getString(R.string.n_key)); // 클라이언트 아이디 값 설정
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.setScalingFactor(1.7f);
        mMapView.requestFocus();

        mapLayout.addView(mMapView);
    }


    static class LoadFriendaddress extends AsyncTask<String, Void, Void> {
        String target;

        @Override
        protected void onPreExecute() {
            target = "http://brad903.cafe24.com/LoadFriendaddress.php";
        }

        @Override
        protected Void doInBackground(String... Apptinfo) {
            try{
                URL url = new URL(target);
                Map<String,Object> params = new LinkedHashMap<>();

                params.put("userID", userID);
                params.put("apptNo", Apptinfo[0]);

                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String,Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postDataBytes);

                Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                StringBuilder sb = new StringBuilder();
                for (int c; (c = in.read()) >= 0;)
                    sb.append((char)c);
                String response = sb.toString().trim();

                in.close();
                conn.disconnect();

                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("response");

                int count = 0;
                String friendID, friendNickname, userNickname, friendAddress;
                while(count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);
                    friendID = object.getString("friendID");
                    friendNickname = object.getString("friendNickname");
                    userNickname = object.getString("userNickname");
                    friendAddress = object.getString("userAddress");

                    MapApptfriend mapApptfriend = new MapApptfriend(friendID, friendNickname, userNickname, friendAddress);
                    mapApptfriendList.add(mapApptfriend);
                    count++;

                    PointF point = AddressToGeocode.getGeocode(mapApptfriend.getFriendAddress());
                    Log.w(String.valueOf(point.x), String.valueOf(point.y));
                }

                // 해당 로그인 유저 userID, userAddress 리스트에 삽입
                MapApptfriend mapApptfriend = new MapApptfriend(userID, null, null, userAddress);
                mapApptfriendList.add(mapApptfriend);

            }catch(Exception e){
                e.printStackTrace();
            }
            return null;

        }

    }
}

