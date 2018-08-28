package com.meet.now.apptsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

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
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class appt_calendar_view_activity extends AppCompatActivity{

    private static String TAG = "phptest_MainActivity";

    private static final String TAG_JSON = "response";
    private static final String TAG_Appt_Name = "ApptName";
    private static final String TAG_Date = "Date";

    MaterialCalendarView calendarView;


    ArrayList<HashMap<String, ArrayList<String>>> mArrayList;
    String mJsonString;
    private String userID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appt_calendar_view);

//        ImageButton backButton = (ImageButton) findViewById(R.id.list_change_button);
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        calendarView = findViewById(R.id.calendarView);

        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2000, 0, 1))
                .setMaximumDate(CalendarDay.from(2100, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        calendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                new oneDayDecorator());

        GetData task = new GetData();
        task.execute("http://brad903.cafe24.com/ApptListView.php");

        ImageButton imageButton = findViewById(R.id.list_change_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), appt_list_view_activity.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
                finish();
            }
        });
    }

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(appt_calendar_view_activity.this,
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


            //점 표시하는건 나중에
            ArrayList<CalendarDay> ApptDay = new ArrayList<>();
            for(int j = 0; j < Date_String_Array.size(); j++){
                String[] Date = Date_String_Array.get(j).split("-");
                int Year = Integer.parseInt(Date[0]);
                int Month = Integer.parseInt(Date[1]);
                int Day = Integer.parseInt(Date[2]);

                CalendarDay day = CalendarDay.from(Year, Month-1, Day);

                ApptDay.add(day);
            } calendarView.addDecorator(new EventDecorator(Color.RED, ApptDay));

            calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
                @Override
                public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                    Intent intent = new Intent(appt_calendar_view_activity.this, appt_detail_date_activity.class);
                    intent.putExtra("Appt_Info", mJsonString);

                    String[] Date = String.valueOf(date).split("\\{");
                    //중괄호 제거한 값 저장
                    String Date1 = Date[1].substring(0, Date[1].length()-1);
                    //-로 스플릿
                    String[] Date2 = Date1.split("-");
                    //달의 값 1 증가
                    int Month = Integer.parseInt(Date2[1]);
                    Month++;
                    Date2[1] = String.valueOf(Month);
                    if(Date2[1].length()<2){
                        Date2[1] = "0"+Date2[1];
                    }

                    //최종
                    String Date3 = Date2[0]+"-"+Date2[1]+"-"+Date2[2];

                    intent.putExtra("Date", Date3);
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

    //일요일 색깔 표시
    public class SundayDecorator implements DayViewDecorator {

        private final Calendar calendar = Calendar.getInstance();

        public SundayDecorator() {
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            day.copyTo(calendar);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            return weekDay == Calendar.SUNDAY;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.RED));
        }
    }

    //토요일 색깔 표시
    public class SaturdayDecorator implements DayViewDecorator {

        private final Calendar calendar = Calendar.getInstance();

        public SaturdayDecorator() {
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            day.copyTo(calendar);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            return weekDay == Calendar.SATURDAY;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.BLUE));
        }
    }

    //오늘 날짜 표시
    public class oneDayDecorator implements DayViewDecorator {

        private CalendarDay date;

        public oneDayDecorator() {
            date = CalendarDay.today();
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return date != null && day.equals(date);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new StyleSpan(Typeface.BOLD));
            view.addSpan(new RelativeSizeSpan(1.4f));
            view.addSpan(new ForegroundColorSpan(Color.GREEN));
        }

        /**
         * We're changing the internals, so make sure to call {@linkplain MaterialCalendarView#invalidateDecorators()}
         */
        public void setDate(Date date) {
            this.date = CalendarDay.from(date);
        }
    }

    public class EventDecorator implements DayViewDecorator {

        private int color;
        private HashSet<CalendarDay> dates;

        public EventDecorator(int color, Collection<CalendarDay> dates) {
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(5, color)); // 날자밑에 점
        }
    }

}
