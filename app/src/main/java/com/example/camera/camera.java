package com.example.camera;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.Collections;


public class camera extends Activity {
    private static final int PERMISSION_CODE = 1;
    ImageButton btn;
    ImageView imageView;
    Uri image;
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.main);
        createNotificationChannels();
        // set thoi gian nhac chup hinh
        Intent intentTime = new Intent(camera.this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(camera.this, 0, intentTime, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // cứ 10s khi ct chạy
//        long timeAtStart = System.currentTimeMillis();
//        long tenSeconds = 1000 * 10;
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
//                timeAtStart + tenSeconds, 1,pendingIntent);
        // thời gian cụ the vào mỗi ngày
        // Lấy thời điểm hiện tại
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

// Đặt thời gian cụ thể là 9 giờ tối
        calendar.set(Calendar.HOUR_OF_DAY, 21); // 21 là 9 giờ tối
        calendar.set(Calendar.MINUTE, 10);
        calendar.set(Calendar.SECOND, 0);

// Nếu thời gian đã qua 9 giờ tối, thì đặt cho ngày mai
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

// Đặt alarm
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        btn = (ImageButton) findViewById(R.id.imageButton);
        imageView = (ImageView) findViewById(R.id.imgView);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                        String[] permission =
                                {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

                        requestPermissions(permission, PERMISSION_CODE);
                    } else {
                        openCamera();
                    }
                } else {
                    openCamera();
                }
            }
        });
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "new image");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From The Camera");
        image = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
           imageView.setImageURI(image);
        }

    }

    private void createNotificationChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CharSequence name = "Thông báo chụp hình";
            String description = "Tới giờ chụp hình rồi bạn ơiiiii";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Thông báo", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannels(Collections.singletonList(channel));
        }
    }
}
