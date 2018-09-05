package com.meet.now.apptsystem;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_STORAGE = 1111;

    private DdayAdapter adapter;
    private List<Dday> ddayList;
    String userID;
    String userAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userID = MyApplication.userID;
        userAddress = MyApplication.Address;

        ListView ddayListView = findViewById(R.id.ddayListView);
        ddayList = new ArrayList<>();
        adapter = new DdayAdapter(getApplicationContext(), ddayList);
        ddayListView.setAdapter(adapter);

        // 약속 상세보기 지도로 이동
        ddayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ApptCenterplaceActivity.class);
                intent.putExtra("apptNo", ddayList.get(position).getApptNo());
                startActivity(intent);
            }
        });

        // 친구추가로 이동
        Button addfriendButton = findViewById(R.id.addfriendButton);
        addfriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addfreindintent = new Intent(MainActivity.this, FriendlistActivity.class);
                addfreindintent.putExtra("apptNo","false");
                addfreindintent.putExtra("userID", userID);
                MainActivity.this.startActivity(addfreindintent);
            }
        });

        // 약속생성으로 이동
        Button makeapptButton = findViewById(R.id.makeapptButton);
        makeapptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), appt_create_activity.class);
                intent.putExtra("UserID", userID);
                startActivity(intent);
            }
        });

        // 약속목록으로 이동
        Button button = findViewById(R.id.viewapptButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), appt_list_view_activity.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

        ImageButton profileLoadBtn = findViewById(R.id.ib_profile_load);
        profileLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileLoadIntent = new Intent(MainActivity.this, ProfileLoadActivity.class);
                profileLoadIntent.putExtra("userID", userID);
                MainActivity.this.startActivity(profileLoadIntent);
            }
        });

        new BackgroundTask().execute();
        checkPermisson();

    }

    // 두번 뒤로가기 누르면 종료되도록
    private long lastTimeBackPressed;

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed < 1500){
            finish();
            return;
        }
        Toast.makeText(this, "'뒤로' 버튼을 한 번 더 눌러 종료합니다", Toast.LENGTH_SHORT).show();
        lastTimeBackPressed = System.currentTimeMillis();
    }

    @SuppressLint("StaticFieldLeak")
    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            target="http://brad903.cafe24.com/DdayList.php";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                URL url = new URL(target);
                Map<String,Object> params = new LinkedHashMap<>();
                params.put("userID", userID);

                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String,Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postDataBytes);

                Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                StringBuilder sb = new StringBuilder();
                for (int c; (c = in.read()) >= 0;)
                    sb.append((char)c);
                String response = sb.toString().trim();

                in.close();
                conn.disconnect();

                return response;

            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;

                Calendar today = Calendar.getInstance();
                Calendar d_day = Calendar.getInstance();
                long l_today = today.getTimeInMillis() / (1000 * 60 * 60 * 24);
                long l_dday, substract;

                String apptName, apptDate, apptNo;
                String[] apptDateArray;

                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);
                    apptName = object.getString("apptName");
                    apptDate = object.getString("apptDate");
                    apptNo = object.getString("apptNo");

                    apptDateArray = apptDate.split("-");
                    d_day.set(Integer.parseInt(apptDateArray[0]), Integer.parseInt(apptDateArray[1]) - 1, Integer.parseInt(apptDateArray[2]));

                    l_dday = d_day.getTimeInMillis() / (1000 * 60 * 60 * 24);

                    substract = l_today - l_dday;

                    if (substract <= 0 && substract >= -7) {  // 일주일 이내 데이터만 가져올 수 있도록
                        Dday dday = new Dday(apptName, apptDate, (int) substract, apptNo);
                        ddayList.add(dday);
                    }

                    count++;
                }

                adapter.notifyDataSetChanged();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void checkPermisson(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                new AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_STORAGE:
                for(int i=0; i<grantResults.length; i++){
                    // grantResult[] : 허용된 권한은 0, 거부한 권한은 -1
                    if(grantResults[i] < 0){
                        Toast.makeText(MainActivity.this, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // 허용했다면
                break;
        }


    }
}
