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
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.taehun.totalmanager.Request.Board1WriteRequest;
import com.google.firebase.components.Component;


import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class myBeacon extends Application implements BeaconConsumer{
    BeaconManager beaconManager;
    private List<Beacon> beaconList = new ArrayList<>();
    SharedPreferences preferences;// 자동 로그인 데이터 저장
    SharedPreferences.Editor editor;
    @Override
    public void onCreate() {
        super.onCreate();

        beaconManager = BeaconManager.getInstanceForApplication(getApplicationContext());
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215, i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);
        preferences = getSharedPreferences("Beacon",getApplicationContext().MODE_PRIVATE);
        editor = preferences.edit();
        editor.putBoolean("BeaconAlram", true);
        editor.putBoolean("BeaconEmergency", true);
        editor.commit();
        handler.sendEmptyMessage(0);
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
        notificationIntent.putExtra("Minor", "15404");
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
        notificationIntent.putExtra("Minor", "15404");
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

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {

                if (collection.size() > 0) {

                    beaconList.clear();
                    for (Beacon beacon : collection) {
                        beaconList.add(beacon);
                    }
                }
            }
        });
        try{
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        }catch(RemoteException e){}
    }
    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            for(Beacon beacon : beaconList){
                if(preferences.getBoolean("BeaconAlram", false)&&((int)beacon.getDistance())<=4){
                    Log.d("비콘", "가까이 있음");
                    showNotification("비콘", "10미터 이내에 있음");
                    editor.putBoolean("BeaconAlram", false);
                    editor.putBoolean("BeaconEmergency", true);
                    editor.commit();
                }else if(preferences.getBoolean("BeaconEmergency", false)&&((int)beacon.getDistance())>4){
                    Log.d("비콘", "멀리있음");
                    showNotification2("비콘", "10미터 밖에 있음");
                    editor.putBoolean("BeaconAlram", true);
                    editor.putBoolean("BeaconEmergency", false);
                    editor.commit();
                }
            }

            handler.sendEmptyMessageDelayed(0, 1000);
        }
    };
}
