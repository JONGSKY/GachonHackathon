package com.meet.now.apptsystem;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class FriendlistActivity extends AppCompatActivity {

    Dialog addfriendDialog;
    Button searchfriend, closefriend;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);

        ImageButton addfriendButton = (ImageButton)findViewById(R.id.addfriendlistButton);
        addfriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customAddfriendDialog();
            }
        });
    }

    public void customAddfriendDialog(){
        addfriendDialog = new Dialog(FriendlistActivity.this);
        addfriendDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addfriendDialog.setContentView(R.layout.dialog_addfriend);
        addfriendDialog.setTitle("TestCustomDialog");

        searchfriend = (Button)addfriendDialog.findViewById(R.id.searchfriendButton);
        closefriend = (Button)addfriendDialog.findViewById(R.id.closefriendButton);

        searchfriend.setEnabled(true);
        closefriend.setEnabled(true);

        searchfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Text CustomDialog", Toast.LENGTH_LONG).show();
            }
        });

        closefriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addfriendDialog.cancel();
            }
        });

        addfriendDialog.show();
    }
}
