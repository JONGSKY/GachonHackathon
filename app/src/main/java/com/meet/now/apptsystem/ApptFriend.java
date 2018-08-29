package com.meet.now.apptsystem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ApptFriend extends LinearLayout{

    public ApptFriend(Context context, String nickname, String userPhoto) {
        super(context);
        init(context, nickname, userPhoto);
    }
    private void init(Context context, String nickname, String userPhoto){
        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.apptfriend,this,true);
        TextView apptFriendID = view.findViewById(R.id.apptFriendID);
        ImageView apptUserPhoto = view.findViewById(R.id.apptUserPhoto);
        apptFriendID.setText(nickname);

        if(userPhoto.equals("null")){
            apptUserPhoto.setImageResource(R.drawable.ic_profile_picture);
            apptUserPhoto.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
        }else {
            ////////////////// 프로필 로드 이슈 해결 필요 //////////////////
            ProfileLoadActivity profileLoadActivity = new ProfileLoadActivity();
           /* Bitmap bitmap = profileLoadActivity.bitmapImgDownload(userPhoto);
            apptUserPhoto.setImageBitmap(bitmap);
            apptUserPhoto.setBackground(new ShapeDrawable(new OvalShape()));
            apptUserPhoto.setClipToOutline(true);*/
        }


    }
}
