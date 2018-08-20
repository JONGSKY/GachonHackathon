package com.meet.now.apptsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public static String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
         userID = intent.getStringExtra("userID");

        Button addfriendButton = (Button)findViewById(R.id.addfriendButton);
        addfriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addfreindintent = new Intent(MainActivity.this, FriendlistActivity.class);
                addfreindintent.putExtra("userID", userID);
                MainActivity.this.startActivity(addfreindintent);
            }
        });
    }

}
