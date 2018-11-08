package com.example.taehun.totalmanager.BoardRegion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.example.taehun.totalmanager.BeaconListItem;
import com.example.taehun.totalmanager.GPSListener;
import com.example.taehun.totalmanager.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class FindMapsActivity extends FragmentActivity implements OnMapReadyCallback {
    FloatingActionButton fab_gps;
    private GoogleMap mMap;
    GPSListener gpsListener;
    Animation operatingAnim;
    LocationManager locationManager;
    private int GALLERY = 1000;
    Timer timer;
    String myJSON;
    JSONArray jsonArray;
    private static final String TAG_UUID = "UUID";
    private static final String TAG_MAJOR = "Major";
    private static final String TAG_MINOR = "Minor";
    private static final String TAG_RESULT = "result";
    private static final String TAG_TIME = "Time";
    private static final String TAG_LATITUDE = "Latitude";
    private static final String TAG_LONGTITUDE = "Longtidue";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        gpsListener = new GPSListener();
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        fab_gps = (FloatingActionButton) findViewById(R.id.fab_afm);
        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        operatingAnim.setInterpolator(new LinearInterpolator());


        fab_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                fab_gps.setImageResource(R.drawable.baseline_cached_white_24dp);
                fab_gps.startAnimation(operatingAnim);

                final Double lat = gpsListener.latitude;
                final Double lon = gpsListener.longitude;
                LatLng sydney = new LatLng(lat, lon);

                mMap.clear();
                getData("http://xognsxo1491.cafe24.com/Treace_Beacon_connect.php");

                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
                mMap.animateCamera(zoom);
                fab_gps.clearAnimation();
                fab_gps.setImageResource(R.drawable.baseline_my_location_white_24dp);

                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {

                        fab_gps.clearAnimation();
                        fab_gps.setImageResource(R.drawable.baseline_my_location_white_24dp);
                        locationManager.removeUpdates(gpsListener);

                        Snackbar.make(v,"작동하지 않는다면 신호세기를 확인해주세요.",Snackbar.LENGTH_LONG).show();
                    }
                };

                timer = new Timer();
                timer.schedule(timerTask, 6500);

            }
        });
        mapFragment.getMapAsync(this);
        startLocationService();
        getData("http://xognsxo1491.cafe24.com/Treace_Beacon_connect.php");

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        final Double lat = gpsListener.latitude;
        final Double lon = gpsListener.longitude;
        LatLng sydney = new LatLng(lat, lon);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
        googleMap.animateCamera(zoom);

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
            }

            if(gpsListener.latitude == 0.0) {
                Toast.makeText(this, "위치정보 불러오기에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }

        } catch(SecurityException ex) {
            ex.printStackTrace();
        }
    }
    public void getData(String url) { // php 파싱관련

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
                System.out.println(s);
                addMaker();
            }

        }
        GetDataJSON getDataJSON = new GetDataJSON();
        getDataJSON.execute(url);
    }
    protected void addMaker() {  // php 파싱 설정
        try {
            JSONObject jsonObject = new JSONObject(myJSON);
            jsonArray = jsonObject.getJSONArray(TAG_RESULT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String UUID = object.getString(TAG_UUID);
                String major = object.getString(TAG_MAJOR);
                String minor = object.getString(TAG_MINOR);
                String time = object.getString(TAG_TIME);
                double lat = Double.parseDouble(object.getString(TAG_LATITUDE));
                double lon = Double.parseDouble(object.getString(TAG_LONGTITUDE));


                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(lat, lon)).title("발견시각 " +time);
                mMap.addMarker(markerOptions);
            }

        } catch (JSONException e) { // 오류 캐치
            e.printStackTrace();
        }
    }
}
