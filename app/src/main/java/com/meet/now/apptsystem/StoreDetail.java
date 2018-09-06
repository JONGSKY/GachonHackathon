package com.meet.now.apptsystem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class StoreDetail extends AppCompatActivity {

    JSONObject storeInfo;
    List<String> infoName;
    List<Integer> drawableList;
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

        TextView hotplaceName = findViewById(R.id.hotplaceName);
        TextView storeTitle = findViewById(R.id.storeTitle);
        ImageView _imv = findViewById(R.id.store_pic);
        jsonArray = StoreListActivity.jsonArray;

        infoName = Arrays.asList("addr", "PhoneNumber", "FoodType", "Price", "Parking", "Time");
        drawableList = Arrays.asList(R.drawable.store_addr, R.drawable.store_phonenumber, R.drawable.store_foodtype, R.drawable.store_price, R.drawable.store_parking, R.drawable.store_time);

        Intent intent = getIntent();
        hotplaceName.setText(intent.getStringExtra("placeName"));

        try {
            JSONObject jsonObject = jsonArray.getJSONObject(intent.getIntExtra("position", 0));
            _imv.setImageBitmap(StoreListAdapter.getBitmapFromString(jsonObject.getString("pic")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            storeInfo = new JSONObject(intent.getStringExtra("storeInfo"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LinearLayout storeDetailLayout = findViewById(R.id.storeDetailLayout);

            try {
                storeTitle.setText(storeInfo.getString("title"));
                for(int i=0; i<infoName.size(); i++) {
                    String textContent = storeInfo.getString(infoName.get(i));
                    if (!textContent.equals("")) {
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        TextView detailTextview = new TextView(this);
                        storeDetailLayout.addView(detailTextview, layoutParams);

                        detailTextview.setText("  " + textContent);
                        detailTextview.setTextSize(16);
                        detailTextview.setTextColor(0xff000000);
                        detailTextview.setPadding(3,20,5,20);
                        detailTextview.setCompoundDrawablesWithIntrinsicBounds(drawableList.get(i), 0, 0, 0);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


    }
}
