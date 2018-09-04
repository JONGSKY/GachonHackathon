package com.meet.now.apptsystem;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class appt_detail_date_adapter extends BaseAdapter {
    private LayoutInflater inflater;
    private JSONArray jsonArray;
    private int layout;

    private String ApptNo = "hello";

    public appt_detail_date_adapter(Context context, int layout, JSONArray jsonArray) {
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.jsonArray = jsonArray;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return this.jsonArray.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return this.jsonArray.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = inflater.inflate(layout, viewGroup, false);
        }

        JSONObject jsonObject = new JSONObject();
        String Time = "약속시간 : ";
        String RelationGroup = "모임 유형 : ";
        String MemberNo = "멤버 수 : ";
        String ApptPlace = "약속 장소 : ";

        String ApptnameValue = null;
        String ApptplaceValue = null;

        try {
            jsonObject = jsonArray.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            ApptNo = jsonObject.getString("ApptNo");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.w("minyong", ApptNo);

        try {
            ApptnameValue = jsonObject.getString("ApptName");
            ApptplaceValue = jsonObject.getString("ApptPlace");
            if(ApptplaceValue.equals("null")){
                ApptplaceValue = "미정";
            }

            Time += jsonObject.getString("ApptTime");
            RelationGroup += jsonObject.getString("RelationGroup");
            ApptPlace += ApptplaceValue;
            MemberNo += jsonObject.getString("MemberNo");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView textView1 = (TextView)view.findViewById(R.id.appt_time_textview);
        TextView textView2 = (TextView)view.findViewById(R.id.appt_group_textview);
        TextView textView3 = (TextView)view.findViewById(R.id.appt_member_number_textview);
        TextView textView4 = (TextView)view.findViewById(R.id.appt_place_textview);
        TextView textView5 = (TextView)view.findViewById(R.id.appt_name_textview);

        textView1.setText(Time);
        textView2.setText(RelationGroup);
        textView3.setText(MemberNo);
        textView4.setText(ApptPlace);
        textView5.setText(ApptnameValue);

        ImageButton imageButton = view.findViewById(R.id.appt_delete_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApptDelete apptDelete = new ApptDelete();
                Log.w("Test", ApptNo);
                apptDelete.execute(ApptNo);
                notifyDataSetChanged();
            }
        });

        return view;
    }

    class ApptDelete extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            HttpURLConnection httpURLConnection = null;

            String ApptNo = strings[0];
            String URL = "https://brad903.cafe24.com/ApptDelete.php";

            try {
                String data = URLEncoder.encode("ApptNo", "UTF-8") + "=" + URLEncoder.encode(ApptNo, "UTF-8");

                java.net.URL url = new URL(URL);

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader
                        (httpURLConnection.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);//

                }

                httpURLConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
