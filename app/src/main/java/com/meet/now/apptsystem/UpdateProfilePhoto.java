package com.meet.now.apptsystem;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

import static com.meet.now.apptsystem.ProfileLoadActivity.file;


public class UpdateProfilePhoto extends DialogFragment implements View.OnClickListener {

    //사진으로 전송시 되돌려 받을 번호
    static int REQUEST_PICTURE = 1000;
    //앨범으로 전송시 돌려받을 번호
    static int REQUEST_PHOTO_ALBUM = 2000;

    public UpdateProfilePhoto() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.dialog_profile_photo_edit, null);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button camera = (Button) getView().findViewById(R.id.camera_btn);
        camera.setOnClickListener(this);
        Button photoAlbum = (Button) getView().findViewById(R.id.photoAlbum_btn);
        photoAlbum.setOnClickListener(this);
        Button back = (Button) getView().findViewById(R.id.btn_back_photo);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.camera_btn) {
            takePicture();
            dismiss();
        } else if (v.getId() == R.id.photoAlbum_btn) {
            photoAlbum();
            dismiss();
        } else if (v.getId() == R.id.btn_back_photo) {
            dismiss();
        }
    }

    void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String sampleImg = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".JPEG"; // 파일이름.
        file = new File(Environment.getExternalStorageDirectory(), sampleImg); // sdcard에 새로운 파일 생성. 경로, 이름
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        getActivity().startActivityForResult(intent, REQUEST_PICTURE);

    }

    void photoAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getActivity().startActivityForResult(intent, REQUEST_PHOTO_ALBUM);
    }

}