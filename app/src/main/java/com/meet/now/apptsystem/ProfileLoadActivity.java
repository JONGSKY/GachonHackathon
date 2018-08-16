package com.meet.now.apptsystem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class ProfileLoadActivity extends Activity {

    String userNickname = null;
    String userStatusmsg = null;
    String userPhoto = null;
    String userAddress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_load);

        Intent intent = getIntent();
        final String userID = intent.getStringExtra("userID");
        Log.d("유저ID", userID);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){
                        // 화면에 이름, 사진, 상태메시지를 뿌린다.
                        Toast.makeText(getApplicationContext(),"가져오기 성공", Toast.LENGTH_SHORT).show();

                        userNickname = jsonResponse.getString("userNickname");
                        userStatusmsg = jsonResponse.getString("userStatusmsg");
                        userPhoto = jsonResponse.getString("userPhoto");
                        userAddress = jsonResponse.getString("userAddress");

                        TextView nicknameText = (TextView)findViewById(R.id.tv_user);
                        TextView statusmsgText = (TextView)findViewById(R.id.tv_introduce);
                        ImageView photoView = findViewById(R.id.iv_user);

                        nicknameText.setText(userNickname);
                        if(!userStatusmsg.equals("null")) statusmsgText.setText(userStatusmsg);

                        // 이미지 경로 찾아가 이미지 가져오기
                        // 주소 뿌리기

                    }else{
                        // 값 가져오기 실패
                        Toast.makeText(getApplicationContext(),"다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };

        ProfileLoadRequest profileLoadRequest = new ProfileLoadRequest(userID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ProfileLoadActivity.this);
        queue.add(profileLoadRequest);


    }



}
