package com.meet.now.apptsystem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    InputMethodManager imm;
    private AlertDialog dialog;
    EditText idText;
    EditText passwordText;
    @SuppressLint("ServiceCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView registerButton = (TextView)findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        idText = (EditText) findViewById(R.id.idText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        final Button loginButton = (Button) findViewById(R.id.loginButton);
        RelativeLayout rl = findViewById(R.id.rl_login);

        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(idText.getWindowToken(),0);
                imm.hideSoftInputFromWindow(passwordText.getWindowToken(),0);
            }
        });



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 final String userID = idText.getText().toString();
                 final String userPassword = passwordText.getText().toString();

                 Response.Listener<String> responseListener = new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         try{
                             JSONObject jsonResponse = new JSONObject(response);
                             boolean success = jsonResponse.getBoolean("success");
                             if(success){
                                 String address = jsonResponse.getString("userAddress");
                                 AsyncLogin asyncLogin = new AsyncLogin();
                                 asyncLogin.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userID, userPassword, address );

                                 AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                 dialog = builder.setMessage("로그인에 성공하셨습니다.")
                                         .setPositiveButton("확인", null)
                                         .create();
                                 dialog.show();
                                 // 화면 전환
                                 Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                                 LoginActivity.this.startActivity(loginIntent);
                                 finish();
                             }else{
                                 // 로그인 실패
                                 AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                 dialog = builder.setMessage("아이디와 패스워드를 다시 확인하세요.")
                                         .setNegativeButton("다시 시도", null)
                                         .create();
                                 dialog.show();
                             }
                         }catch(Exception e){
                             e.printStackTrace();
                         }
                     }
                 };
                LoginRequest loginRequest = new LoginRequest(userID, userPassword, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(dialog != null){
            dialog.dismiss();
            dialog = null;
        }
    }


    class AsyncLogin extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... params) {
            // 로그인 정보 어플리케이션에 저장
            MyApplication.userID = params[0];
            MyApplication.password = params[1];
            MyApplication.Address = params[2];
            return null;
        }
    }
}
