package com.meet.now.apptsystem;


import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapView;

public class ApptCenterplaceActivity extends NMapActivity {

    private ViewGroup mapLayout;
    private NMapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appt_centerplace);
        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        String apptNo =  intent.getStringExtra("apptNo");
        Toast.makeText(getApplicationContext(), userID+" "+apptNo, Toast.LENGTH_SHORT).show();


        init();
    }

    private void init(){

        mapLayout = findViewById(R.id.map_view);

        mMapView = new NMapView(this);
        mMapView.setClientId(getResources().getString(R.string.n_key)); // 클라이언트 아이디 값 설정
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.setScalingFactor(1.5f);
        mMapView.requestFocus();

        mapLayout.addView(mMapView);
    }
}
