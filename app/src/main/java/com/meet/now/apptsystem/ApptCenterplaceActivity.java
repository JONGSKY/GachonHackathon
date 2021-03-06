package com.meet.now.apptsystem;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;

public class ApptCenterplaceActivity extends NMapActivity implements View.OnClickListener {
    private final String TAG = "ApptCenterplaceActivity";
    private int async_count = 3;

    private static int settingMiddle = 1;
    private NMapView mMapView;
    private NMapController mMapController;
    private NMapResourceProvider nMapResourceProvider;
    private NMapOverlayManager mapOverlayManager;
    private String finalApptPlace = "null";

    LoadFriendaddress loadFriendaddress;
    LoadNonaddress loadNonaddress;
    LoadHotplace loadHotplace;
    NGeoPoint middleSpot;
    NMapPOIdata poiData;
    NMapPOIdata hotplacePoiData;


    private Animation fab_open, fab_close;
    private Animation rotate_forward, rotate_backward;
    private Boolean isFabOpen = false;
    // fab1 주소검색, fab2 인원추가, fab3 지도축척변경
    private FloatingActionButton fab, fab1, fab2, fab3;

    private static final int UPDATE_DISTANCE = 3;
    private static final int ADD_MEMBER = 2;
    private static final int UPDATE_MIDDLE = 4;
    private static final int STORE_LIST = 5;

    private int memberCount;

