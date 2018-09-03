package com.meet.now.apptsystem;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class UpdateMapPerson extends AppCompatActivity implements View.OnClickListener {
    private ApptCenterplaceActivity apptCenterplaceActivity;
    private static final int ADD_MEMBER = 500;
    private static final int ADD_NONMEMBER = 600;

    String apptNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_map_person);
        apptCenterplaceActivity = (ApptCenterplaceActivity)ApptCenterplaceActivity.apptCenterplaceActivity;
        Intent intent = getIntent();
        apptNo = intent.getStringExtra("apptNo");
        Button member = findViewById(R.id.btn_map_member);
        Button nonmember = findViewById(R.id.btn_map_nonmember);

        member.setOnClickListener(this);
        nonmember.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent sendIntent;
        int id = v.getId();
        switch (id) {
            case R.id.btn_map_member:
                sendIntent = new Intent(UpdateMapPerson.this, FriendlistActivity.class);
                sendIntent.putExtra("apptNo", apptNo);
                startActivityForResult(sendIntent, ADD_MEMBER);
                break;
            case R.id.btn_map_nonmember:
                sendIntent = new Intent(UpdateMapPerson.this, UpdateMapPersonNon.class);
                sendIntent.putExtra("apptNo", apptNo);
                startActivityForResult(sendIntent, ADD_NONMEMBER);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_MEMBER) {
                Intent intent = new Intent(UpdateMapPerson.this, ApptCenterplaceActivity.class);
                apptCenterplaceActivity.finish();
                finish();
                startActivity(intent);

            } else if (requestCode == ADD_NONMEMBER && data != null) {
                // 비회원 추가 및 point 변환
                String nonMemberID = data.getStringExtra("nonMemberID");
                String nonNickName = data.getStringExtra("nonNickName");
                String apptNo = data.getStringExtra("apptNo");
                String addr = data.getStringExtra("addr");

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                Intent intent = new Intent(UpdateMapPerson.this, ApptCenterplaceActivity.class);
                                apptCenterplaceActivity.finish();
                                finish();
                                startActivity(intent);

                            } else {
                                Toast.makeText(getApplicationContext(), "추가 실패했습니다.다시 시도해주세요!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                AddNonMemberRequest addNonMemberRequest = new AddNonMemberRequest(nonMemberID, apptNo, nonNickName, addr, responseListener);
                RequestQueue queue = Volley.newRequestQueue(UpdateMapPerson.this);
                queue.add(addNonMemberRequest);
            }
        }
    }
}
