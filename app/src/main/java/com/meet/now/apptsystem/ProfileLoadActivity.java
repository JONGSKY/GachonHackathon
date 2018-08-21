package com.meet.now.apptsystem;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

public class ProfileLoadActivity extends AppCompatActivity {

    String userNickname = null;
    String userStatusmsg = null;
    String userPhoto = null;
    String userAddress = null;
    String userID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_load);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        // DB 프로필 정보 로드
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {

                        userNickname = jsonResponse.getString("userNickname");
                        userStatusmsg = jsonResponse.getString("userStatusmsg");
                        userPhoto = jsonResponse.getString("userPhoto");
                        userAddress = jsonResponse.getString("userAddress");

                        TextView nicknameText = (TextView) findViewById(R.id.tv_user);
                        TextView statusmsgText = (TextView) findViewById(R.id.tv_introduce);
                        ImageView photoView = findViewById(R.id.iv_user);

                        nicknameText.setText(userNickname);
                        if (!userStatusmsg.equals("null")) statusmsgText.setText(userStatusmsg);

                        // 이미지 경로 찾아가 이미지 가져오기
                        // 프로필 사진 등록하기

                        ImageView ivImage = findViewById(R.id.iv_user);
                        // 프로필 라운딩
                        //ivImage.setBackground(new ShapeDrawable(new OvalShape()));
                        //ivImage.setClipToOutline(true);


                    } else {
                        // 값 가져오기 실패
                        Toast.makeText(getApplicationContext(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        ProfileLoadRequest profileLoadRequest = new ProfileLoadRequest(userID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ProfileLoadActivity.this);
        queue.add(profileLoadRequest);
        //////////////////////////////////////////////////////////////////////////////////

        // 프로필 이미지 변경
        ImageButton ibEditImg = (ImageButton) findViewById(R.id.ib_edit_userImg);
        ibEditImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = getSupportFragmentManager();
                UpdateProfilePhoto dialogFragment = new UpdateProfilePhoto();

                Bundle bundle = new Bundle();
                bundle.putString("userID", userID);
                dialogFragment.setArguments(bundle);

                dialogFragment.show(fm, "fragment_dialog_photo");


            }
        });
        ////////////////////////////////////////////////////////////////////////////////////

        // 뒤로가기 버튼
        ImageButton backBtn = (ImageButton) findViewById(R.id.ib_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ///////////////////////////////////////////////////////////////////////////////////

        // 닉네임 수정
        ImageButton ibNickname = (ImageButton) findViewById(R.id.ib_edit_nickname);
        ibNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                UpdateProfileNickname dialogFragment = new UpdateProfileNickname();

                Bundle bundle = new Bundle();
                bundle.putString("userID", userID);
                bundle.putString("userNickname", userNickname);
                dialogFragment.setArguments(bundle);

                dialogFragment.show(fm, "fragment_dialog_nickname");
            }
        });
        // 상태메시지 수정
        ImageButton ibEditStatus = (ImageButton) findViewById(R.id.ib_edit_status);
        ibEditStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = getSupportFragmentManager();
                UpdateProfileStatus dialogFragment = new UpdateProfileStatus();

                Bundle bundle = new Bundle();
                bundle.putString("userID", userID);
                bundle.putString("userStatusmsg", userStatusmsg);
                dialogFragment.setArguments(bundle);

                dialogFragment.show(fm, "fragment_dialog_status");
            }

        });
        ///////////////////////////////////////////////////////////////////////////////

        ImageButton mapBtn = (ImageButton) findViewById(R.id.ib_map);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoc();
            }
        });
    }

    // 위치 보여주기
    void showLoc() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_profile_loc, null);
        builder.setView(view);

        TextView tvLoc = (TextView) view.findViewById(R.id.tv_loc);
        ImageButton ibLoc = (ImageButton) view.findViewById(R.id.ib_loc);
        ImageButton ibBack = (ImageButton) view.findViewById(R.id.ib_back_loc);

        if (!userAddress.equals(null)) tvLoc.setText(userAddress);

        final AlertDialog dialog = builder.create();

        ibBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    void showNick() {

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
