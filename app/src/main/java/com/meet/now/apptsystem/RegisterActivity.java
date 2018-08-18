package com.meet.now.apptsystem;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity {

    private ArrayAdapter adapter;
    private Spinner spinner;
    private String userID;
    private String userPassword;
    private String userNickname;
    private String userAddress;
    private String userAge;
    private String userGender;
    private AlertDialog dialog;
    private boolean validate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        spinner = (Spinner) findViewById(R.id.ageSpinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.age, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final EditText idText = (EditText) findViewById(R.id.idText);
        final Button validateButton = (Button) findViewById(R.id.validateButton);
        final EditText passwordText = (EditText) findViewById(R.id.passwordText);
        final EditText nicknameText = (EditText) findViewById(R.id.nicknameText);
        final EditText addressText = (EditText) findViewById(R.id.addressText);

        Intent intent = getIntent();
        idText.setText(intent.getStringExtra("userID"));
        passwordText.setText(intent.getStringExtra("userPassword"));
        nicknameText.setText(intent.getStringExtra("userNickname"));
        addressText.setText(intent.getStringExtra("userAddress"));
        if(intent.getBooleanExtra("validate", false) == true){
            validate = true;
            idText.setEnabled(false);
            idText.setBackgroundColor(getResources().getColor(R.color.colorGray));
            validateButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
        };
        spinner.setSelection(intent.getIntExtra("userAge", 0));

        final RadioGroup genderGroup = (RadioGroup) findViewById(R.id.genderGroup);
        int genderGroupID = genderGroup.getCheckedRadioButtonId();
        userGender = ((RadioButton)findViewById(genderGroupID)).getText().toString();

        ((RadioButton)genderGroup.getChildAt(intent.getIntExtra("userGender",0))).setChecked(true);

        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton genderButton = (RadioButton) findViewById(i);
                userGender = genderButton.getText().toString();
            }
        });

        validateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String userID = idText.getText().toString();
                if(validate){  // validate 진행했다면
                    return;
                } else if(userID.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("아이디는 빈 칸일 수 없습니다.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                } else {
                    Response.Listener<String> responseListener = new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                             try{
                                 JSONObject jsonResponse = new JSONObject(response);
                                 boolean success = jsonResponse.getBoolean("success");
                                 if(success){
                                     AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                     dialog = builder.setMessage("사용할 수 있는 아이디입니다.")
                                             .setPositiveButton("확인", null)
                                             .create();
                                     dialog.show();
                                     idText.setEnabled(false);  // 더이상 못 바꾸게
                                     validate = true;
                                     idText.setBackgroundColor(getResources().getColor(R.color.colorGray));
                                     validateButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
                                 }
                                 else {  // 중복체크 실패했다면
                                     AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                     dialog = builder.setMessage("사용할 수 없는 아이디입니다.")
                                             .setNegativeButton("확인", null)
                                             .create();
                                     dialog.show();
                                 }
                             } catch(Exception e){
                                e.printStackTrace();
                             }
                        }
                    };
                    ValidateRequest validateRequest = new ValidateRequest(userID, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                    queue.add(validateRequest);
                }
            }
        });

        Button addressButton = (Button) findViewById(R.id.addressButton);
        addressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int genderIndex = genderGroup.indexOfChild(genderGroup.findViewById(genderGroup.getCheckedRadioButtonId()));
                Intent addressIntent = new Intent(RegisterActivity.this, DaumWebViewActivity.class);
                addressIntent.putExtra("userID", idText.getText().toString());
                addressIntent.putExtra("userPassword", passwordText.getText().toString());
                addressIntent.putExtra("userNickname", nicknameText.getText().toString());
                addressIntent.putExtra("userAddress", addressText.getText().toString());
                addressIntent.putExtra("userAge", spinner.getSelectedItemPosition());
                addressIntent.putExtra("userGender", genderIndex);
                addressIntent.putExtra("validate", validate);
                RegisterActivity.this.startActivity(addressIntent);
                finish();
            }
        });

        Button registerButton = (Button)findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                userID = idText.getText().toString();
                userPassword = passwordText.getText().toString();
                userNickname = nicknameText.getText().toString();
                userAddress = addressText.getText().toString();
                userAge = spinner.getSelectedItem().toString();

                if(!validate){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("먼저 중복 체크를 해주세요.")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                if(userID.equals("") || userPassword.equals("") || userNickname.equals("") || userAddress.equals("") || userAge.equals("") || userGender.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("빈 칸 없이 입력해주세요")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("회원 등록에 성공했습니다")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                                finish();
                            }
                            else {  // 중복체크 실패했다면
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("회원 등록에 실패했습니다")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                RegisterRequest registerRequest = new RegisterRequest(userID, userPassword, userNickname, userAddress, userAge, userGender, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
            }
        });
    }

    @Override
    protected void onStop(){  // 회원등록 끝나고 등록 성공 dialog 닫음
         super.onStop();
         if(dialog != null){
             dialog.dismiss();
             dialog = null;
         }
    }
}
