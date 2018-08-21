package com.meet.now.apptsystem;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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


        Button addfriendButton = (Button) findViewById(R.id.addfriendButton);
        addfriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addfreindintent = new Intent(MainActivity.this, FriendlistActivity.class);
                addfreindintent.putExtra("userID", userID);
                MainActivity.this.startActivity(addfreindintent);
            }
        });

        Button makeapptButton = (Button) findViewById(R.id.makeapptButton);
        makeapptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent makeapptIntent = new Intent(MainActivity.this, CreateApptSubActivity.class);
                makeapptIntent.putExtra("userID", userID);
                MainActivity.this.startActivity(makeapptIntent);
            }
        });


        // 프로필로드 테스트중
        ImageButton profileLoadBtn = findViewById(R.id.ib_profile_load);
        profileLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileLoadIntent = new Intent(MainActivity.this, ProfileLoadActivity.class);
                profileLoadIntent.putExtra("userID", userID);
                MainActivity.this.startActivity(profileLoadIntent);
            }
        });


    }

    public Context mContext;
    final static int MY_PERMISSION_CAMERA = 1;
    public boolean isCheck(String permission) {
        switch (permission) {
            case "camera":
                // 권한없다면
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        new AlertDialog.Builder(mContext)
                                .setTitle("알림")
                                .setMessage("저장소 권한은 거부되었습니다.")
                                // 권한설정
                                .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.setData(Uri.parse("package:com.meet.now.apptsystem"));
                                        mContext.startActivity(intent);
                                    }
                                })
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ((Activity) mContext).finish();
                                    }
                                })
                                .setCancelable(false)
                                .create()
                                .show();

                        //권한 있다면 권한요구
                    } else {
                        ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_CAMERA);
                    }


                } else {
                    return true;
                }
                break;
        }
        return true;
    }

    }