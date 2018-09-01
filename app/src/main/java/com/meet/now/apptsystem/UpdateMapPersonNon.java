package com.meet.now.apptsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateMapPersonNon extends AppCompatActivity implements View.OnClickListener {
    private static final int ADD_ADDR = 501;

    TextView friendAddr;
    EditText friendName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_map_person_nonmember);


        friendName = findViewById(R.id.et_non_nick);
        friendAddr = findViewById(R.id.et_non_addr);
        ImageButton loc = findViewById(R.id.ib_non_loc);
        Button ok = findViewById(R.id.btn_non_ok);
        Button cancel = findViewById(R.id.btn_non_cancel);

        loc.setOnClickListener(this);
        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = getIntent();
        switch (id){
            case R.id.ib_non_loc:
                // 지도에서 위치 선택하고 주소 가져오기
                // 지도레이아웃을 제일 위로 불러오고 선택가능하도록
                Intent mapIntent = new Intent(UpdateMapPersonNon.this, UpdateMapPersonAddAddr.class);
                mapIntent.putExtra("apptNo", intent.getStringExtra("apptNo"));
                startActivityForResult(mapIntent, ADD_ADDR);
                // 좌표값 주소로 반환받아 EDIT TEXT 에 반영.
                break;

            case R.id.btn_non_ok:
                if(friendName.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(),"친구 이름을 입력해주세요!", Toast.LENGTH_SHORT).show();
                    break;
                }else if(friendAddr.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "주소를 입력해주세요!", Toast.LENGTH_SHORT).show();
                    break;
                }else{
                    intent.putExtra("friendName", friendName.getText().toString());
                    intent.putExtra("friendAddr", friendAddr.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            case R.id.btn_non_cancel:
                finish();
        }
    }

    String addr;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == ADD_ADDR){
                // 반환받은 주소를 EditText에 반영
                if(data != null){
                    String addr = data.getStringExtra("friendAddr");
                    Toast.makeText(getApplicationContext(),addr,Toast.LENGTH_SHORT).show();
                    double longitude = data.getDoubleExtra("longitude",0);
                    double latitude = data.getDoubleExtra("latitude",0);
                    if(addr != null) friendAddr.setText(addr);
                }

            }
        }
    }
}
