package com.meet.now.apptsystem.updateAddr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.meet.now.apptsystem.R;
import com.nhn.android.maps.NMapView;

public class FragmentMapActivity extends FragmentActivity {
    private NMapView mMapView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_update_profile_addr);
        mMapView = (NMapView)findViewById(R.id.mapView);

        // initialize map view
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();
    }
}
