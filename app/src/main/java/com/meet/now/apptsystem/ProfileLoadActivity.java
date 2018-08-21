package com.meet.now.apptsystem;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import okhttp3.internal.Util;

import static android.support.v4.media.MediaBrowserServiceCompat.RESULT_OK;
import static com.meet.now.apptsystem.UpdateProfilePhoto.REQUEST_PHOTO_ALBUM;
import static com.meet.now.apptsystem.UpdateProfilePhoto.REQUEST_PICTURE;
import static com.meet.now.apptsystem.UpdateProfilePhoto.file;

public class ProfileLoadActivity extends AppCompatActivity {

    String userNickname = null;
    String userStatusmsg = null;
    String userPhoto = null;
    String userAddress = null;
    String userID = null;

    ImageView iv;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_load);

        iv = (ImageView)findViewById(R.id.iv_user);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        // DB 프로필 정보 로드
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {

                        userNickname = jsonResponse.getString("userNickname");
                        userStatusmsg = jsonResponse.getString("userStatusmsg");
                        userPhoto = jsonResponse.getString("userPhoto");
                        userAddress = jsonResponse.getString("userAddress");

                        TextView nicknameText = (TextView) findViewById(R.id.tv_user);
                        TextView statusmsgText = (TextView) findViewById(R.id.tv_introduce);
                        ImageView photoView = findViewById(R.id.iv_user);

                        nicknameText.setText(userNickname);
                        if (!userStatusmsg.equals("null")) statusmsgText.setText(userStatusmsg);

                        // 이미지 경로 찾아가 이미지 가져오기
                        // 프로필 사진 등록하기

                        ImageView ivImage = findViewById(R.id.iv_user);
                        // 프로필 라운딩
                        //ivImage.setBackground(new ShapeDrawable(new OvalShape()));
                        //ivImage.setClipToOutline(true);


                    } else {
                        // 값 가져오기 실패
                        Toast.makeText(getApplicationContext(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        ProfileLoadRequest profileLoadRequest = new ProfileLoadRequest(userID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ProfileLoadActivity.this);
        queue.add(profileLoadRequest);
        //////////////////////////////////////////////////////////////////////////////////

        // 프로필 이미지 변경
        ImageButton ibEditImg = (ImageButton) findViewById(R.id.ib_edit_userImg);
        ibEditImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = getSupportFragmentManager();
                UpdateProfilePhoto dialogFragment = new UpdateProfilePhoto();

                Bundle bundle = new Bundle();
                bundle.putString("userID", userID);
                dialogFragment.setArguments(bundle);  // userID전달, 받을 땐 getArguments이용

                dialogFragment.show(fm, "fragment_dialog_photo");


            }
        });
        ////////////////////////////////////////////////////////////////////////////////////

        // 뒤로가기 버튼
        ImageButton backBtn = (ImageButton) findViewById(R.id.ib_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ///////////////////////////////////////////////////////////////////////////////////

        // 닉네임 수정
        ImageButton ibNickname = (ImageButton) findViewById(R.id.ib_edit_nickname);
        ibNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                UpdateProfileNickname dialogFragment = new UpdateProfileNickname();

                Bundle bundle = new Bundle();
                bundle.putString("userID", userID);
                bundle.putString("userNickname", userNickname);
                dialogFragment.setArguments(bundle);

                dialogFragment.show(fm, "fragment_dialog_nickname");
            }
        });
        // 상태메시지 수정
        ImageButton ibEditStatus = (ImageButton) findViewById(R.id.ib_edit_status);
        ibEditStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = getSupportFragmentManager();
                UpdateProfileStatus dialogFragment = new UpdateProfileStatus();

                Bundle bundle = new Bundle();
                bundle.putString("userID", userID);
                bundle.putString("userStatusmsg", userStatusmsg);
                dialogFragment.setArguments(bundle);

                dialogFragment.show(fm, "fragment_dialog_status");
            }

        });
        ///////////////////////////////////////////////////////////////////////////////

        ImageButton mapBtn = (ImageButton) findViewById(R.id.ib_map);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoc();
            }
        });
    }

    // 위치 보여주기
    void showLoc() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_profile_loc, null);
        builder.setView(view);

        TextView tvLoc = (TextView) view.findViewById(R.id.tv_loc);
        ImageButton ibLoc = (ImageButton) view.findViewById(R.id.ib_loc);
        ImageButton ibBack = (ImageButton) view.findViewById(R.id.ib_back_loc);

        if (!userAddress.equals(null)) tvLoc.setText(userAddress);

        final AlertDialog dialog = builder.create();

        ibBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    Bitmap loadPicture() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.w("resultCode", String.valueOf(resultCode));
        Log.w("requestCode", String.valueOf(requestCode));
        // 이미지 바로띄우기는 가능.
        // 서버에 이미지를 업로드 -> db에 경로 뿌리기 -> dismiss()
//         activity에서 db경로 가져오기 -> 서버의 이미지 가져오기 -> 이미지 뿌리기
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICTURE) {
                // 이미지 바로 띄우기 -> 서버로 업로드하기로 변경
                Async_ftp_Prepare(file); // 파일 서버에 저장.
                iv.setImageBitmap(loadPicture());
            }

            if (requestCode == REQUEST_PHOTO_ALBUM) {
                Log.w("FILE", "호출");
                Async_ftp_Prepare(new File(data.getData().getPath()));
                iv.setImageURI(data.getData());

            }

        }
    }


    // 파일을 서버에 저장
    public void Async_ftp_Prepare(File file) {
        Async_ftp async_ftp = new Async_ftp();
        async_ftp.execute();
    }

    /*********  work only for Dedicated IP ***********/
    static final String FTP_HOST = "brad903.cafe24.com";
    /*********  FTP USERNAME ***********/
    static final String FTP_USER = "brad903";
    /*********  FTP PASSWORD ***********/
    static final String FTP_PASS = "Nvo78/fd4h";
    static final String FTP_PATH = "../userphoto/";

    // ftp 서버 연결 asyncTask
    class Async_ftp extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            /********** Pick file from memory *******/
            //장치로부터 메모리 주소를 얻어낸 뒤, 파일명을 가지고 찾는다.
            //현재 이것은 내장메모리 루트폴더에 있는 것.

            // Upload file
            FTPClient client = new FTPClient();

            try {
                // 연결
                client.connect(FTP_HOST, 21);//ftp 서버와 연결, 호스트와 포트를 기입
                client.login(FTP_USER, FTP_PASS);//로그인을 위해 아이디와 패스워드 기입
                client.setType(FTPClient.TYPE_BINARY);//2진으로 변경
                client.changeDirectory(FTP_PATH);//서버에서 넣고 싶은 파일 경로를 기입

                client.upload(file);//업로드 시작

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                    }
                });

                e.printStackTrace();

            }finally {
                try {
                    client.disconnect(true); // 연결중지
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }

            return null;
        }

    }



    @Override
    protected void onResume() {
        super.onResume();

    }
}
