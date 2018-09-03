package com.meet.now.apptsystem;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.meet.now.apptsystem.MainActivity.userID;


public class FriendlistActivity extends AppCompatActivity {

    Dialog addfriendDialog;

    static private ListView friendListView;
    static private FriendListAdapter adapter;
    static private List<Friend> friendList;
    static private List<Friend> saveList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);
        Intent intent = getIntent();

        friendListView = (ListView) findViewById(R.id.friendListView);
        friendList = new ArrayList<Friend>();
        saveList = new ArrayList<Friend>();
        adapter = new FriendListAdapter(getApplicationContext(), friendList);  // 해당 리스트의 글들이 매칭
        friendListView.setAdapter(adapter);  // 뷰에 해당 어뎁터가 매칭

        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent mapAddMember = getIntent();
                if (mapAddMember !=null) {
                    Log.e("mapAddMember", "성공");
                    // 아이템 클릭시 돌아가기
                    String friendID = friendList.get(position).getUserID();
                    String apptNo = mapAddMember.getStringExtra("apptNo");
                    setResult(RESULT_OK, mapAddMember);
                    // 받아온 변수로 서버저장 및 돌려주기
                    // 약속에 추가하기, 초기화
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonResponse = null;
                            try {
                                jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success) {
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "추가 실패했습니다.다시 시도해주세요!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    AddMemberRequest addMemberRequest = new AddMemberRequest(friendID, apptNo, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(FriendlistActivity.this);
                    queue.add(addMemberRequest);

                }
                Log.e("mapAddMember", "실패");
            }
        });
        ImageButton addfriendButton = (ImageButton) findViewById(R.id.addfriendlistButton);
        addfriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customAddfriendDialog();
            }
        });
        if(intent.getStringExtra("apptNo") != null){
            addfriendButton.setVisibility(View.GONE);
        }
        new BackgroundTask().execute();

        EditText search = (EditText) findViewById(R.id.searchFriend);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchFriend(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

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

    public void customAddfriendDialog() {
        addfriendDialog = new Dialog(FriendlistActivity.this);
        addfriendDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addfriendDialog.setContentView(R.layout.dialog_addfriend);

        final Button searchfriendButton = (Button) addfriendDialog.findViewById(R.id.searchfriendButton);
        final Button closefriendButton = (Button) addfriendDialog.findViewById(R.id.closefriendButton);
        final TextView dialogmessage = (TextView) addfriendDialog.findViewById(R.id.dialogmessage);
        final EditText findfriendID = (EditText) addfriendDialog.findViewById(R.id.findfriendID);
        final LinearLayout dialogsearch = (LinearLayout) addfriendDialog.findViewById(R.id.dialogsearch);

        searchfriendButton.setEnabled(true);
        closefriendButton.setEnabled(true);

        searchfriendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String friendID = findfriendID.getText().toString().replaceAll("\\p{Z}", "");  // 공백제거
                dialogsearch.setVisibility(View.GONE);

                if (friendID.equals(userID) || friendID.equals("")) {
                    dialogmessage.setText("친구ID를 다시 확인해주세요.");
                    searchfriendButton.setText("다시 검색");
                    searchfriendButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addfriendDialog.cancel();
                            customAddfriendDialog();
                        }
                    });
                } else {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean notUser = jsonResponse.getBoolean("success");
                                if (notUser) {
                                    dialogmessage.setText(friendID + " 님이 검색되지 않았습니다.");
                                    searchfriendButton.setText("다시 검색");
                                    searchfriendButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            addfriendDialog.cancel();
                                            customAddfriendDialog();
                                        }
                                    });
                                } else {
                                    dialogmessage.setText(friendID + " 님이 검색되었습니다. 친구로 추가하시겠습니까?");
                                    searchfriendButton.setText("추가");
                                    searchfriendButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        JSONObject jsonResponse = new JSONObject(response);
                                                        boolean success = jsonResponse.getBoolean("success");
                                                        if (success) {
                                                            new BackgroundTask().execute();
                                                            addfriendDialog.cancel();

                                                        } else {
                                                            dialogmessage.setText("이미 추가된 친구입니다. 다시 검색해주세요.");
                                                            searchfriendButton.setText("다시 검색");
                                                            searchfriendButton.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    addfriendDialog.cancel();
                                                                    customAddfriendDialog();
                                                                }
                                                            });
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            };

                                            AddfriendRequest addfriendRequest = new AddfriendRequest(userID, friendID, responseListener);
                                            RequestQueue queue = Volley.newRequestQueue(FriendlistActivity.this);
                                            queue.add(addfriendRequest);
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    ValidateRequest validateRequest = new ValidateRequest(friendID, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(FriendlistActivity.this);
                    queue.add(validateRequest);
                }
            }
        });

        closefriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addfriendDialog.cancel();
            }
        });

        addfriendDialog.show();
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
                params.put("userID", userID);

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

                Log.w("freindList", result);
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");

                int count = 0;
                String userID, userPhoto, friendNickname, userNickname, userStatusmsg;
                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);
                    userID = object.getString("friendID");
                    userPhoto = object.getString("userPhoto");
                    friendNickname = object.getString("friendNickname");
                    userNickname = object.getString("userNickname");
                    userStatusmsg = object.getString("userStatusmsg");
                    Friend friend = new Friend(userID, userPhoto, friendNickname, userNickname, userStatusmsg);
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
