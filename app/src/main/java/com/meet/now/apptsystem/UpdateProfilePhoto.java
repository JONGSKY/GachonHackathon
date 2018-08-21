package com.meet.now.apptsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import it.sauronsoftware.ftp4j.FTPClient;

import static android.support.v4.media.MediaBrowserServiceCompat.RESULT_OK;

public class UpdateProfilePhoto extends DialogFragment implements View.OnClickListener {

    //사진으로 전송시 되돌려 받을 번호
    static int REQUEST_PICTURE = 1;
    //앨범으로 전송시 돌려받을 번호
    static int REQUEST_PHOTO_ALBUM = 2;
    // 파일 크랍시 돌려받을 번호
    static int REQUEST_IMAGE_CROP = 3;

    //샘플
    static String SAMPLEIMG = null;

    String userID = null;
    String userPhoto = null;

    ImageView iv = null;

    public UpdateProfilePhoto() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // 다이얼로그 뷰 생성
        View view = inflater.inflate(R.layout.dialog_profile_photo_edit, null);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 유저아이디 받아옴
        userID = getArguments().getString("userID");

        // 프로필 이미지 뷰
        iv = getActivity().findViewById(R.id.iv_user);

    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.camera_btn) {
            // 카메라로 사진찍기
            Log.d("onClick", "카메라로 사진찍기");
            //takePicture();
            //dismiss();

        } else if (v.getId() == R.id.photoAlbum_btn) {
            // 앨범에서 가져오기
            Log.d("onClick", "앨범에서 가져오기");

            //photoAlbum();
           // dismiss();

        } else if(v.getId() == R.id.btn_back_photo){
            Log.d("onClick", "취소");
            dismiss();
        }

    }

    File file = null;
    void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        SAMPLEIMG = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg"; // 파일이름.
        file = new File(Environment.getExternalStorageDirectory(), SAMPLEIMG); // sdcard에 새로운 파일 생성. 경로, 이름
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

        startActivityForResult(intent, REQUEST_PICTURE);

    }

    void photoAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PHOTO_ALBUM);

    }


    Bitmap loadPicture() {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 이미지 바로띄우기는 가능.
        // 서버에 이미지를 업로드 -> db에 경로 뿌리기 -> dismiss()
        // activity에서 db경로 가져오기 -> 서버의 이미지 가져오기 -> 이미지 뿌리기
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICTURE) {
                // 이미지 바로 띄우기 -> 서버로 업로드하기로 변경
                Async_ftp_Prepare(file); // 파일 서버에 저장.
                iv.setImageBitmap(loadPicture());

            }

            if (requestCode == REQUEST_PHOTO_ALBUM) {
                Log.d("data.getData()",data.getData().toString());
                iv.setImageURI(data.getData());

            }

        }

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        getActivity().recreate();

    }


    // 파일을 서버에 저장
    public void Async_ftp_Prepare(File file) {
        Async_ftp async_ftp = new Async_ftp();
        async_ftp.execute();
    }
    // 파일 서버 경로를 Db에 저장
    public void Async_db_Prepare() {
        Async_test async_test = new Async_test();
        async_test.execute(userID, userPhoto);
    }

    // DB에 사진경로 저장하는 asyncTask
    class Async_test extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection httpURLConnection = null;

            try {
                String userID = params[0];
                String userPhoto = params[1];

                String data = URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");
                data += "&" + URLEncoder.encode("userPhoto", "UTF-8") + "=" + URLEncoder.encode(userPhoto, "UTF-8");

                //String data2 = "tmsg="+testMsg+"&tmsg2="+testMsg2;

                String link = "http://brad903.cafe24.com/" + "UserPhotoUpdate.php";

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

                httpURLConnection.disconnect();
                return new String("Exception Occure" + e.getMessage());
            }
        }
    }

    /*********  work only for Dedicated IP ***********/
    static final String FTP_HOST = "http://brad903.cafe24.com/";
    /*********  FTP USERNAME ***********/
    static final String FTP_USER = "brad903";
    /*********  FTP PASSWORD ***********/
    static final String FTP_PASS = "Nvo78/fd4h";
    static final String FTP_PATH = "userphoto/";

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

            } catch (Exception e) {
                e.printStackTrace();

            }finally {
                try {
                    // 연결중지
                    client.logout();
                    client.disconnect(true);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }

            return null;
        }//try catch end

    }//doInbackground end

}


