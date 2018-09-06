package com.meet.now.apptsystem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class appt_list_view_adapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<HashMap<String, ArrayList<String>>> arrayList;
    private int layout;
    private Context context;

    appt_list_view_adapter(Context context, int layout, ArrayList<HashMap<String, ArrayList<String>>> arrayList) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.arrayList = arrayList;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return this.arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = inflater.inflate(layout, viewGroup, false);
        }

        Log.w("i값", String.valueOf(i));

        HashMap<String, ArrayList<String>> hashMap = arrayList.get(i);

        Set key = hashMap.keySet();
        Iterator iterator = key.iterator();
        String Date = (String) iterator.next();
        String[] Date_Array = Date.split("-");

        //날짜 설정
        TextView Day_TextView = view.findViewById(R.id.Day_textView);
        Day_TextView.setText(Date_Array[2]);

        //요일 설정
        String week;
        long diffDays = 0;
        try {
            week = week_result(Date_Array[0] + Date_Array[1] + Date_Array[2]);

            TextView Week_TextView = view.findViewById(R.id.Week_textView);
            Week_TextView.setText(week);

            Date now = new Date();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date certainDay = sdf.parse(Date);
            Date today = sdf.parse(sdf.format(now));

            long diff = certainDay.getTime() - today.getTime();
            diffDays = diff / (24 * 60 * 60 * 1000);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(diffDays >= 0 && diffDays <= 4){
            final ImageView apptlistWeather = view.findViewById(R.id.apptlistWeather);
            Log.w("diffDays : ", String.valueOf(diffDays) + " " + String.valueOf(Date));
            GetWeatherInfo getWeatherInfo = new GetWeatherInfo(new AsyncWeather() {
                @Override
                public void processFinish(Bitmap output) {
                    Log.w("output : ", String.valueOf(output));
                    apptlistWeather.setImageBitmap(output);
                }
            });
            getWeatherInfo.execute(Date);
        }

        LinearLayout linearLayout = view.findViewById(R.id.today_appt);

        if (linearLayout.getChildCount() == 0) {
            for (int j = 0; j < hashMap.get(Date).size(); j++) {
                String Appt_Name = hashMap.get(Date).get(j);

                TextView textView = new TextView(context);
                textView.setText(" - "+Appt_Name);
                textView.setTextSize(16);
                textView.setTextColor(Color.BLACK);
                textView.setPadding(4,4,4,4);

                linearLayout.addView(textView);
            }

        }

        return view;
    }

    private String week_result(String date) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

        Date date1 = formatter.parse(date);  // 날짜 입력하는곳 .

        date1 = new Date(date1.getTime() + (1000 * 60 * 60 * 24)-1);  // 날짜에 하루를 더한 값

        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);              // 하루더한 날자 값을 Calendar  넣는다.

        int dayNum = cal.get(Calendar.DAY_OF_WEEK);   // 요일을 구해온다.

        String convertedString = "";

        switch (dayNum) {
            case 1:
                convertedString = "Sun";
                break;
            case 2:
                convertedString = "Mon";
                break;
            case 3:
                convertedString = "Tue";
                break;
            case 4:
                convertedString = "Wed";
                break;
            case 5:
                convertedString = "Thu";
                break;
            case 6:
                convertedString = "Fri";
                break;
            case 7:
                convertedString = "Sat";
                break;
        }

        return convertedString;
    }
}