package com.meet.now.apptsystem;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static String userID;

    private ListView ddayListView;
    private DdayAdapter adapter;
    private List<Dday> ddayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        ddayListView = (ListView)findViewById(R.id.ddayListView);
        ddayList = new ArrayList<Dday>();
        adapter = new DdayAdapter(getApplicationContext(), ddayList);
        ddayListView.setAdapter(adapter);


        Button addfriendButton = (Button)findViewById(R.id.addfriendButton);
        addfriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addfreindintent = new Intent(MainActivity.this, FriendlistActivity.class);
                addfreindintent.putExtra("userID", userID);
                MainActivity.this.startActivity(addfreindintent);
            }
        });

        new BackgroundTask().execute();
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
            try{
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");

                int count = 0;
                String apptName, apptDate;
                int dDay;
                while(count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);
                    apptName = object.getString("apptName");
                    apptDate = object.getString("apptDate");
                    Dday dday= new Dday(apptName, apptDate, 27);
                    ddayList.add(dday);
                    count++;
                }
                adapter.notifyDataSetChanged();

            }catch(Exception e){
                e.printStackTrace();
            }
        }


    }
}
