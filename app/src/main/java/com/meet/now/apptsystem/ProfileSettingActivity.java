package com.meet.now.apptsystem;

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
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileSettingActivity extends AppCompatActivity {
    ImageView ivImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);

/*
        // 프로필 사진 등록하기
        ImageButton button = findViewById(R.id.ib_edit_userimg);
        ivImage = findViewById(R.id.iv_user);
        // 프로필 라운딩
        ivImage.setBackground(new ShapeDrawable(new OvalShape()));
        ivImage.setClipToOutline(true);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });*/
    }

    void show() {
        final List<String> ListItems = new ArrayList<>();
        ListItems.add("앨범에서 사진 가져오기");
        ListItems.add("카메라로 사진 찍기");
        final CharSequence[] items = ListItems.toArray(new String[ListItems.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("프로필 변경");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        // 앨범
                        Log.d("앨범에서 가져오기", "start");
                        GalleryPhoto();
                        break;
                    case 1:
                        // 카메라
                        Log.d("카메라로 가져오기", "start");
                        getProfileImg();
                        break;
                }
            }

        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }


    private final int CAMERA_CODE = 1110;
    private final int GALLERY_CODE = 1112;
    File file = null;

    private File createFile() throws IOException {
        String imageFileName = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        File storageDir = Environment.getExternalStorageDirectory();
        File curFile = new File(storageDir, imageFileName);

        return curFile;
    }

    private void getProfileImg() {
        Intent getProfileImgIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            file = createFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        getProfileImgIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        if (getProfileImgIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(getProfileImgIntent, CAMERA_CODE);
        }
    }

    private void GalleryPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        Log.d("앨범에서 가져오기", "Intent 시작");
        startActivityForResult(intent, GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // 이미지 비트맵으로 띄우기 수정 필요.
        if (requestCode == RESULT_OK) {

            switch (requestCode) {

                case GALLERY_CODE:
                    Log.d("앨범에서 가져오기", "인텐트 받음");
                    sendPicture(data.getData()); //갤러리에서 가져오기
                    break;
                case CAMERA_CODE:
                    Log.d("카메라로 가져오기", "인텐트 받음");
                    getPictureForPhoto(); //카메라에서 가져오기
                    break;

                default:
                    break;
            }
        }
    }

    private void getPictureForPhoto() {
        Log.d("카메라로 가져오기", "비트맵이미지 경로");
        Log.d(this.getClass().getName(), "경로 " + file.getAbsolutePath());
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation;
        int exifDegree;

        if (exif != null) {
            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            exifDegree = exifOrientationToDegrees(exifOrientation);
        } else {
            exifDegree = 0;
        }
        ivImage.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
    }

    private void sendPicture(Uri imgUri) {
        Log.d("앨범 절대경로 가져오기", "시작");
        String imagePath = getRealPathFromURI(imgUri); // path 경로
        Log.d("앨범에서 가져오기", "이미지 경로");
        Log.d(this.getClass().getName(), "경로 " + imagePath);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);
        Log.d("앨범에서 가져오기", "이미지 경로");
        Log.d(this.getClass().getName(), "경로 " + imagePath);
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);//경로를 통해 비트맵으로 전환
        ivImage.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기

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

    private String getRealPathFromURI(Uri contentUri) {
        Log.d("비트맵 이미지 절대경로 가져오기", "절대경로 가져오기");
        int column_index = 0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }
}

