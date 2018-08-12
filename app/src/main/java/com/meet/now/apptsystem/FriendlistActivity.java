package com.meet.now.apptsystem;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class FriendlistActivity extends AppCompatActivity {

    Dialog addfriendDialog;
    String userID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        ImageButton addfriendButton = (ImageButton) findViewById(R.id.addfriendlistButton);
        addfriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customAddfriendDialog();
            }
        });
    }

    public void customAddfriendDialog() {
        addfriendDialog = new Dialog(FriendlistActivity.this);
        addfriendDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addfriendDialog.setContentView(R.layout.dialog_addfriend);

        final Button searchfriendButton = (Button) addfriendDialog.findViewById(R.id.searchfriendButton);
        final Button closefriendButton = (Button) addfriendDialog.findViewById(R.id.closefriendButton);
        final TextView dialogmessage = (TextView) addfriendDialog.findViewById(R.id.dialogmessage);
        final EditText findfriendID = (EditText) addfriendDialog.findViewById(R.id.findfriendID);
        final LinearLayout dialogsearch = (LinearLayout) addfriendDialog.findViewById(R.id.dialogsearch);

        searchfriendButton.setEnabled(true);
        closefriendButton.setEnabled(true);

        searchfriendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String friendID = findfriendID.getText().toString().replaceAll("\\p{Z}","");  // 공백제거
                dialogsearch.setVisibility(View.GONE);

                if (friendID.equals(userID) || friendID.equals("")) {
                    dialogmessage.setText("친구ID를 다시 확인해주세요.");
                    searchfriendButton.setText("다시 검색");
                    searchfriendButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addfriendDialog.cancel();
                            customAddfriendDialog();
                        }
                    });
                } else {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean notUser = jsonResponse.getBoolean("success");
                                if (notUser) {
                                    dialogmessage.setText(friendID + " 님이 검색되지 않았습니다.");
                                    searchfriendButton.setText("다시 검색");
                                    searchfriendButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            addfriendDialog.cancel();
                                            customAddfriendDialog();
                                        }
                                    });
                                } else {
                                    dialogmessage.setText(friendID + " 님이 검색되었습니다. 친구로 추가하시겠습니까?");
                                    searchfriendButton.setText("추가");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    ValidateRequest validateRequest = new ValidateRequest(friendID, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(FriendlistActivity.this);
                    queue.add(validateRequest);
                }
            }
        });

        closefriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addfriendDialog.cancel();
            }
        });

        addfriendDialog.show();
    }

}