    String apptNo;
    String apptName;
    TextView cnt;
    int stopCount = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appt_centerplace);
        final EditText editText = findViewById(R.id.et_center_place);
        editText.setVisibility(GONE);
        ImageButton imageButton = findViewById(R.id.ib_center_place);
        imageButton.setVisibility(GONE);
        Button button = findViewById(R.id.btn_refresh);
        button.setOnClickListener(this);
        button.setVisibility(GONE);
        ImageButton setting = findViewById(R.id.settingPlace);
        setting.setOnClickListener(this);

        Intent intent = getIntent();
        apptNo = intent.getStringExtra("apptNo");
        apptName = intent.getStringExtra("apptName");

        TextView title = findViewById(R.id.tv_appt_name_map);
        title.setText(apptName);
        cnt = findViewById(R.id.tv_appt_cnt_map);


        loadFriendaddress = new LoadFriendaddress(new AsyncNullListener() {
            @Override
            public void taskComplete() {
                async_count--;
                if(async_count == 0) {
                    setMarker();
                }
            }
        });
        loadFriendaddress.execute(apptNo);
        loadNonaddress = new LoadNonaddress(new AsyncNullListener() {
            @Override
            public void taskComplete() {
                async_count--;
                if(async_count == 0) {
                    setMarker();
                }
            }
        });
        loadNonaddress.execute(apptNo);
        loadHotplace = new LoadHotplace(new AsyncNullListener() {
            @Override
            public void taskComplete() {
                async_count--;
                if(async_count == 0) {
                    setMarker();
                }
            }
        });
        loadHotplace.execute();

        init();

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
                    if (address.equals("")) {
                        Toast.makeText(getApplicationContext(), "검색어를 입력해주세요!", Toast.LENGTH_SHORT).show();
                    } else {
                        Async_geo async_geo = new Async_geo(new AsyncListener() {
                            @Override
                            public void taskComplete(PointF point) {
                                if (point.x == 200.0 && point.y == 200.0) {
                                    Toast.makeText(getApplicationContext(), "검색결과가 없습니다.", Toast.LENGTH_SHORT).show();
                                } else if (point.x > 126.375924 && point.x < 127.859605 && point.y > 36.889164 && point.y < 38.313650) {
                                    mMapController.setMapCenter(point.x, point.y, 11);
                                } else {
                                    Toast.makeText(getApplicationContext(), "검색 가능한 지역을 벗어났습니다.", Toast.LENGTH_SHORT).show();
                                }
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

    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                anim();
                break;
            case R.id.fab1:
                anim();
                EditText editText = findViewById(R.id.et_center_place);
                editText.setVisibility(View.VISIBLE);
                editText.bringToFront();
                ImageButton imageButton = findViewById(R.id.ib_center_place);
                imageButton.setVisibility(View.VISIBLE);
                imageButton.bringToFront();
                break;
            case R.id.fab2:
                anim();
                Intent addPerson = new Intent(ApptCenterplaceActivity.this, UpdateMapPerson.class);
                addPerson.putExtra("apptNo", apptNo);
                startActivityForResult(addPerson, ADD_MEMBER);
                break;
            case R.id.fab3:
                anim();
                Intent intent = new Intent(ApptCenterplaceActivity.this, UpdateMapDistance.class);
                startActivityForResult(intent, UPDATE_DISTANCE); // 다시 돌아올 액티비티 콜
                break;
            case R.id.btn_refresh:
                async_count = 3;
                poiData.removeAllPOIdata();
                hotplacePoiData.removeAllPOIdata();  //  기존 띄워져있는 핀 지우기
                stopCount = 0;
                loadFriendaddress = new LoadFriendaddress(new AsyncNullListener() {
                    @Override
                    public void taskComplete() {
                        async_count--;
                        if(async_count == 0) {
                            setMarker();
                        }
                    }
                });
                loadFriendaddress.execute(apptNo);
                loadNonaddress = new LoadNonaddress(new AsyncNullListener() {
                    @Override
                    public void taskComplete() {
                        async_count--;
                        if(async_count == 0) {
                            setMarker();
                        }
                    }
                });
                loadNonaddress.execute(apptNo);
                loadHotplace = new LoadHotplace(new AsyncNullListener() {
                    @Override
                    public void taskComplete() {
                        async_count--;
                        if(async_count == 0) {
                            setMarker();
                        }
                    }
                });
                loadHotplace.execute();

                Button button = findViewById(R.id.btn_refresh);
                button.setVisibility(GONE);
                break;
            case R.id.settingPlace:
                Intent updateMiddle = new Intent(ApptCenterplaceActivity.this, UpdateMapMiddle.class);
                startActivityForResult(updateMiddle, UPDATE_MIDDLE); // 다시 돌아올 액티비티 콜
                break;
        }
    }

    // 지도 축척 변경 결과값
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == UPDATE_MIDDLE && data != null){
                int result = Integer.parseInt(data.getStringExtra(UpdateMapDistance.INTENT_RESULT));
                if(result != 0){
                    // 중심위치 찾기 변경
                    poiData.removeAllPOIdata(); // 기존 친구 핀 지우기
                    hotplacePoiData.removeAllPOIdata();  //  기존 핫플레이스 핀 지우기
                    switch(result){
                        case 1:
                            // 평균
                            settingMiddle = 1;
                            setMarker();
                            break;
                        case 2:
                            // 멀리사는 친구
                            settingMiddle = 2;
                            setMarker();
                            break;
                        default:
                            break;
                    }
                }
            }
            if (requestCode == UPDATE_DISTANCE && data != null) {
                int result = Integer.parseInt(data.getStringExtra(UpdateMapDistance.INTENT_RESULT));
                if(result != 0){
                    // 지도 축척 변경
                    NMapController nMapController = mMapView.getMapController();
                    int hotspotId = NMapPOIflagType.HOTSPOT;
                    JSONArray hotplaceList = LoadHotplace.hotplaceList;
                    hotplacePoiData.removeAllPOIdata();  //  기존 띄워져있는 핀 지우기
                    hotplacePoiData = new NMapPOIdata(hotplaceList.length(), nMapResourceProvider);  // 새로운 객체 생성
                    hotplacePoiData.beginPOIdata(hotplaceList.length());
                    double manify = 0.008*1;
                    switch(result){
                        case 11:
                            break;
                        case 10:
                            manify = 0.008*2;
                            break;
                        case 9:
                            manify = 0.008*3;
                            break;
                        case 8:
                            manify = 0.008*5;
                            break;
                        default:
                    }

                    for(int i=0; i<hotplaceList.length(); i++){
                        try{
                            JSONObject hotplace = hotplaceList.getJSONObject(i);
                            NGeoPoint hotPoint = new NGeoPoint();
                            hotPoint.latitude = Double.parseDouble(hotplace.getString("latitude"));
                            hotPoint.longitude = Double.parseDouble(hotplace.getString("longitude"));

                            if(hotPoint.longitude-middleSpot.longitude>-manify && hotPoint.longitude-middleSpot.longitude<manify && hotPoint.latitude-middleSpot.latitude<manify && hotPoint.latitude-middleSpot.latitude>-manify){
                                NMapPOIitem item = hotplacePoiData.addPOIitem(hotPoint, hotplace.getString("placeName"), hotspotId, 1);
                                item.setRightAccessory(true, NMapPOIflagType.CLICKABLE_ARROW);
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                    }

                    hotplacePoiData.endPOIdata();

                    // create POI data overlay
                    NMapPOIdataOverlay hotplacePoiDataOverlay = mapOverlayManager.createPOIdataOverlay(hotplacePoiData, null);
                    hotplacePoiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);

                    nMapController.setMapCenter(middleSpot, result);
                }
            }
            else if(requestCode == ADD_MEMBER){
                String userAddress = data.getStringExtra("userAddress");
                final double longitude = data.getDoubleExtra("longitude", 0);
                final double latitude = data.getDoubleExtra("latitude", 0);
                final String userNickname = data.getStringExtra("userNickname");
                if(!userAddress.equals("")){
                    // 좌표 반환
                    Async_geo async_geo = new Async_geo(new AsyncListener() {
                        @Override
                        public void taskComplete(PointF point) {
                            if(longitude != 200.0 && latitude != 200.0) {
                                newMarker(point.x, point.y, userNickname);
                            }
                        }
                    });
                    async_geo.execute(userAddress);
                }
                else {
                    if(longitude != 200.0 && latitude != 200.0){
                        newMarker(longitude, latitude, userNickname);
                    }
                }
            }else if(requestCode == STORE_LIST){
                finalApptPlace = data.getStringExtra("finalApptPlace");
            }
        }// result_ok end
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

    void init() {
        mMapView = findViewById(R.id.map_view);
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

        nMapResourceProvider = new NMapViewerResourceProvider(this);
        mapOverlayManager = new NMapOverlayManager(this, mMapView, nMapResourceProvider);

    }

    private void newMarker(double longitude, double latitude, String userNickname) {
        Button button = findViewById(R.id.btn_refresh);
        button.setVisibility(View.VISIBLE);
        int markerId = NMapPOIflagType.PIN;
        // set POI data
        NMapPOIdata newPoiData = new NMapPOIdata(1, nMapResourceProvider);
        newPoiData.beginPOIdata(1);

        if (longitude > 126.375924 && longitude < 127.859605 && latitude > 36.889164 && latitude < 38.313650) {  // 경기, 서울로 마크 표시 제한
            newPoiData.addPOIitem(longitude, latitude, userNickname, markerId, 0);
        }
        newPoiData.endPOIdata();

        // create POI data overlay
        NMapPOIdataOverlay newPoiDataOverlay = mapOverlayManager.createPOIdataOverlay(newPoiData, null);
        NMapPOIdataOverlay poiDataOverlay = mapOverlayManager.createPOIdataOverlay(poiData, null);
        poiDataOverlay.showAllPOIdata(4);
        newPoiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);  //좌표 클릭시 말풍선 리스
    }

    private void setMarker() {
        List<MapApptfriend> mapApptfriendList = LoadFriendaddress.mapApptfriendList;
        List<MapApptfriend> mapApptNonList = LoadNonaddress.mapApptNonList;
        JSONArray hotplaceList = LoadHotplace.hotplaceList;

        int markerId = NMapPOIflagType.PIN;
        int spotId = NMapPOIflagType.SPOT;
        int hotspotId = NMapPOIflagType.HOTSPOT;

        if (mapApptNonList != null && stopCount == 0) {
            mapApptfriendList.addAll(mapApptNonList);
            stopCount++;
        }

        int size = mapApptfriendList.size();
        memberCount = size;
        TextView tv = findViewById(R.id.tv_appt_cnt_map);
        String setCnt = String.valueOf(size) + " 명";
        tv.setText(setCnt);
        size += 1; // 중심위치 추가


        // set POI data
        poiData = new NMapPOIdata(size, nMapResourceProvider);
        poiData.beginPOIdata(size);
        hotplacePoiData = new NMapPOIdata(hotplaceList.length(), nMapResourceProvider);
        hotplacePoiData.beginPOIdata(hotplaceList.length());
        middleSpot = new NGeoPoint();

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

        // 중심지점
        if(settingMiddle == 2){
            // 멀리사는 친구로 구하기
            double MaxX = mapApptfriendList.get(0).getPoint().x;
            double MaxY = mapApptfriendList.get(0).getPoint().y;

            double MinX = mapApptfriendList.get(0).getPoint().x;
            double MinY = mapApptfriendList.get(0).getPoint().y;

            for(int i=0; i<mapApptfriendList.size(); i++) {
                MapApptfriend mapApptfriend = mapApptfriendList.get(i);

                double coordX = mapApptfriend.getPoint().x;
                double coordY = mapApptfriend.getPoint().y;

                if(MaxX+MaxY*1.3 < coordX+coordY*1.3){
                    MaxX = coordX; MaxY = coordY;
                }

                if(MinX+MinY*1.3 > coordX+coordY*1.3){
                    MinX = coordX; MinY = coordY;
                }
            }

            middleSpot.longitude = (MaxX+MinX)/2.0;
            middleSpot.latitude = (MaxY+MinY)/2.0;
            poiData.addPOIitem(middleSpot.longitude, middleSpot.latitude, "중간지점", spotId, 0);
        }else{
            // 평균으로 구하기
            middleSpot.longitude = middleSpot.longitude / spotCount;
            middleSpot.latitude = middleSpot.latitude / spotCount;
            poiData.addPOIitem(middleSpot.longitude, middleSpot.latitude, "중간지점", spotId, 0);
        }

            poiData.endPOIdata();

        for(int i=0; i<hotplaceList.length(); i++){
            try{
                JSONObject hotplace = hotplaceList.getJSONObject(i);
                NGeoPoint hotPoint = new NGeoPoint();
                hotPoint.latitude = Double.parseDouble(hotplace.getString("latitude"));
                hotPoint.longitude = Double.parseDouble(hotplace.getString("longitude"));

                double manify = 0.008*1;
                if(hotPoint.longitude-middleSpot.longitude>-manify && hotPoint.longitude-middleSpot.longitude<manify && hotPoint.latitude-middleSpot.latitude<manify && hotPoint.latitude-middleSpot.latitude>-manify){
                    NMapPOIitem item = hotplacePoiData.addPOIitem(hotPoint, hotplace.getString("placeName"), hotspotId, 1);
                    item.setRightAccessory(true, NMapPOIflagType.CLICKABLE_ARROW);
                }
            }catch(Exception e){
                e.printStackTrace();
            }

        }

        hotplacePoiData.endPOIdata();

        // create POI data overlay
        NMapPOIdataOverlay poiDataOverlay = mapOverlayManager.createPOIdataOverlay(poiData, null);
        NMapPOIdataOverlay hotplacePoiDataOverlay = mapOverlayManager.createPOIdataOverlay(hotplacePoiData, null);
        poiDataOverlay.showAllPOIdata(0);
        hotplacePoiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);
    }

    private NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateChangeListener = new NMapPOIdataOverlay.OnStateChangeListener() {

        String dong = null;
        String gu = null;

        @Override
        public void onFocusChanged(NMapPOIdataOverlay nMapPOIdataOverlay, NMapPOIitem nMapPOIitem) {
            Log.w(TAG, "onFocusChanged: ");
        }

        @Override
        public void onCalloutClick(NMapPOIdataOverlay nMapPOIdataOverlay, final NMapPOIitem nMapPOIitem) {
            if (nMapPOIitem != null) {

                GeocodeToAddress geoToadd = new GeocodeToAddress(new AddressAsyncResponse(){
                    @Override
                    public void processFinish(HashMap<String, String> output) {
                        dong = output.get("dong");
                        gu = output.get("gu");

                        Intent intent = new Intent(ApptCenterplaceActivity.this, StoreListActivity.class);
                        intent.putExtra("dong", dong);
                        intent.putExtra("gu", gu);
                        intent.putExtra("title", nMapPOIitem.getTitle());
                        intent.putExtra("apptNo", apptNo);
                        startActivityForResult(intent, STORE_LIST);
                    }
                });
                geoToadd.execute(nMapPOIitem.toString());

                Log.w(TAG, "onCalloutClick: " + nMapPOIitem.toString());
            } else {
                Log.w(TAG, "onCalloutClick: ");
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
            editText.setVisibility(GONE);
            ImageButton imageButton = findViewById(R.id.ib_center_place);
            imageButton.setVisibility(GONE);

            if (nMapView != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) imm.hideSoftInputFromWindow(nMapView.getWindowToken(), 0);
            }
        }
    };

    @Override
    public void onBackPressed() {
        Intent apptDetailIntent = new Intent();
        int apptPosition = getIntent().getIntExtra("apptPosition", 0);
        apptDetailIntent.putExtra("UserID", MyApplication.userID);
        apptDetailIntent.putExtra("memberCount", memberCount);
        apptDetailIntent.putExtra("apptPosition", apptPosition);
        apptDetailIntent.putExtra("finalApptPlace", finalApptPlace);
        setResult(1, apptDetailIntent);
        finish();
        super.onBackPressed();
    }
}




