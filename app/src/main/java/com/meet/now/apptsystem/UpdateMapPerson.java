package com.meet.now.apptsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class UpdateMapPerson extends AppCompatActivity implements View.OnClickListener {

    private static final int ADD_MEMBER = 500;
    private static final int ADD_NONMEMBER = 600;
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
        switch (id){
            case R.id.btn_map_member:
                sendIntent = new Intent(UpdateMapPerson.this, UpdateMapPersonMem.class);
                sendIntent.putExtra("apptNo", apptNo);
                startActivityForResult(sendIntent, ADD_MEMBER);
                break;
            case R.id.btn_map_nonmember:
                sendIntent = new Intent(UpdateMapPerson.this ,UpdateMapPersonNon.class);
                Toast.makeText(getApplicationContext(), "비회원추가", Toast.LENGTH_SHORT).show();
                sendIntent.putExtra("apptNo", apptNo);
                startActivityForResult(sendIntent, ADD_NONMEMBER);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = getIntent();
        if(resultCode == RESULT_OK){
            if(requestCode == ADD_MEMBER){
                Toast.makeText(getApplicationContext(),"addMember", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, intent);
                finish();

            }else if(requestCode == ADD_NONMEMBER && data != null){
                // 받은 결과값을 넘겨준다
                String friendName = data.getStringExtra("friendName");
                String friendAddr = data.getStringExtra("friendAddr");
                intent.putExtra("friendName", friendName);
                intent.putExtra("friendAddr", friendAddr);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        finish();
    }
}
