package com.meet.now.apptsystem;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class appt_detail_date_adapter extends BaseAdapter {
    private LayoutInflater inflater;
    private JSONArray jsonArray;
    private int layout;
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

        JSONObject jsonObject = new JSONObject();
        String Time = "약속시간 : ";
        String RelationGroup = "모임 유형 : ";
        //String MemberNumber = "멤버 수 : ";
        String ApptPlace = "약속 장소 : ";

        try {
            jsonObject = jsonArray.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            Time += jsonObject.getString("ApptTime");
            RelationGroup += jsonObject.getString("RelationGroup");
            ApptPlace += jsonObject.getString("ApptPlace");
            //멤버 수에 대한 처리 해야함
           // MemberNumber += jsonObject.getString("MemberNumber");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView textView1 = (TextView)view.findViewById(R.id.appt_time_textview);
        TextView textView2 = (TextView)view.findViewById(R.id.appt_group_textview);
        //TextView textView3 = (TextView)view.findViewById(R.id.appt_member_number_textview);
        TextView textView4 = (TextView)view.findViewById(R.id.appt_place_textview);

        textView1.setText(Time);
        textView2.setText(RelationGroup);
        //textView3.setText(MemberNumber);
        textView4.setText(ApptPlace);

        return view;
    }
}
