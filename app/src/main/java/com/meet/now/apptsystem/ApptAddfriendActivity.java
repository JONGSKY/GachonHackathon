package com.meet.now.apptsystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

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

import static android.view.View.GONE;

public class ApptAddfriendActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    static private FriendListAdapter adapter;
    static private List<Friend> friendList;
    static private List<Friend> saveList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);

        ImageButton addfriendButton = findViewById(R.id.addfriendlistButton);
        addfriendButton.setVisibility(GONE);

        ListView friendListView = findViewById(R.id.friendListView);
        friendList = new ArrayList<>();
        saveList = new ArrayList<>();
        adapter = new FriendListAdapter(getApplicationContext(), friendList);  // 해당 리스트의 글들이 매칭
        friendListView.setAdapter(adapter);  // 뷰에 해당 어뎁터가 매칭

        new ApptAddfriendActivity.BackgroundTask().execute();

        EditText search = findViewById(R.id.searchFriend);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                new ApptAddfriendActivity().searchFriend(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent apptFriendIntent = new Intent();
                String nickname;
                if (friendList.get(position).getFriendNickname().equals("null")) {

                    nickname = friendList.get(position).getUserNickname();
                } else {
                    nickname = friendList.get(position).getFriendNickname();
                }
                apptFriendIntent.putExtra("nickname", nickname);
                apptFriendIntent.putExtra("friendID", friendList.get(position).getUserID());
                apptFriendIntent.putExtra("userPhoto", friendList.get(position).getUserPhoto());
                setResult(1, apptFriendIntent);
                finish();

            }
        });

    }

    public void searchFriend(String search) {
        friendList.clear();
        for (int i = 0; i < saveList.size(); i++) {
            if (saveList.get(i).getUserNickname().contains(search) || saveList.get(i).getFriendNickname().contains(search)) {
                friendList.add(saveList.get(i));
            }
        }
        adapter.notifyDataSetChanged();
    }


    static class BackgroundTask extends AsyncTask<Void, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            target = "http://brad903.cafe24.com/FriendList.php";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(target);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("userID", MyApplication.userID);

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
                return response;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {   // 결과 처리부분
            try {
                friendList.clear();
                saveList.clear();

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");

                int count = 0;
                String userID, userPhoto, friendNickname, userNickname, userStatusmsg, userAddress;
                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);
                    userID = object.getString("friendID");
                    userPhoto = object.getString("userPhoto");
                    friendNickname = object.getString("friendNickname");
                    userNickname = object.getString("userNickname");
                    userStatusmsg = object.getString("userStatusmsg");
                    userAddress = object.getString("userAddress");
                    Friend friend = new Friend(userID, userPhoto, friendNickname, userNickname, userStatusmsg, userAddress);
                    friendList.add(friend);
                    saveList.add(friend);
                    count++;
                }

                adapter.notifyDataSetChanged();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
