package com.meet.now.apptsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateMapPersonNon extends AppCompatActivity implements View.OnClickListener {
    private static final int ADD_ADDR = 501;

    TextView friendAddr;
    EditText friendName;
    double longitude;
    double latitude;
    String nonMemberID, apptNo, nonNickName, addr;

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
        switch (id) {
            case R.id.ib_non_loc:
                // 지도에서 위치 선택하고 주소 가져오기
                Intent mapIntent = new Intent(UpdateMapPersonNon.this, UpdateMapPersonAddAddr.class);
                apptNo = intent.getStringExtra("apptNo");
                mapIntent.putExtra("apptNo", apptNo);
                startActivityForResult(mapIntent, ADD_ADDR);
                // 좌표값 주소로 반환받아 EDIT TEXT 에 반영.
                break;

            case R.id.btn_non_ok:
                if (friendName.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "친구 이름을 입력해주세요!", Toast.LENGTH_SHORT).show();
                    break;
                } else if (friendAddr.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "주소를 입력해주세요!", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    // 비회원 등록하고 포인트와 닉네임 반환
                    // nonMemberID,apptNo,nonNickName,nonAddr
                    nonMemberID = "non_" + System.currentTimeMillis();
                    nonNickName = friendName.getText().toString();
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success) {
                                } else {
                                    Toast.makeText(getApplicationContext(), "추가 실패했습니다.다시 시도해주세요!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    AddNonMemberRequest addNonMemberRequest = new AddNonMemberRequest(nonMemberID, apptNo, nonNickName, addr, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(UpdateMapPersonNon.this);
                    queue.add(addNonMemberRequest);

                    intent.putExtra("userAddress", "");
                    intent.putExtra("longitude", longitude );
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("userNickname", nonNickName);
                    Log.e("longitude", longitude+"");
                    setResult(RESULT_OK, intent);
                    finish();
                }
            case R.id.btn_non_cancel:
                finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_ADDR) {
                // 반환받은 주소를 EditText에 반영
                if (data != null) {
                    addr = data.getStringExtra("friendAddr");
                    if (addr != null) friendAddr.setText(addr);
                    longitude = data.getDoubleExtra("longitude",0);
                    latitude = data.getDoubleExtra("latitude", 0);
                }
            }
        }
    }
}
