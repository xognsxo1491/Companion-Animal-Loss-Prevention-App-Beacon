package com.example.taehun.totalmanager;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.taehun.totalmanager.Request.BeaconFindRequest;
import com.example.taehun.totalmanager.Request.Board1CommentWriteRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

public class myBeacon extends Application implements BeaconConsumer{
    BeaconManager beaconManager;
    private List<Beacon> beaconList = new ArrayList<>();
    SharedPreferences preferences;// 자동 로그인 데이터 저장
    SharedPreferences.Editor editor;
    String myJSON;
    JSONArray jsonArray;
    ArrayList<BeaconListItem> myBeacons = new ArrayList<>();
    ArrayList<BeaconListItem> missingBeacons= new ArrayList<>();
    private static final String TAG_UUID = "UUID";
    private static final String TAG_MAJOR = "Major";
    private static final String TAG_Minor = "Minor";
    private static final String TAG_RESULT = "result";
    long start = System.currentTimeMillis(); //시작하는 시점 계산
    long end;

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
        editor.putBoolean("findMyBeacon", true);
        editor.putBoolean("first", true);
        editor.commit();

        getBeaconsFromDataBase("http://xognsxo1491.cafe24.com/Beacon_connect.php");
        getMissingBeaconsFromDataBase("http://xognsxo1491.cafe24.com/Beacon_connect.php");
//        System.out.println(myBeacons.get(0).getMajor());
        handler.sendEmptyMessage(0);
    }

    public void showNearNotification(String title, String message, String UUID, String major, String minor) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(title, message, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), title);

        Intent notificationIntent = new Intent(getApplicationContext(), PopupActivity.class);
        notificationIntent.putExtra("UUID", UUID);
        notificationIntent.putExtra("Major", major);
        notificationIntent.putExtra("Minor", minor);
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
    public void showFarNotification(String title, String message, String UUID, String major, String minor) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(title, message, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), title);

        Intent notificationIntent = new Intent(getApplicationContext(), Popup2Activity.class);
        notificationIntent.putExtra("UUID", UUID);
        notificationIntent.putExtra("Major", major);
        notificationIntent.putExtra("Minor", minor);
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
    protected void addBeacons(ArrayList<BeaconListItem> beacons) {  // php 파싱 설정
        try {
            JSONObject jsonObject = new JSONObject(myJSON);
            jsonArray = jsonObject.getJSONArray(TAG_RESULT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String UUID = object.getString(TAG_UUID);
                String major = object.getString(TAG_MAJOR);
                String minor = object.getString(TAG_Minor);
                System.out.println("비콘 " + UUID + " " + major + " " +minor);
                beacons.add(new BeaconListItem(UUID, major, minor));

            }

        } catch (JSONException e) { // 오류 캐치
            e.printStackTrace();
        }
    }
    public void getBeaconsFromDataBase(String url) { // php 파싱관련

        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                SharedPreferences preference2 = getSharedPreferences("freeLogin",Context.MODE_PRIVATE);
                String uri = params[0];
                BufferedReader bufferedReader = null;

                String postParameter = "Id="+ preference2.getString("Id","");
                Log.d("Number", postParameter);

                try {
                    URL url = new URL(uri);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(5000);
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.connect();
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(postParameter.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();
                    int responseStatusCode = connection.getResponseCode();

                    InputStream inputStream;
                    if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                        inputStream = connection.getInputStream();
                    }

                    else{
                        inputStream = connection.getErrorStream();
                    }
                    StringBuilder builder = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        builder.append(json + "\n");
                    }
                    return builder.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) { // url 추출
                myJSON = s;
                addBeacons(myBeacons);
            }

        }
        GetDataJSON getDataJSON = new GetDataJSON();
        getDataJSON.execute(url);
    }
    public void getMissingBeaconsFromDataBase(String url) { // php 파싱관련

        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                SharedPreferences preference2 = getSharedPreferences("freeLogin",Context.MODE_PRIVATE);
                String uri = params[0];
                BufferedReader bufferedReader = null;

                String postParameter = "Missing=true";
                Log.d("Number", postParameter);

                try {
                    URL url = new URL(uri);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(5000);
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.connect();
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(postParameter.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();
                    int responseStatusCode = connection.getResponseCode();

                    InputStream inputStream;
                    if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                        inputStream = connection.getInputStream();
                    }

                    else{
                        inputStream = connection.getErrorStream();
                    }
                    StringBuilder builder = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        builder.append(json + "\n");
                    }
                    return builder.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) { // url 추출
                myJSON = s;
                addBeacons(missingBeacons);
            }

        }
        GetDataJSON getDataJSON = new GetDataJSON();
        getDataJSON.execute(url);
    }
            Handler handler = new Handler(){
        public void handleMessage(Message msg){
//            System.out.println("BeaconAlram "+preferences.getBoolean("findMyBeacon", false) +" BeaconEmergency "+
//                    preferences.getBoolean("BeaconEmergency", false) +" findMyBeacon "+ preferences.getBoolean("findMyBeacon", false) + " first "+preferences.getBoolean("first", false));
            end = System.currentTimeMillis();
            editor.putBoolean("findMyBeacon", true);
            editor.commit();
                for (Beacon beacon : beaconList) {
                    if (myBeacons.size()>0&&beacon.getId1().toString().equals(myBeacons.get(0).getUUID())
                            && beacon.getId2().toString().equals(myBeacons.get(0).getMajor())
                            && beacon.getId3().toString().equals(myBeacons.get(0).getMinor())) {
                        editor.putBoolean("findMyBeacon", false);
                        editor.commit();
                        if (preferences.getBoolean("BeaconAlram", false) && ((int) beacon.getDistance()) <= 4) {
                            Log.d("비콘", "가까이 있음");
                            showNearNotification("비콘", "10미터 이내에 있음", beacon.getId1().toString(), beacon.getId2().toString(), beacon.getId3().toString());
                            editor.putBoolean("BeaconAlram", false);
                            editor.putBoolean("BeaconEmergency", true);
                            editor.putBoolean("first", true);
                            editor.commit();
                        } else if (preferences.getBoolean("BeaconEmergency", false) && (((int) beacon.getDistance()) > 4)) {
                            Log.d("비콘", "멀리있음");
                            showFarNotification("비콘", "10미터 밖에 있음", beacon.getId1().toString(), beacon.getId2().toString(), beacon.getId3().toString());
                            editor.putBoolean("BeaconAlram", true);
                            editor.putBoolean("BeaconEmergency", false);
                            editor.commit();
                        }
                    }

                    for (BeaconListItem missingBeacon : missingBeacons) {
//                        System.out.println("FindBeacons " + beacon.getId1().toString() +" " +beacon.getId2().toString() + " " +beacon.getId3().toString() + "missingBeacon "+ " "+ missingBeacon.getUUID() +" " + missingBeacon.getMajor() + " " + missingBeacon.getMinor());
                        if ((end-start)>60000&&beacon.getId1().toString().equals(missingBeacon.getUUID())
                                && beacon.getId2().toString().equals(missingBeacon.getMajor())
                                & beacon.getId3().toString().equals(missingBeacon.getMinor())) {
                            Log.d("비콘", "실종발견");
                            Response.Listener<String> responseListener = new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        boolean success = jsonObject.getBoolean("success"); // php가 db 접속이 성공적일 경우 success라는 문구가 나오는데 success를 캐치

                                        if (success) { // 성공일 경우
                                            Log.d("비콘", "실종발견");
                                        }

                                    } catch (JSONException e) { //오류 캐치
                                        e.printStackTrace();
                                    }
                                }
                            };
                            String UUID = beacon.getId1().toString();
                            String major = beacon.getId2().toString();
                            String minor = beacon.getId3().toString();

                            BeaconFindRequest beaconFindRequest = new BeaconFindRequest(UUID, major, minor, responseListener); // 입력 값을 넣기 위한 request 클래스 참조
                            RequestQueue queue = Volley.newRequestQueue(myBeacon.this);
                            queue.add(beaconFindRequest);

                            start = System.currentTimeMillis();
                            end = System.currentTimeMillis();
                        }
                    }
                }
                if (myBeacons.size() > 0 && preferences.getBoolean("findMyBeacon", false) && preferences.getBoolean("first", false)) {
                    Log.d("비콘", "감지 안됨");
                    showFarNotification("비콘", "10미터 밖에 있음", myBeacons.get(0).getUUID(), myBeacons.get(0).getMajor(), myBeacons.get(0).getMinor());
                    editor.putBoolean("findMyBeacon", false);
                    editor.putBoolean("first", false);
                    editor.putBoolean("BeaconAlram", true);
                    editor.commit();
                }
            beaconList.clear();
            handler.sendEmptyMessageDelayed(0, 3000);
        }
    };
}
