package com.meet.now.apptsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        final String userID = intent.getStringExtra("userID");

        Button addfriendButton = (Button)findViewById(R.id.addfriendButton);
        addfriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addfreindintent = new Intent(MainActivity.this, FriendlistActivity.class);
                addfreindintent.putExtra("userID", userID);
                MainActivity.this.startActivity(addfreindintent);
            }
        });

        // 프로필로드 테스트중
        ImageButton profileLoadBtn = findViewById(R.id.ib_profile_load);
        profileLoadBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent profileLoadIntent = new Intent(MainActivity.this, ProfileLoadActivity.class);
                profileLoadIntent.putExtra("userID", userID);
                MainActivity.this.startActivity(profileLoadIntent);
            }
        });
        //////////////////////////////////////////////////////////////////////////////


    }


}
