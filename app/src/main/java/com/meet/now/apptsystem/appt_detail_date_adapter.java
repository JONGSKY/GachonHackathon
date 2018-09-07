package com.meet.now.apptsystem;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
    private JSONObject jsonObject;
    private String ApptNo = "hello";
    private Context context;

    public appt_detail_date_adapter(Context context, int layout, JSONArray jsonArray) {
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.jsonArray = jsonArray;
        this.layout = layout;
        this.context = context;
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

        jsonObject = new JSONObject();
        String Time = "약속시간 : ";
        String RelationGroup = "모임 유형 : ";
        String MemberNo = "멤버 수 : ";

        String ApptnameValue = null;
        String ApptplaceValue = null;

        try {
            jsonObject = jsonArray.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            ApptnameValue = jsonObject.getString("ApptName");
            ApptplaceValue = jsonObject.getString("ApptPlace");
            if(ApptplaceValue.equals("null")){
                ApptplaceValue = "미정";
            }

            Time += jsonObject.getString("ApptTime");
            RelationGroup += jsonObject.getString("RelationGroup");
            MemberNo += jsonObject.getString("MemberNo");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView textView1 = (TextView)view.findViewById(R.id.appt_time_textview);
        TextView textView2 = (TextView)view.findViewById(R.id.appt_group_textview);
        TextView textView3 = (TextView)view.findViewById(R.id.appt_member_number_textview);
        TextView textView4 = (TextView)view.findViewById(R.id.appt_place_value);
        TextView textView5 = (TextView)view.findViewById(R.id.appt_name_textview);

        textView1.setText(Time);
        textView2.setText(RelationGroup);
        textView3.setText(MemberNo);
        textView4.setText(ApptplaceValue);
        textView4.setSelected(true);
        textView5.setText(ApptnameValue);


        ImageButton imageButton = view.findViewById(R.id.appt_delete_button);
        imageButton.setFocusable(false);
        imageButton.setTag(i);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                try {
                    jsonObject = jsonArray.getJSONObject((Integer) v.getTag());
                    ApptNo = jsonObject.getString("ApptNo");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                DialogApptDelete dialogApptDelete = new DialogApptDelete(context, ApptNo);
                dialogApptDelete.show();
            }
        });

        return view;
    }

}