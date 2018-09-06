package com.meet.now.apptsystem;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static com.meet.now.apptsystem.UpdateProfilePhoto.REQUEST_PHOTO_ALBUM;
import static com.meet.now.apptsystem.UpdateProfilePhoto.REQUEST_PICTURE;

public class ProfileLoadActivity extends AppCompatActivity {

    String userNickname = null;
    String userStatusmsg = null;
    String userPhoto = null;
    String userAddress = null;
    String userID = null;
    int position = -1;

    private UpdateProfilePhoto updateProfilePhoto;
    public static File file = null;
    public static File cacheDir;
    TextView nicknameText;
    TextView statusmsgText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_load);
        nicknameText = findViewById(R.id.tv_user);
        statusmsgText = findViewById(R.id.tv_introduce);
        ImageView iv = findViewById(R.id.iv_user);


        ImageButton logout = findViewById(R.id.ib_logout);
        ImageButton backBtn = findViewById(R.id.ib_back);
        ImageButton ibEditStatus = findViewById(R.id.ib_edit_status);
        ImageButton ibEditImg = findViewById(R.id.ib_edit_userImg);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        // 친구프로필
        if (!userID.equals(MyApplication.userID)) {
            logout.setVisibility(View.GONE);
            backBtn.setVisibility(View.GONE);
            ibEditStatus.setVisibility(View.GONE);
            ibEditImg.setVisibility(View.GONE);

            String friendNickname = intent.getStringExtra("getFriendNickname");
            if (friendNickname.equals("null")) {
                userNickname = intent.getStringExtra("getUserNickname");
            } else {
                userNickname = friendNickname;
            }
            userPhoto = intent.getStringExtra("getUserPhoto");
            userAddress = intent.getStringExtra("getUserAddress");
            userStatusmsg = intent.getStringExtra("getUserStatusmsg");
            position = intent.getIntExtra("position", -1);
            nicknameText.setText(userNickname);
            if (!userStatusmsg.equals("null")) statusmsgText.setText(userStatusmsg);
            if (!userPhoto.equals("null")) {
                Bitmap bitmap = bitmapImgDownload(userPhoto);
                iv.setImageBitmap(bitmap);
                iv.setBackground(new ShapeDrawable(new OvalShape())); // 프로필 라운딩
                iv.setClipToOutline(true);
            }

            //내프로필
        } else {
            cacheDir = getApplicationContext().getCacheDir();

            JSONObject jo = new JSONObject();
            try {
                jo.put("이름", 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
                            ImageView iv = findViewById(R.id.iv_user);

                            nicknameText.setText(userNickname);
                            if (!userStatusmsg.equals("null")) statusmsgText.setText(userStatusmsg);


                            if (!userPhoto.equals("null")) {
                                Bitmap bitmap = bitmapImgDownload(userPhoto);
                                iv.setImageBitmap(bitmap);
                                iv.setBackground(new ShapeDrawable(new OvalShape())); // 프로필 라운딩
                                iv.setClipToOutline(true);
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
        }
        // 이벤트

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogout();
            }
        });

        // 뒤로가기 버튼

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
                bundle.putString("userNickname", nicknameText.getText().toString());
                dialogFragment.setArguments(bundle);

                dialogFragment.show(fm, "fragment_dialog_nickname");

                dialogFragment.setDialogResult(new UpdateProfileNickname.MyDialogResult() {
                    @Override
                    public void finish(String result) {
                        nicknameText.setText(result);
                        if (!userID.equals(MyApplication.userID)) {
                            userNickname = result;
                            Intent intent = getIntent();
                            intent.putExtra("position", position);
                            intent.putExtra("userNickname", userNickname);
                            setResult(RESULT_OK, intent);
                        }
                    }
                });

            }
        });

        // 상태메시지 수정
        ibEditStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                UpdateProfileStatus dialogFragment = new UpdateProfileStatus();

                Bundle bundle = new Bundle();
                bundle.putString("userID", userID);
                bundle.putString("userStatusmsg", statusmsgText.getText().toString());
                dialogFragment.setArguments(bundle);

                dialogFragment.show(fm, "fragment_dialog_status");
                dialogFragment.setDialogResult(new UpdateProfileStatus.MyDialogResult() {
                    @Override
                    public void finish(String result) {
                        statusmsgText.setText(result);
                    }
                });
            }

        });

        // 위치변경
        ImageButton mapBtn = findViewById(R.id.ib_map);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoc();
            }
        });

        // 이미지 변경
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
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_profile_loc, null);
        builder.setView(view);

        TextView tvLoc = view.findViewById(R.id.tv_loc);
        ImageButton ibBack = view.findViewById(R.id.ib_back_loc);

        tvLoc.setText(userAddress);

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

        if (resultCode == RESULT_OK) {
            Bitmap bitmap;

            String imagePath = null;
            if (requestCode == REQUEST_PICTURE) {
                imagePath = file.getAbsolutePath();
            } else if (requestCode == REQUEST_PHOTO_ALBUM) {
                imagePath = getRealPathFromURI(data.getData());
                file = new File(imagePath);
            }

            ExifInterface exif = null;
            try {
                assert imagePath != null;
                exif = new ExifInterface(imagePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert exif != null;
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int exifDegree = exifOrientationToDegrees(exifOrientation);
            bitmap = loadPictureWithResize();
            bitmap = rotate(bitmap, exifDegree);

            SaveBitmapToFileCache(bitmap); // 비트맵 변환하여 캐시, 서버, db 저장
            ImageView iv = findViewById(R.id.iv_user);
            iv.setImageBitmap(bitmap);//이미지 뷰에 비트맵 넣기
            iv.setBackground(new ShapeDrawable(new OvalShape())); // 프로필 라운딩
            iv.setClipToOutline(true);
        }
    }

    public void Async_ftp_Prepare(String act, String userPhoto) {
        Async_ftp async_ftp = new Async_ftp();
        async_ftp.execute(act, userPhoto);
    }

    public void Async_db_Prepare() {
        Async_db async_test = new Async_db();
        async_test.execute(userID, userPhoto);
    }

    // URI -> 파일경로
    private String getRealPathFromURI(Uri contentUri) {
        int column_index = 0;
        String[] proj = {MediaStore.Images.Media.DATA};
        @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        assert cursor != null;
        if (cursor.moveToFirst()) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }

    // 이미지 리사이징
    private Bitmap loadPictureWithResize() {
        Bitmap resizeBitmap;

        BitmapFactory.Options options = new BitmapFactory.Options();
        BitmapFactory.decodeFile(file.getAbsolutePath(), options); // 1번

        int width = options.outWidth;
        int height = options.outHeight;
        int samplesize = 1;

        while (true) {//2번
            if (width / 2 < 200 || height / 2 < 200)
                break;
            width /= 2;
            height /= 2;
            samplesize *= 2;
        }

        options.inSampleSize = samplesize;
        resizeBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

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

    private void SaveBitmapToFileCache(Bitmap bitmap) {
        String fileName = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".JPEG";
        file = new File(getApplicationContext().getCacheDir(), fileName);
        OutputStream out = null;

        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            Async_ftp_Prepare("upload", userPhoto); // 파일 서버에 저장.
            userPhoto = file.getName();
            Async_db_Prepare(); // 파일 db 저장
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert out != null;
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    Bitmap bitmapImgDownload(String userPhoto) {
        Bitmap bitmap;
        String imgPath;

        imgPath = "data/data/com.meet.now.apptsystem/cache/" + userPhoto;
        File file = new File(imgPath);

        if (!file.exists()) {
            file = new File(Environment.getExternalStorageDirectory(), userPhoto);
            if (!file.exists()) {
                Async_ftp_Prepare("download", userPhoto);
                file = new File(Environment.getExternalStorageDirectory(), userPhoto);
            }
            imgPath = file.getAbsolutePath();
//            file.delete();
        }

        bitmap = BitmapFactory.decodeFile(imgPath);

        return bitmap;
    }

    void showLogout() {
        new AlertDialog.Builder(this)
                .setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        MyApplication.userID = "";
                        MyApplication.password = "";
                        MyApplication.Address = "";
                        finishAffinity();
                        Intent logoutIntent = new Intent(ProfileLoadActivity.this, LoginActivity.class);
                        startActivity(logoutIntent);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}