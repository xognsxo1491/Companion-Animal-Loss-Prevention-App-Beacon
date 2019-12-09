package com.example.taehun.totalmanager;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.taehun.totalmanager.BoardRegion.BoardRegionMapActivity;
import com.example.taehun.totalmanager.Request.BeaconMissingRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.TimerTask;

public class Popup2Activity extends Activity {

    String strMajor,strminor,strUuid;
    GPSListener gpsListener;
    Button btn ,btn3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup2);

        btn = findViewById(R.id.notification2_btn1);
        btn3 = findViewById(R.id.notification2_btn3);

        Intent intent = getIntent();

        gpsListener = new GPSListener();

        strUuid = intent.getStringExtra("UUID");
        strMajor = intent.getStringExtra("Major");
        strminor = intent.getStringExtra("Minor");

        System.out.println(strUuid +" + " + strMajor + " + " + strminor);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences preferences = getSharedPreferences("freeLogin", Context.MODE_PRIVATE); // freeLogin 이라는 키 안에 데이터 저장
                String userId = preferences.getString("Id", null);
                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            boolean success = jsonObject.getBoolean("success"); // php가 db 접속이 성공적일 경우 success라는 문구가 나오는데 success를 캐치
//
//                            if (success) { // 성공일 경우
//                                Intent intent = new Intent(getApplicationContext(), FreeBoard_Activity.class);
//                                startActivity(intent);
//                            }
//
//                        } catch (JSONException e) { //오류 캐치
//                            e.printStackTrace();
//                        }
                        //방법이 없어서 일단 주석처리
                    }
                };

                startLocationService();

                BeaconMissingRequest beaconMissingRequest = new BeaconMissingRequest(userId, strUuid, strMajor, strminor, gpsListener.latitude, gpsListener.longitude, responseListener); // 입력 값을 넣기 위한 request 클래스 참조
                RequestQueue queue = Volley.newRequestQueue(Popup2Activity.this);
                queue.add(beaconMissingRequest);

                finish();
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(getApplicationContext(), BoardRegionMapActivity.class);
                startActivity(intent1);
            }
        });
    }

    private void startLocationService() {
        // 위치 관리자 객체 참조
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 위치 정보를 받을 리스너 생성
        long minTime = 10000;
        float minDistance = 0;

        try {
            // GPS를 이용한 위치 요청
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
            // 네트워크를 이용한 위치 요청
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);
            // 위치 확인이 안되는 경우에도 최근에 확인된 위치 정보 먼저 확인
            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastLocation != null) {
                gpsListener.latitude = lastLocation.getLatitude();
                gpsListener.longitude = lastLocation.getLongitude();

                Toast.makeText(this, "간편 실종등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            }

            if(gpsListener.latitude == 0.0) {

                Intent intent = new Intent(getApplicationContext(), BeaconMissingActivity.class);
                intent.putExtra("UUID", strUuid);
                intent.putExtra("Major", strMajor);
                intent.putExtra("Minor", strminor);
                startActivity(intent);

                Toast.makeText(this, "위치정보를 받아올 수 없습니다.\n 직접 선택해주세요", Toast.LENGTH_LONG).show();
            }

        } catch(SecurityException ex) {
            ex.printStackTrace();
        }
    }
}