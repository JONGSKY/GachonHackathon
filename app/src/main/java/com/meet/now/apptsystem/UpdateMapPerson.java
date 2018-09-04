package com.meet.now.apptsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import static android.content.Intent.FLAG_ACTIVITY_FORWARD_RESULT;

public class UpdateMapPerson extends AppCompatActivity implements View.OnClickListener {

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

    @Override
    public void onClick(View v) {
        Intent sendIntent;
        int id = v.getId();
        switch (id) {
            case R.id.btn_map_member:
                sendIntent = new Intent(UpdateMapPerson.this, FriendlistActivity.class);
                sendIntent.putExtra("apptNo", apptNo);
                sendIntent.setFlags(FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(sendIntent);
                finish();
                break;
            case R.id.btn_map_nonmember:
                sendIntent = new Intent(UpdateMapPerson.this, UpdateMapPersonNon.class);
                sendIntent.putExtra("apptNo", apptNo);
                sendIntent.setFlags(FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(sendIntent);
                finish();
                break;
        }
    }
}
