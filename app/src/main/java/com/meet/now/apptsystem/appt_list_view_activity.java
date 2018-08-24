package com.meet.now.apptsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

// 약속목록 리스트보기 방식
public class appt_list_view_activity extends AppCompatActivity{

    public static AppCompatActivity apptListActivity;

    private static String TAG = "phptest_MainActivity";

    private static final String TAG_JSON = "respons";
    private static final String TAG_Appt_Name = "ApptName";
    private static final String TAG_Date = "Date";

    ArrayList<HashMap<String, ArrayList<String>>> mArrayList;
    ListView mlistView;
    String mJsonString;
    LinearLayout appt_name_linearlayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appt_list_view);
        // 리스트뷰 객체 생성
        apptListActivity = appt_list_view_activity.this;

        // 캘린더뷰 종료
        appt_calendar_view_activity apptCalenterActivity = (appt_calendar_view_activity)appt_calendar_view_activity.apptCalenderActivity;
        if(apptCalenterActivity != null) apptCalenterActivity.finish();

        // 뒤로가기
        ImageButton backButton = (ImageButton) findViewById(R.id.btn_back_list);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // 리스트 뷰
        mlistView = findViewById(R.id.appt_listView);
        mArrayList = new ArrayList<>();
        appt_name_linearlayout = findViewById(R.id.today_appt);

        // 약속 목록 데이터 받아오기
        GetData task = new GetData();
        task.execute("http://brad903.cafe24.com/AppointmentDetails_Data_Get.php");

        // 캘린더보기로 바꾸기
        ImageButton imageButton = findViewById(R.id.calendar_change_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), appt_calendar_view_activity.class);
                startActivity(intent);
            }
        });
    }

    // getData
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

                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == httpURLConnection.HTTP_OK){
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();

            }
            catch (IOException e){
                Log.d(TAG, "InsertData:Error ", e);
                errorString = e.toString();

                return null;
            }
        }

    }

    // 결과출력
    private void showResult(){
        try{
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            ArrayList<String> Date_String = new ArrayList<>();
            ArrayList<String> Appt_Name_String = new ArrayList<>();

            //Date 리스트 생성
            for(int i=0; i< jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String Date = item.getString(TAG_Date);
                Date_String.add(Date);
            }

            //중복 item제거
            HashSet<String> set = new HashSet<>(Date_String);
            ArrayList<String> Date_String_Array = new ArrayList<>(set);

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
                    Log.d("민용 텍스트", ApptName+" | "+ApptDate);


                    if(Date_String_Array.get(i).equals(ApptDate)){
                        Appt_Name_List.add(ApptName);
                    }
                }

                // 해쉬맵에 아이템 추가
                hashMap.put(Date_String_Array.get(i), Appt_Name_List);

                // 리스트에 해쉬맵 추가
                mArrayList.add(hashMap);

            }

            appt_list_view_adapter adapter = new appt_list_view_adapter(appt_list_view_activity.this,
                    R.layout.appt_list_item, mArrayList);

            // 어댑터 추가
            mlistView.setAdapter(adapter);

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
}