package com.meet.now.apptsystem;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
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

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){

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

        ImageButton mapBtn = (ImageButton) findViewById(R.id.ib_map);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoc();
            }
        });
    }

    void showLoc(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_profile_loc, null);
        builder.setView(view);

        TextView tvLoc = (TextView) view.findViewById(R.id.tv_loc);
        ImageButton ibLoc = (ImageButton) view.findViewById(R.id.ib_loc);
        ImageButton ibBack = (ImageButton) view.findViewById(R.id.ib_back_loc);

        if(!userAddress.equals(null)) tvLoc.setText(userAddress);

        final AlertDialog dialog = builder.create();

        ibBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
