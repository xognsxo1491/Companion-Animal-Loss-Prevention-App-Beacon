package com.example.taehun.totalmanager;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;
import com.google.firebase.components.Component;


import java.util.List;
import java.util.UUID;

public class myBeacon extends Application {

    private BeaconManager beaconManager;

    @Override
    public void onCreate() {
        super.onCreate();

        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {

            @Override
            public void onServiceReady() {
                beaconManager.startMonitoring(new BeaconRegion("monitored", UUID.fromString("E2C56DB5-DFFB-48D2-B060-D0F5A71096E0"), 40001, 16966));
            }
        });

        beaconManager.setMonitoringListener(new BeaconManager.BeaconMonitoringListener() {
            @Override
            public void onEnteredRegion(BeaconRegion beaconRegion, List<Beacon> beacons) {
                Log.d("비콘", "들어옴");
                showNotification(
                        "비콘",
                        "가까이 있음");
            }


            @Override
            public void onExitedRegion(BeaconRegion beaconRegion) {
                Log.d("비콘", "나감");
                showNotification2(
                        "비콘",
                        "멀리 있음");
            }
        });
    }

    public void showNotification(String title, String message) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(title, message, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), title);

        Intent notificationIntent = new Intent(getApplicationContext(), PopupActivity.class);
        notificationIntent.putExtra("UUID", "E2C56DB5-DFFB-48D2-B060-D0F5A71096E0");
        notificationIntent.putExtra("Major", "40001");
        notificationIntent.putExtra("Minor", "16966");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int requestID = (int) System.currentTimeMillis();

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentTitle(title) // required
                .setContentText(message)  // required
                .setDefaults(Notification.DEFAULT_ALL) // 알림, 사운드 진동 설정
                .setAutoCancel(true) // 알림 터치시 반응 후 삭제

                .setSound(RingtoneManager

                        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

                .setSmallIcon(android.R.drawable.btn_star)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        android.R.drawable.ic_dialog_info))
                .setBadgeIconType(android.R.drawable.ic_dialog_info)

                .setContentIntent(pendingIntent);
        notificationManager.notify(0, builder.build());
    }
    public void showNotification2(String title, String message) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(title, message, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), title);

        Intent notificationIntent = new Intent(getApplicationContext(), Popup2Activity.class);
        notificationIntent.putExtra("UUID", "E2C56DB5-DFFB-48D2-B060-D0F5A71096E0");
        notificationIntent.putExtra("Major", "40001");
        notificationIntent.putExtra("Minor", "16966");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int requestID = (int) System.currentTimeMillis();

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentTitle(title) // required
                .setContentText(message)  // required
                .setDefaults(Notification.DEFAULT_ALL) // 알림, 사운드 진동 설정
                .setAutoCancel(true) // 알림 터치시 반응 후 삭제

                .setSound(RingtoneManager

                        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

                .setSmallIcon(android.R.drawable.btn_star)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        android.R.drawable.ic_dialog_info))
                .setBadgeIconType(android.R.drawable.ic_dialog_info)

                .setContentIntent(pendingIntent);
        notificationManager.notify(0, builder.build());
    }
}
