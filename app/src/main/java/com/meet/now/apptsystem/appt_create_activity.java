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
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.security.AccessController.getContext;

public class appt_create_activity extends AppCompatActivity {

    private EditText appt_name;
    private CalendarView appt_date;
    private Spinner appt_age;
    private TimePicker appt_time;
    private Spinner appt_meeting_type;
    private String Name;
    private String Date;
    private String Age;
    private String Time;
    private String Meeting;
    private String Login_User_ID;
    private String Appt_No;
    private String mJsonString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appt_create);

        ImageButton backButton = (ImageButton) findViewById(R.id.btn_back_create);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        appt_name = findViewById(R.id.appt_name_edit);
        appt_date = findViewById(R.id.calendarView);
        appt_age = findViewById(R.id.age_spinner);
        appt_time = findViewById(R.id.appt_time_spinner);
        appt_meeting_type = findViewById(R.id.appt_meeting_type_spinner);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat CurYearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat CurMonthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat CurDayFormat = new SimpleDateFormat("dd");
        Date = CurYearFormat.format(date) + "-" + CurMonthFormat.format(date) + "-" + CurDayFormat.format(date);

        appt_date.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                Date = String.valueOf(year) + "-" + String.valueOf(month+1) + "-" + String.valueOf(day);
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


        Button button = (Button)findViewById(R.id.appt_create_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppointmentDetail_put_Prepare();
                Appointment_put_Prepare();
                Intent intent = new Intent(getApplicationContext(), appt_list_view_activity.class);
                startActivity(intent);
            }
        });
    }

    public void Appt_Name_Set_String(EditText editText){
        Name = editText.getText().toString();
    }

    public void Appt_Age_Set_String(Spinner spinner){
        Age = spinner.getSelectedItem().toString();
    }

    public void Appt_Meeting_Type_Set_String(Spinner spinner){
        Meeting = spinner.getSelectedItem().toString();
    }

    public void AppointmentDetail_put_Prepare() {
        AppointmentDetail_put async_test = new AppointmentDetail_put();
        Appt_Name_Set_String(appt_name);
        Appt_Age_Set_String(appt_age);
        Appt_Meeting_Type_Set_String(appt_meeting_type);
        async_test.execute(Name, Date, Age, Time, Meeting);
    }

    public void Appointment_put_Prepare(){
        Appointment_put async_test = new Appointment_put();
        async_test.execute(Login_User_ID, Appt_No);
    }

    class AppointmentDetail_put extends AsyncTask<String, Void, String> {

        int cnt = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //textView.setText("I got Msg from Server! : " + s);// TextView에 보여줍니다.
            Toast.makeText(getApplicationContext(),"i got a msg from server :"+s,Toast.LENGTH_LONG).show();
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

                String data = URLEncoder.encode("appt_name", "UTF-8") + "=" + URLEncoder.encode(appt_name, "UTF-8");// UTF-8로  설정 실제로 string 상으로 봤을땐, tmsg="String" 요런식으로 설정 된다.
                data += "&" + URLEncoder.encode("Date_String", "UTF-8") + "=" + URLEncoder.encode(Date_String, "UTF-8");
                data += "&" + URLEncoder.encode("Age_String", "UTF-8") + "=" + URLEncoder.encode(Age_String, "UTF-8");
                data += "&" + URLEncoder.encode("Time_String", "UTF-8") + "=" + URLEncoder.encode(Time_String, "UTF-8");
                data += "&" + URLEncoder.encode("Meeting_Type_String", "UTF-8") + "=" + URLEncoder.encode(Meeting_Type_String, "UTF-8");

                //String data2 = "tmsg="+testMsg+"&tmsg2="+testMsg2;

                String link = "http://brad903.cafe24.com/" + "Appt_Create.php";// 요청하는 url 설정 ex)192.168.0.1/httpOnlineTest.php

                URL url = new URL(link);

                httpURLConnection = (HttpURLConnection) url.openConnection();//httpURLConnection은 url.openconnection을 통해서 만 생성 가능 직접생성은 불가능하다.
                httpURLConnection.setRequestMethod("POST");//post방식으로 설정

                httpURLConnection.setDoInput(true);// server와의 통신에서 입력 가능한 상태로 만든다.
                httpURLConnection.setDoOutput(true);//server와의 통신에서 출력 가능한 상태로 만든다.

                OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());//서버로 뿅 쏴줄라구용
                wr.write(data);//아까 String값을 쓱삭쓱삭 넣어서 보내주고!
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

    //Appt_No 하고 Login_User_ID String 값으로 저장해야됨
    class Appointment_put extends AsyncTask<String, Void, String> {

        int cnt = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //textView.setText("I got Msg from Server! : " + s);// TextView에 보여줍니다.
            Toast.makeText(getApplicationContext(),"i got a msg from server :"+s,Toast.LENGTH_LONG).show();
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
                String login_user_id = params[0];
                String appt_no = params[1];

                String data = URLEncoder.encode("login_user_id", "UTF-8") + "=" + URLEncoder.encode(login_user_id, "UTF-8");// UTF-8로  설정 실제로 string 상으로 봤을땐, tmsg="String" 요런식으로 설정 된다.
                data += "&" + URLEncoder.encode("appt_no", "UTF-8") + "=" + URLEncoder.encode(appt_no, "UTF-8");

                //String data2 = "tmsg="+testMsg+"&tmsg2="+testMsg2;

                String link = "http://brad903.cafe24.com/" + "Appointment_Create.php";// 요청하는 url 설정 ex)192.168.0.1/httpOnlineTest.php

                URL url = new URL(link);

                httpURLConnection = (HttpURLConnection) url.openConnection();//httpURLConnection은 url.openconnection을 통해서 만 생성 가능 직접생성은 불가능하다.
                httpURLConnection.setRequestMethod("POST");//post방식으로 설정

                httpURLConnection.setDoInput(true);// server와의 통신에서 입력 가능한 상태로 만든다.
                httpURLConnection.setDoOutput(true);//server와의 통신에서 출력 가능한 상태로 만든다.

                OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());//서버로 뿅 쏴줄라구용
                wr.write(data);//아까 String값을 쓱삭쓱삭 넣어서 보내주고!
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

    //보류
    private class GetData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(appt_create_activity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            mJsonString = result;
            Log.w("result값 : ", result);

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
                errorString = e.toString();

                return null;
            }
        }

    }
}