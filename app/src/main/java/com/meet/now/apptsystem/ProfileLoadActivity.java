package com.meet.now.apptsystem;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.ExifInterface;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

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


    private UpdateProfilePhoto updateProfilePhoto;

    ImageView iv;

    Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_load);

        iv = (ImageView) findViewById(R.id.iv_user);

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
                        // 파일 이름과 파일 필요.
                        Async_db_Prepare("false");

                        ImageView iv = findViewById(R.id.iv_user);
                        iv.setImageBitmap(loadPictureWithResize(200));
                        iv.setBackground(new ShapeDrawable(new OvalShape())); // 프로필 라운딩
                        iv.setClipToOutline(true);
                        file.delete();
                        /*ExifInterface exif = null;
                        try {
                            Log.d("이미지", file.getAbsolutePath());
                            exif = new ExifInterface(file.getPath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        int exifDegree = exifOrientationToDegrees(exifOrientation);
                        Bitmap bitmap = loadPictureWithResize(200);
                        iv.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
                        iv.setBackground(new ShapeDrawable(new OvalShape())); // 프로필 라운딩
                        iv.setClipToOutline(true);*/


                    } else {
                        // 값 가져오기 실패
                        Toast.makeText(getApplicationContext(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();

                        file.delete();
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
                updateProfilePhoto = new UpdateProfilePhoto();

                Bundle bundle = new Bundle();
                bundle.putString("userID", userID);
                updateProfilePhoto.setArguments(bundle);
                updateProfilePhoto.show(fm, "fragment_dialog_photo");
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

    // 리사이징
    private Bitmap loadPictureWithResize(int resize) {
        Bitmap resizeBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

        BitmapFactory.decodeFile(file.getAbsolutePath(), options); // 1번

        int width = options.outWidth;
        int height = options.outHeight;
        int samplesize = 1;

        while (true) {//2번
            if (width / 2 < resize || height / 2 < resize)
                break;
            width /= 2;
            height /= 2;
            samplesize *= 2;
        }

        options.inSampleSize = samplesize;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options); //3번
        resizeBitmap = bitmap;

        return resizeBitmap;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.w("resultCode", String.valueOf(resultCode));
        Log.w("requestCode", String.valueOf(requestCode));

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICTURE) {
                // 카메라로 가져오기
                Async_ftp_Prepare(file, "false"); // 파일 서버에 저장.
                userPhoto = file.getName();
                Async_db_Prepare("true"); // 파일경로 db에 저장.
                iv.setImageBitmap(loadPictureWithResize(200));
                iv.setBackground(new ShapeDrawable(new OvalShape())); // 프로필 라운딩
                iv.setClipToOutline(true);
                file.delete();
            }


            if (requestCode == REQUEST_PHOTO_ALBUM) {
                // 앨범에서 가져오기
                String imagePath = getRealPathFromURI(data.getData());
                file = new File(imagePath);
                userPhoto = file.getName();
                Async_ftp_Prepare(file, "true"); // 파일 서버에 저장.
                Async_db_Prepare("true");

                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(imagePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int exifDegree = exifOrientationToDegrees(exifOrientation);
                Bitmap bitmap = loadPictureWithResize(200);
                iv.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
                iv.setBackground(new ShapeDrawable(new OvalShape())); // 프로필 라운딩
                iv.setClipToOutline(true);
                file.delete();
            }
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        int column_index = 0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }

    // 파일을 서버에 저장
    public void Async_ftp_Prepare(File file, String isPhotoAlbum) {
        Async_ftp async_ftp = new Async_ftp();
        async_ftp.execute(isPhotoAlbum);
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

            String isPhotoAlbum = params[0];
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

            } finally {
                try {
                    client.disconnect(true); // 연결중지
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }

            return null;
        }

    }

    // 파일을 서버에서 가져오기
    public void Async_ftp_Prepare_download() {
        Async_ftp_download async_ftp_download = new Async_ftp_download();
        async_ftp_download.execute();
    }
    // ftp 서버 연결 asyncTask
    class Async_ftp_download extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.d("이미지 다운로드", "시작");

            file = new File(Environment.getExternalStorageDirectory(), userPhoto); // sdcard에 새로운 파일 생성. 경로, 이름
            // Download file
            FTPClient client = new FTPClient();

            try {
                // 연결
                client.connect(FTP_HOST, 21);//ftp 서버와 연결, 호스트와 포트를 기입
                client.login(FTP_USER, FTP_PASS);//로그인을 위해 아이디와 패스워드 기입
                client.setType(FTPClient.TYPE_BINARY);//2진으로 변경
                client.changeDirectory(FTP_PATH);//서버에서 넣고 싶은 파일 경로를 기입
                client.download(userPhoto, file);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("이미지경로",file.getAbsolutePath());
                        Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.d("이미지 다운로드", "오류!!");
                file.delete();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();

            } finally {
                try {
                    client.disconnect(true); // 연결중지
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }

            return null;
        }

    }

    // 파일 서버 경로를 Db에 저장
    public String Async_db_Prepare(String isUpdate) {
        Async_db async_test = new Async_db();
        String result = null;
        try {
             result = async_test.execute(userID, userPhoto, isUpdate).get();
        } catch (InterruptedException e) {
            Log.d("이미지", "interruptedException");
            e.printStackTrace();
        } catch (ExecutionException e) {
            Log.d("이미지", "ExcutionException");
            e.printStackTrace();
        }
        return result;
    }

    // DB에 사진경로 저장하는 asyncTask
    class Async_db extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("이미지", result);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("photoJson");
                JSONObject item = jsonArray.getJSONObject(1);
                userPhoto = item.getString("userPhoto");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Async_ftp_Prepare_download();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection httpURLConnection = null;

            try {
                String userID = params[0];
                String userPhoto = params[1];
                String isUpdate = params[2];

                String data = null;
                String link = null;
                if("true".equals(isUpdate)){
                    Log.d("이미지", "성공");
                    data = URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");
                    data += "&" + URLEncoder.encode("userPhoto", "UTF-8") + "=" + URLEncoder.encode(userPhoto, "UTF-8");

                    link = "http://brad903.cafe24.com/" + "UserPhotoUpdate.php";

                }
                if("false".equals(isUpdate)){
                    Log.d("이미지", "검색");
                    data = URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");
                    link = "http://brad903.cafe24.com/" + "UserPhotoSelect.php";

                }
                URL url = new URL(link);

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                //httpURLConnection.setConnectTimeout(30);

                OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader
                        (httpURLConnection.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                httpURLConnection.disconnect();

                return sb.toString();
            } catch (Exception e) {
                Log.d("이미지 검색", "오류!!");

                httpURLConnection.disconnect();
                return new String("Exception Occure" + e.getMessage());
            }
        }
    }

    private int exifOrientationToDegrees(int exifOrientation) {
        Log.d("비트맵 이미지 수정", "회전각도 정하기");
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap src, float degree) {
        Log.d("비트맵 이미지 수정", "이미지 회전");
        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
    }
}
