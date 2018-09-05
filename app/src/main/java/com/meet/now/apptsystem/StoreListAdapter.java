package com.meet.now.apptsystem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class StoreListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private JSONArray jsonArray;
    private int layout;

    public StoreListAdapter(Context context, JSONArray jsonArray, int layout) {
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.jsonArray = jsonArray;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return this.jsonArray.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return this.jsonArray.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(layout, parent, false);
        }

        JSONObject jsonObject = new JSONObject();
        String StoreName = null;
        String StoreType = null;
        String StorePic = null;

        try {
            jsonObject = jsonArray.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            StoreName = jsonObject.getString("title");
            StoreType = jsonObject.getString("FoodType");
            StorePic = jsonObject.getString("pic");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView textView1 = convertView.findViewById(R.id.store_name);
        TextView textView2 = convertView.findViewById(R.id.store_type);
        ImageView imageView = convertView.findViewById(R.id.store_pic);

        textView1.setText(StoreName);
        textView2.setText(StoreType);
        if(!StorePic.equals("")){
            Bitmap img = getBitmapFromString(StorePic);
            imageView.setImageBitmap(img);
        }

        return convertView;
    }

    public static Bitmap getBitmapFromString(String stringPicture) {
        /*
         * This Function converts the String back to Bitmap
         * */
        byte[] decodedString = Base64.decode(stringPicture, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
}
