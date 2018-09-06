package com.meet.now.apptsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class appt_list_view_activity extends AppCompatActivity{

    private static String TAG = "phptest_MainActivity";

    private static final String TAG_JSON = "response";
    private static final String TAG_Appt_Name = "ApptName";
    private static final String TAG_Date = "Date";


    ArrayList<HashMap<String, ArrayList<String>>> mArrayList;
    ListView mlistView;
    String mJsonString;
    LinearLayout appt_name_linearlayout;
    private String userID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appt_list_view);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        mlistView = findViewById(R.id.appt_listView);
        mArrayList = new ArrayList<>();

        appt_name_linearlayout = findViewById(R.id.today_appt);

        GetData task = new GetData();
        task.execute("http://brad903.cafe24.com/ApptListView.php");

        ImageButton imageButton = findViewById(R.id.calendar_change_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), appt_calendar_view_activity.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
                finish();
            }
        });

    }

    private class GetData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(appt_list_view_activity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response  - " + result);

            mJsonString = result;
            Log.w("result값 : ", result);
            showResult();

        }

        @Override
        protected String doInBackground(String... strings) {
            String serverURL = strings[0];

            try{
                URL url = new URL(serverURL);

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

            }
            catch (IOException e){
                Log.d(TAG, "InsertData:Error ", e);
                errorString = e.toString();

                return null;
            }
        }
    }

    private void showResult(){
        try{
            JSONObject jsonObject = new JSONObject(mJsonString);
            final JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            ArrayList<String> Date_String = new ArrayList<>();
            ArrayList<String> Appt_Name_String = new ArrayList<>();

            int k;
            //Date 리스트 생성
            for(k=0; k< jsonArray.length(); k++) {
                JSONObject item = jsonArray.getJSONObject(k);
                String Date = item.getString(TAG_Date);
                Date_String.add(Date);
            }

            //중복 item제거
            HashSet<String> set = new HashSet<>(Date_String);
            final ArrayList<String> Date_String_Array = new ArrayList<>(set);

            //중복 제거한 List 오름차순으로 정렬
            Ascending ascending = new Ascending();
            Collections.sort(Date_String_Array, ascending);

            //Appt_Name 리스트 생성
            for(int i=0; i< jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String Appt_Name = item.getString(TAG_Appt_Name);
                Appt_Name_String.add(Appt_Name);
            }

            for(int i=0; i < Date_String_Array.size(); i++){
                HashMap<String, ArrayList<String>> hashMap = new HashMap<>();
                ArrayList<String> Appt_Name_List = new ArrayList<>();
                for(int j=0; j< jsonArray.length(); j++){
                    JSONObject item = jsonArray.getJSONObject(j);

                    String ApptName = item.getString(TAG_Appt_Name);
                    String ApptDate = item.getString(TAG_Date);

                    if(Date_String_Array.get(i).equals(ApptDate)){
                        Appt_Name_List.add(ApptName);
                    }
                }

                hashMap.put(Date_String_Array.get(i), Appt_Name_List);
                mArrayList.add(hashMap);

            }

            appt_list_view_adapter adapter = new appt_list_view_adapter(appt_list_view_activity.this, R.layout.appt_list_item, mArrayList);
            mlistView.setAdapter(adapter);

            mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Intent intent = new Intent(appt_list_view_activity.this, appt_detail_date_activity.class);
                    intent.putExtra("Appt_Info", mJsonString);
                    intent.putExtra("Date", Date_String_Array.get(i));
                    startActivity(intent);
                }
            });
        }
        catch(JSONException e){
            Log.d(TAG, "showResult : ", e);
        }
    }

    //ArrayList 오름차순으로 정렬
    class Ascending implements Comparator<String> {
        @Override
        public int compare(String s, String t1) {
            return s.compareTo(t1);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent backIntent = new Intent(getApplicationContext(), MainActivity.class);
        backIntent.putExtra("userID", userID);
        startActivity(backIntent);
    }

}