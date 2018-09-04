package com.meet.now.apptsystem;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    private static final int ADD_MEMBER = 2;

    String apptNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_map_person);
        Intent intent = getIntent();
        apptNo = intent.getStringExtra("apptNo");
        Button member = findViewById(R.id.btn_map_member);
        Button nonmember = findViewById(R.id.btn_map_nonmember);

        member.setOnClickListener(this);
        nonmember.setOnClickListener(this);

    }

    void startMap() {
        Intent intent = new Intent(UpdateMapPerson.this, ApptCenterplaceActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        finish();
        startActivity(intent);
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
                startActivityForResult(sendIntent, ADD_MEMBER);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            double longitude = data.getDoubleExtra("longitude", 0);
            double latitude = data.getDoubleExtra("latitude", 0);
            String userNickname = data.getStringExtra("userNickname");
            Intent intent = getIntent();
            intent.putExtra("longitude", longitude);
            intent.putExtra("latitude", latitude);
            intent.putExtra("userNickname", userNickname);
            Log.e("userNickname", userNickname);
            setResult(RESULT_OK, intent);
            finish();

        }
    }
}
