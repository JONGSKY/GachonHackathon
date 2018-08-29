package com.meet.now.apptsystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.media.ExifInterface;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import it.sauronsoftware.ftp4j.FTPClient;

import static com.meet.now.apptsystem.UpdateProfilePhoto.REQUEST_PHOTO_ALBUM;
import static com.meet.now.apptsystem.UpdateProfilePhoto.REQUEST_PICTURE;

public class ProfileLoadActivity extends AppCompatActivity {

    String userNickname = null;
    String userStatusmsg = null;
    String userPhoto = null;
    String userAddress = null;
    String userID = null;

    private UpdateProfilePhoto updateProfilePhoto;
    public static File file = null;
    ImageView iv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_load);
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

                        TextView nicknameText = findViewById(R.id.tv_user);
                        TextView statusmsgText = findViewById(R.id.tv_introduce);
                        iv = findViewById(R.id.iv_user);

                        nicknameText.setText(userNickname);
                        if (!userStatusmsg.equals("null")) statusmsgText.setText(userStatusmsg);

                        if (userPhoto != null) {
                            bitmapImgDownload(userPhoto);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "정보를 가져오지 못했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        ProfileLoadRequest profileLoadRequest = new ProfileLoadRequest(userID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ProfileLoadActivity.this);
        queue.add(profileLoadRequest);

        // 이벤트
        // 뒤로가기 버튼
        ImageButton backBtn = findViewById(R.id.ib_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // 닉네임 수정
        ImageButton ibNickname = findViewById(R.id.ib_edit_nickname);
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
        ImageButton ibEditStatus = findViewById(R.id.ib_edit_status);
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

        // 위치보기
        ImageButton mapBtn = findViewById(R.id.ib_map);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoc();
            }
        });

        // 이미지 변경
        ImageButton ibEditImg = findViewById(R.id.ib_edit_userImg);
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

    }

    // 위치 보여주기
    void showLoc() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = (View)inflater.inflate(R.layout.dialog_profile_loc, null);
        builder.setView(view);

        TextView tvLoc = (TextView) view.findViewById(R.id.tv_loc);
        ImageButton ibBack = (ImageButton) view.findViewById(R.id.ib_back_loc);

        if (!userAddress.equals("null")) tvLoc.setText(userAddress);

        final AlertDialog dialog = builder.create();

        ibBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    // 이미지 카메라, 앨범에서 가져오기
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.w("resultCode", String.valueOf(resultCode));
        Log.w("requestCode", String.valueOf(requestCode));

        if (resultCode == RESULT_OK) {
            String imagePath;
            if (requestCode == REQUEST_PICTURE) {
                imagePath = file.getAbsolutePath();
                if (file.exists()) setImage(imagePath);
            } else if (requestCode == REQUEST_PHOTO_ALBUM) {
                imagePath = getRealPathFromURI(data.getData());
                if (imagePath != null) file = new File(imagePath);
                if (file.exists()) setImage(imagePath);
            }

        }

    }

    public void setImage(String imagePath) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (exif == null) {
            return;
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);
        Bitmap bitmap = loadPictureWithResize();
        bitmap = rotate(bitmap, exifDegree);

        ImageView iv = findViewById(R.id.iv_user);
        iv.setImageBitmap(bitmap);//이미지 뷰에 비트맵 넣기
        iv.setBackground(new ShapeDrawable(new OvalShape())); // 프로필 라운딩
        iv.setClipToOutline(true);

        SaveBitmapToFileCache(bitmap); // 비트맵 변환하여 캐시, 서버, db 저장
    }


    private void Async_ftp_Prepare(String act) {
        Async_ftp async_ftp = new Async_ftp(new AsyncTaskListener() {
            @Override
            public void taskCompleted(File result) {
                Log.e("taskCompleted", result.getAbsolutePath());
                if (result.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    ImageView iv = findViewById(R.id.iv_user);
                    iv.setImageBitmap(bitmap);
                    iv.setBackground(new ShapeDrawable(new OvalShape())); // 프로필 라운딩
                    iv.setClipToOutline(true);

                    // 캐시 업데이트
                    byte[] buf = new byte[1024];
                    FileInputStream in = null;
                    FileOutputStream out = null;
                    File oldFile = file;
                    File newFile = new File(getApplicationContext().getCacheDir(), userPhoto);

                    try {
                        if (!oldFile.renameTo(newFile)) {
                            buf = new byte[1024];
                            in = new FileInputStream(oldFile);
                            out = new FileOutputStream(newFile);
                            int read = 0;
                            while ((read = in.read(buf, 0, buf.length)) != -1) {
                                out.write(buf, 0, read);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if(in != null) in.close();
                            if(out != null) out.close();
                            if(oldFile.exists()) oldFile.delete();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                }

            }
        });
        async_ftp.execute(act, userPhoto);
    }

    private void Async_db_Prepare() {
        Async_db async_test = new Async_db();
        async_test.execute(userID, userPhoto);
    }

    // URI -> 파일경로
    private String getRealPathFromURI(Uri contentUri) {
        int column_index = 0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) return null;
        if (cursor.moveToFirst()) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        String ret = cursor.getString(column_index);
        cursor.close();
        return ret;
    }

    // 이미지 리사이징
    private Bitmap loadPictureWithResize() {
        int resize = 200;
        Bitmap resizeBitmap;

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
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options); //3번
        resizeBitmap = bitmap;

        return resizeBitmap;

    }

    // 이미지 회전
    private int exifOrientationToDegrees(int exifOrientation) {
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
        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
    }

    // 기존 이미지 삭제 후 이미지 캐시, 서버, db 업로드
    private void SaveBitmapToFileCache(Bitmap bitmap) {
        String fileName = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".JPEG";
        file = new File(getApplicationContext().getCacheDir(), fileName);
        OutputStream out = null;

        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            Async_ftp_Prepare("upload"); // 파일 서버에 저장.
            userPhoto = file.getName();
            Async_db_Prepare(); // 파일 db 저장
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void bitmapImgDownload(String userPhoto) {
        Bitmap bitmap;
        File file = new File(getApplicationContext().getCacheDir(), userPhoto);
        Log.e("cacheFile", file.getAbsolutePath());
        if (file.exists()) { // 캐시 다운로드
            String imgPath = getApplicationContext().getCacheDir() + "/" + userPhoto;
            Log.e("cacheFile down", imgPath);
            bitmap = BitmapFactory.decodeFile(imgPath);
            ImageView iv = findViewById(R.id.iv_user);
            iv.setImageBitmap(bitmap);
            iv.setBackground(new ShapeDrawable(new OvalShape())); // 프로필 라운딩
            iv.setClipToOutline(true);


        } else {
            Async_ftp_Prepare("download");
        }
    }

    public interface AsyncTaskListener {
        void taskCompleted(File result);
    }

    // ftp 서버 연결 asyncTask
    @SuppressLint("StaticFieldLeak")
    class Async_ftp extends AsyncTask<String, File, File> {

        AsyncTaskListener listener;

        Async_ftp(AsyncTaskListener listener) {
            this.listener = listener;
        }

        protected void onPostExecute(File result) {
            if (this.listener != null) {
                if (result != null) this.listener.taskCompleted(result);
            }
        }

        static final String FTP_HOST = "brad903.cafe24.com";
        static final String FTP_USER = "brad903";
        static final String FTP_PASS = "Nvo78/fd4h";
        static final String FTP_PATH = "../userphoto/";

        @Override
        protected File doInBackground(String... params) {

            String act = params[0];
            String userPhoto = params[1];
            // Upload file
            FTPClient client = new FTPClient();

            try {
                // 연결
                client.connect(FTP_HOST, 21);//ftp 서버와 연결, 호스트와 포트를 기입
                client.login(FTP_USER, FTP_PASS);//로그인을 위해 아이디와 패스워드 기입
                client.setType(FTPClient.TYPE_BINARY);//2진으로 변경
                client.changeDirectory(FTP_PATH);//서버에서 넣고 싶은 파일 경로를 기입
                if (act.equals("upload")) {
                    client.upload(file);
                    client.deleteFile(userPhoto);
                }
                if (act.equals("download")) {
                    file = new File(Environment.getExternalStorageDirectory(), userPhoto);
                    client.download(userPhoto, file);
                    return file;
                }

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                try {
                    client.disconnect(true);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            return null;
        }
    }
}
