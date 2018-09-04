package com.meet.now.apptsystem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapResourceProvider;

import java.util.HashMap;
import java.util.List;

public class ApptCenterplaceActivity extends NMapActivity implements View.OnClickListener {

    @SuppressLint("StaticFieldLeak")
    public static ApptCenterplaceActivity apptCenterplaceActivity;
    private final String TAG = "ApptCenterplaceActivity";

    private NMapView mMapView;
    private NMapController mMapController;
    private NMapResourceProvider nMapResourceProvider;
    private NMapOverlayManager mapOverlayManager;

    LoadFriendaddress loadFriendaddress;
    LoadNonaddress loadNonaddress;

    private Animation fab_open, fab_close;
    private Animation rotate_forward, rotate_backward;
    private Boolean isFabOpen = false;
    // fab1 주소검색, fab2 인원추가, fab3 지도축척변경
    private FloatingActionButton fab, fab1, fab2, fab3;

    private static final int UPDATE_DISTANCE = 3;

    String apptNo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appt_centerplace);
        final EditText editText = findViewById(R.id.et_center_place);
        editText.setVisibility(View.GONE);
        ImageButton imageButton = findViewById(R.id.ib_center_place);
        imageButton.setVisibility(View.GONE);


        apptCenterplaceActivity = ApptCenterplaceActivity.this;

        Intent intent = getIntent();

        apptNo = intent.getStringExtra("apptNo");

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);

        fab = findViewById(R.id.fab);
        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);
        fab3 = findViewById(R.id.fab3);

        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);

        loadFriendaddress = new LoadFriendaddress();
        loadFriendaddress.execute(apptNo);
        loadNonaddress = new LoadNonaddress();
        loadNonaddress.execute(apptNo);

        init();

        nMapResourceProvider = new NMapViewerResourceProvider(this);
        mapOverlayManager = new NMapOverlayManager(this, mMapView, nMapResourceProvider);

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm!= null){
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    String address = editText.getText().toString().trim();
                    Log.e("address", address);
                    if (address.equals("")) {
                        Toast.makeText(getApplicationContext(), "검색어를 입력해주세요!", Toast.LENGTH_SHORT).show();
                    } else {
                        Async_geo async_geo = new Async_geo(new AsyncListener() {
                            @Override
                            public void taskComplete(PointF point) {
                                Log.e("point", point.x + " " + point.y);

                                if (point.x == 200.0 && point.y == 200.0) {
                                    Toast.makeText(getApplicationContext(), "검색결과가 없습니다.", Toast.LENGTH_SHORT).show();
                                } else if (point.x > 126.375924 && point.x < 127.859605 && point.y > 36.889164 && point.y < 38.313650) {
                                    mMapController.setMapCenter(point.x, point.y, 11);
                                } else {
                                    Toast.makeText(getApplicationContext(), "검색 가능한 지역을 벗어났습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void taskComplete(HashMap<String, String> hashMap) {

                            }
                        });
                        async_geo.execute(address);


                    }

                }

            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_ENTER;
            }
        });

    } // onCreate end

    @SuppressLint("StaticFieldLeak")
    class Async_geo extends AsyncTask<String, Void, PointF> {
        AsyncListener asyncListener;

        Async_geo(AsyncListener asyncListener) {
            this.asyncListener = asyncListener;
        }

        @Override
        protected void onPostExecute(PointF pointF) {
            super.onPostExecute(pointF);
            if (this.asyncListener != null)
                this.asyncListener.taskComplete(pointF);
        }

        @Override
        protected PointF doInBackground(String... address) {
            return AddressToGeocode.getGeocode(address[0]);
        }
    }

    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                anim();
                break;
            case R.id.fab1:
                anim();
                Toast.makeText(this, "지도검색", Toast.LENGTH_SHORT).show();
                EditText editText = findViewById(R.id.et_center_place);
                editText.setVisibility(View.VISIBLE);
                editText.bringToFront();
                ImageButton imageButton = findViewById(R.id.ib_center_place);
                imageButton.setVisibility(View.VISIBLE);
                imageButton.bringToFront();
                break;
            case R.id.fab2:
                anim();
                Toast.makeText(this, "인원추가", Toast.LENGTH_SHORT).show();
                Intent addPerson = new Intent(ApptCenterplaceActivity.this, UpdateMapPerson.class);
                addPerson.putExtra("apptNo", apptNo);
                startActivity(addPerson);

                break;
            case R.id.fab3:
                anim();
                Toast.makeText(this, "지도축척변경", Toast.LENGTH_SHORT).show();
                Intent editDis = new Intent(ApptCenterplaceActivity.this, UpdateMapDistance.class);
                startActivityForResult(editDis, UPDATE_DISTANCE);

        }

    }

    // 지도 축척 변경 결과값

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("result", requestCode + " " + resultCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == UPDATE_DISTANCE && data != null) {
                int result = Integer.parseInt(data.getStringExtra(UpdateMapDistance.INTENT_RESULT));
                if (result != 0) {
                    // 지도 축척 변경
                    NGeoPoint nGeoPoint = mMapController.getMapCenter();
                    mMapController.setMapCenter(nGeoPoint, result);
                    // 추천 위치 추가 필요 및 단위 변경
                }
            }

        }
    }

    public void anim() {

        if (isFabOpen) {
            fab.startAnimation(rotate_backward);
            fab3.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.startAnimation(fab_close);
            fab3.setClickable(false);
            fab2.setClickable(false);
            fab1.setClickable(false);
            isFabOpen = false;
        } else {
            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            isFabOpen = true;
        }
    }

    private void init() {

        mMapView = findViewById(R.id.map_view);
        mMapView.setVisibility(View.INVISIBLE);
        mMapView.setClientId(getResources().getString(R.string.n_key)); // 클라이언트 아이디 값 설정
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.setScalingFactor(1.7f);
        mMapView.requestFocus();
        mMapView.setOnMapStateChangeListener(changeListener);
        mMapView.setOnMapViewTouchEventListener(mapListener);
        mMapController = mMapView.getMapController();
        mMapController.setMapCenter(new NGeoPoint(126.978371, 37.5666091), 11);     //Default Data
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setMarker();
                mMapView.setVisibility(View.VISIBLE);
            }
        }, 3000);


    }

    private void setMarker() {
        List<MapApptfriend> mapApptfriendList = LoadFriendaddress.mapApptfriendList;
        List<MapApptfriend> mapApptNonList = LoadNonaddress.mapApptNonList;
        int markerId = NMapPOIflagType.PIN;
        int spotId = NMapPOIflagType.SPOT;


        int size = mapApptfriendList.size() + 1;
        if (mapApptNonList != null) {
            size += mapApptNonList.size();
        }

        // set POI data
        NMapPOIdata poiData = new NMapPOIdata(size, nMapResourceProvider);
        poiData.beginPOIdata(size);
        NGeoPoint middleSpot = new NGeoPoint();
        int spotCount = 0;
        for (int i = 0; i < mapApptfriendList.size(); i++) {
            MapApptfriend mapApptfriend = mapApptfriendList.get(i);
            double coordX = mapApptfriend.getPoint().x;
            double coordY = mapApptfriend.getPoint().y;
            if (coordX > 126.375924 && coordX < 127.859605 && coordY > 36.889164 && coordY < 38.313650) {  // 경기, 서울로 마크 표시 제한
                poiData.addPOIitem(coordX, coordY, mapApptfriend.userNickname, markerId, 0);

                middleSpot.longitude += coordX;
                middleSpot.latitude += coordY;
                spotCount++;
            }
        }
        if (mapApptNonList != null) {
            // 비회원 마크
            for (int i = 0; i < mapApptNonList.size(); i++) {
                MapApptfriend mapApptfriend = mapApptNonList.get(i);
                double coordX = mapApptfriend.getPoint().x;
                double coordY = mapApptfriend.getPoint().y;
                if (coordX > 126.375924 && coordX < 127.859605 && coordY > 36.889164 && coordY < 38.313650) {  // 경기, 서울로 마크 표시 제한
                    poiData.addPOIitem(coordX, coordY, mapApptfriend.userNickname, markerId, 0);
                    middleSpot.longitude += coordX;
                    middleSpot.latitude += coordY;
                    spotCount++;
                }
            }
        }
        poiData.addPOIitem(middleSpot.longitude / spotCount, middleSpot.latitude / spotCount, "중간지점", spotId, 0);

        Log.e("middle", middleSpot.longitude / spotCount + " " + middleSpot.latitude / spotCount + " spotCount " + spotCount);
        poiData.endPOIdata();

        // create POI data overlay
        NMapPOIdataOverlay poiDataOverlay = mapOverlayManager.createPOIdataOverlay(poiData, null);
        poiDataOverlay.showAllPOIdata(0);
        poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);  //좌표 클릭시 말풍선 리스
    }

    private NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateChangeListener = new NMapPOIdataOverlay.OnStateChangeListener() {
        @Override
        public void onFocusChanged(NMapPOIdataOverlay nMapPOIdataOverlay, NMapPOIitem nMapPOIitem) {

        }

        @Override
        public void onCalloutClick(NMapPOIdataOverlay nMapPOIdataOverlay, NMapPOIitem nMapPOIitem) {
            if (nMapPOIitem != null) {
                Log.e(TAG, "onFocusChanged: " + nMapPOIitem.toString());
            } else {
                Log.e(TAG, "onFocusChanged: ");
            }
        }
    };


    private NMapView.OnMapStateChangeListener changeListener = new NMapView.OnMapStateChangeListener() {
        @Override
        public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {
            Log.e(TAG, "OnMapStateChangeListener onMapInitHandler : ");
        }

        @Override
        public void onMapCenterChange(NMapView nMapView, NGeoPoint nGeoPoint) {
            Log.e(TAG, "OnMapStateChangeListener onMapCenterChange : " + nGeoPoint.getLatitude() + " ㅡ  " + nGeoPoint.getLongitude());
        }

        @Override
        public void onMapCenterChangeFine(NMapView nMapView) {
            Log.e(TAG, "OnMapStateChangeListener onMapCenterChangeFine : ");
        }

        @Override
        public void onZoomLevelChange(NMapView nMapView, int i) {
            Log.e(TAG, "OnMapStateChangeListener onZoomLevelChange : " + i);
        }

        @Override
        public void onAnimationStateChange(NMapView nMapView, int i, int i1) {
            Log.e(TAG, "OnMapStateChangeListener onAnimationStateChange : ");
        }
    };

    private NMapView.OnMapViewTouchEventListener mapListener = new NMapView.OnMapViewTouchEventListener() {
        @Override
        public void onLongPress(NMapView nMapView, MotionEvent motionEvent) {
            Log.e(TAG, "OnMapViewTouchEventListener onLongPress : ");
        }

        @Override
        public void onLongPressCanceled(NMapView nMapView) {
            Log.e(TAG, "OnMapViewTouchEventListener onLongPressCanceled : ");
        }

        @Override
        public void onTouchDown(NMapView nMapView, MotionEvent motionEvent) {
            Log.e(TAG, "OnMapViewTouchEventListener onTouchDown : ");
        }

        @Override
        public void onTouchUp(NMapView nMapView, MotionEvent motionEvent) {
            Log.e(TAG, "OnMapViewTouchEventListener onTouchUp : ");
        }

        @Override
        public void onScroll(NMapView nMapView, MotionEvent motionEvent, MotionEvent motionEvent1) {
            Log.e(TAG, "OnMapViewTouchEventListener onScroll : ");
        }

        @Override
        public void onSingleTapUp(NMapView nMapView, MotionEvent motionEvent) {
            Log.e(TAG, "OnMapViewTouchEventListener onSingleTapUp : ");
            EditText editText = findViewById(R.id.et_center_place);
            editText.setVisibility(View.GONE);
            ImageButton imageButton = findViewById(R.id.ib_center_place);
            imageButton.setVisibility(View.GONE);

            if (nMapView != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) imm.hideSoftInputFromWindow(nMapView.getWindowToken(), 0);
            }
        }
    };
}




