package com.meet.now.apptsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import com.meet.now.apptsystem.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.security.AccessController.getContext;

public class appt_create_activity extends AppCompatActivity {

    private EditText appt_name;
    private CalendarView appt_date;
    private Spinner appt_age;
    private TimePicker appt_time;
    private Spinner appt_meeting_type;
    private ImageButton apptAddfriend;
    private String Name;
    private String Date;
    private String Age;
    private String Time;
    private String Meeting;
    private static final String TAGApptNo = "ApptNo";
    private String mJsonString;
    private String USERID;
    private JSONArray jsonArray = new JSONArray();
    private JSONObject jsonMain = new JSONObject();

    ArrayList<String> friendList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appt_create);

        appt_name = findViewById(R.id.appt_name_edit);
        appt_date = findViewById(R.id.calendarView);
        appt_age = findViewById(R.id.age_spinner);
        appt_time = findViewById(R.id.appt_time_spinner);
        appt_meeting_type = findViewById(R.id.appt_meeting_type_spinner);
        apptAddfriend = findViewById(R.id.apptAddfriend);

        Intent intent = getIntent();
        USERID = intent.getStringExtra("UserID");

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat CurYearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat CurMonthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat CurDayFormat = new SimpleDateFormat("dd");
        Date = CurYearFormat.format(date) + "-" + CurMonthFormat.format(date) + "-" + CurDayFormat.format(date);

        appt_date.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                Date = String.valueOf(year) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(day);
            }
        });

        Time = String.valueOf(appt_time.getHour()) + ":" + String.valueOf(appt_time.getMinute());
        appt_time.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
                Time = String.valueOf(hour) + ":" + String.valueOf(minute);
            }
        });

        ArrayAdapter appt_age_adapter = ArrayAdapter.createFromResource(this, R.array.age_array, android.R.layout.simple_spinner_item);
        appt_age.setAdapter(appt_age_adapter);

        ArrayAdapter appt_meeting_type_adapter = ArrayAdapter.createFromResource(this, R.array.meeting_type_array, android.R.layout.simple_spinner_item);
        appt_meeting_type.setAdapter(appt_meeting_type_adapter);

        Button button = (Button) findViewById(R.id.appt_create_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppointmentDetailPutPrepare();
                Intent intent = new Intent(getApplicationContext(), appt_list_view_activity.class);
                startActivity(intent);
            }
        });

        apptAddfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ApptfriendIntent = new Intent(getApplicationContext(), ApptAddfriendActivity.class);
                startActivityForResult(ApptfriendIntent, 0);
            }
        });

        friendList = new ArrayList<String>();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(resultCode){
            case 1:
                String nickname = data.getStringExtra("nickname");
                String friendID = data.getStringExtra("friendID");
                String userPhoto = data.getStringExtra("userPhoto");

                boolean flag = false;
                for(int i=0; i<friendList.size(); i++){
                    if(friendList.get(i).equals(friendID)){
                        flag = true;
                        break;
                    }
                }
                if(flag) break;

                friendList.add(friendID);
                ApptFriend n_layout = new ApptFriend(getApplicationContext(), nickname, userPhoto);
                LinearLayout con = (LinearLayout)findViewById(R.id.con);
                con.addView(n_layout);

                try {
                    JSONObject TempFriendID = new JSONObject();
                    TempFriendID.put("FriendID", data.getStringExtra("friendID"));
                    jsonArray.put(TempFriendID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            default:
        }

    }

    public void Appt_Name_Set_String(EditText editText) {
        Name = editText.getText().toString();
    }

    public void Appt_Age_Set_String(Spinner spinner) {
        Age = spinner.getSelectedItem().toString();
    }

    public void Appt_Meeting_Type_Set_String(Spinner spinner) {
        Meeting = spinner.getSelectedItem().toString();
    }

    public void AppointmentDetailPutPrepare() {
        AppointmentDetailPut async_test = new AppointmentDetailPut();
        Appt_Name_Set_String(appt_name);
        Appt_Age_Set_String(appt_age);
        Appt_Meeting_Type_Set_String(appt_meeting_type);

        async_test.execute(Name, Date, Age, Time, Meeting, USERID);
    }

    class AppointmentDetailPut extends AsyncTask<String, Void, String> {

        int cnt = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //textView.setText("I got Msg from Server! : " + s);// TextView에 보여줍니다.
            Toast.makeText(getApplicationContext(), "i got a msg from server :" + s, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            Log.d("onProgress update", "" + cnt++);
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection httpURLConnection = null;

            try {
                String appt_name = params[0];
                String Date_String = params[1];
                String Age_String = params[2];
                String Time_String = params[3];
                String Meeting_Type_String = params[4];
                String USERID = params[5];

                jsonMain.put("FriendList", jsonArray);
                Log.w("minyong", String.valueOf(jsonMain));

                String data = URLEncoder.encode("appt_name", "UTF-8") + "=" + URLEncoder.encode(appt_name, "UTF-8");// UTF-8로  설정 실제로 string 상으로 봤을땐, tmsg="String" 요런식으로 설정 된다.
                data += "&" + URLEncoder.encode("Date_String", "UTF-8") + "=" + URLEncoder.encode(Date_String, "UTF-8");
                data += "&" + URLEncoder.encode("Age_String", "UTF-8") + "=" + URLEncoder.encode(Age_String, "UTF-8");
                data += "&" + URLEncoder.encode("Time_String", "UTF-8") + "=" + URLEncoder.encode(Time_String, "UTF-8");
                data += "&" + URLEncoder.encode("Meeting_Type_String", "UTF-8") + "=" + URLEncoder.encode(Meeting_Type_String, "UTF-8");
                data += "&" + URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(USERID, "UTF-8");

                //String data2 = "tmsg="+testMsg+"&tmsg2="+testMsg2;

                String link = "http://brad903.cafe24.com/AppointmentDetailsCreate.php";// 요청하는 url 설정 ex)192.168.0.1/httpOnlineTest.php

                URL url = new URL(link);

                httpURLConnection = (HttpURLConnection) url.openConnection();//httpURLConnection은 url.openconnection을 통해서 만 생성 가능 직접생성은 불가능하다.
                httpURLConnection.setRequestMethod("POST");//post방식으로 설정

                httpURLConnection.setDoInput(true);// server와의 통신에서 입력 가능한 상태로 만든다.
                httpURLConnection.setDoOutput(true);//server와의 통신에서 출력 가능한 상태로 만든다.

                OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());//서버로 뿅 쏴줄라구용
                wr.write(data);//아까 String값을 쓱삭쓱삭 넣어서 보내주고!
                wr.write(jsonMain.toString());
                Log.w("asd", String.valueOf(jsonMain));
                Log.w("userID", USERID);
                wr.flush();//flush!

                BufferedReader reader = new BufferedReader(new InputStreamReader
                        (httpURLConnection.getInputStream(), "UTF-8"));//자 이제 받아옵시다.
                StringBuilder sb = new StringBuilder();// String 값을 이제 스슥스슥 넣을 껍니다.
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);//

                }

                httpURLConnection.disconnect();//이거 꼭해주세요. 보통은 별일 없는데, 특정상황에서 문제가 생기는 경우가 있다고 합니다.
                return sb.toString();//자 이렇게 리턴이되면 이제 post로 가겠습니다.
            } catch (Exception e) {

                httpURLConnection.disconnect();
                return new String("Exception Occure" + e.getMessage());
            }//try catch end
        }//doInbackground end
    }//asynctask  end

}
