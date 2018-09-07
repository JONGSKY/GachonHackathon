package com.meet.now.apptsystem;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

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
    private Context context;
    String apptNo;
    String StoreAddr;
    String StoreName;

    public StoreListAdapter(Context context, JSONArray jsonArray, int layout, String apptNo) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.jsonArray = jsonArray;
        this.layout = layout;
        this.context = context;
        this.apptNo = apptNo;
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
        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }

        JSONObject jsonObject = new JSONObject();
        StoreName = null;
        String StoreType = null;
        String StorePic = null;
        StoreAddr = null;

        try {
            jsonObject = jsonArray.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            StoreName = jsonObject.getString("title");
            StoreType = jsonObject.getString("FoodType");
            StorePic = jsonObject.getString("pic");
            StoreAddr = jsonObject.getString("addr");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView textView1 = convertView.findViewById(R.id.store_name);
        TextView textView2 = convertView.findViewById(R.id.store_type);
        ImageView imageView = convertView.findViewById(R.id.store_pic);

        textView1.setText(StoreName);
        textView2.setText(StoreType);
        if (!StorePic.equals("")) {
            Bitmap img = getBitmapFromString(StorePic);
            imageView.setImageBitmap(img);
        }

        // 약속장소 확정
        ImageButton set_apptplace = convertView.findViewById(R.id.set_apptplace);
        set_apptplace.setFocusable(false);
        set_apptplace.setTag(position);
        set_apptplace.setOnClickListener(new View.OnClickListener() {
            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(context);
            String[] StoreAddrArray = StoreAddr.split(" ");
            String apptPlaceName = StoreName;

            @Override
            public void onClick(View v) {
                alert_confirm.setMessage("약속 장소로 정하시겠습니까?");
                alert_confirm.setPositiveButton("확인", new DialogInterface.OnClickListener() {

                    String apptPlace = StoreAddrArray[0] + " " + StoreAddrArray[1] + ", " + apptPlaceName;

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                JSONObject jsonResponse = null;
                                try {
                                    jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");
                                    if (success) {
                                        Toast.makeText(context, "약속장소가 등록되었습니다", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "약속장소가 등록을 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        SetApptPlaceRequest apptPlaceRequest = new SetApptPlaceRequest(apptNo, apptPlace, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(context);
                        queue.add(apptPlaceRequest);
                    }
                });
                alert_confirm.setNegativeButton("취소", null);
                AlertDialog alert = alert_confirm.create();
                alert.show();
            }
        });

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
