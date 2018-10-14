package com.example.taehun.totalmanager.BeaconMap;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.telephony.SignalStrength;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ListView;
import android.widget.Toast;

import com.example.taehun.totalmanager.Adapter.Adapter_BeaconSearch;
import com.example.taehun.totalmanager.Board1_Activity;
import com.example.taehun.totalmanager.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.location.LocationServices;
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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class BeacomMapActivity extends FragmentActivity implements OnMapReadyCallback{

    private static final String TAG_RESULT = "result";
    private static final String TAG_UUID = "UUID";
    private static final String TAG_MAJOR = "Major";
    private static final String TAG_Minor = "Minor";
    private static final String TAG_LATITUDE = "Latitude";
    private static final String TAG_LONGITUDE = "Longitude";

    private BottomSheetBehavior bottomSheetBehavior;
    private BottomNavigationView bottomNavigationView;
    private FusedLocationProviderClient fusedLocationProviderClient;

    LocationManager locationManager;
    FloatingActionButton fab_gps;
    MarkerOptions markerOptions;
    JSONArray jsonArray = null;
    Animation operatingAnim;
    String myJSON;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beaconmap);

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            Toast.makeText(this, "GPS가 켜져있어야 해당 기능을 사용할 수 있습니다.", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.beacon_navi);
        fab_gps = (FloatingActionButton) findViewById(R.id.fab_gps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final Beacon1Fragment beacon1Fragment = new Beacon1Fragment();
        final Beacon2Fragment beacon2Fragment = new Beacon2Fragment();

        setFragment(beacon1Fragment); // 앱 접속했을 때 나오는 프레그먼트

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) { // 메인 액티비티 밑의 네비게이터 버튼
                    case R.id.navigation_home:
                        setFragment(beacon1Fragment);
                        return true;

                    case R.id.navigation_dashboard:
                        setFragment(beacon2Fragment);
                        return true;

                }
                return false;
            }
        });

        operatingAnim = AnimationUtils.loadAnimation(BeacomMapActivity.this, R.anim.rotate);
        operatingAnim.setInterpolator(new LinearInterpolator());

        View bottomSheet = (View) findViewById(R.id.bottom_sheet1);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheet.setNestedScrollingEnabled(false);

        bottomSheetBehavior.setPeekHeight(160);
        bottomSheetBehavior.setHideable(false);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) { }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) { }

        });
    }

    private void setFragment(Fragment fragment) { // 프레그먼트 설정

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.beacon_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        final LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        LatLng SANGMYUNG = new LatLng(36.833584, 127.179176);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(SANGMYUNG));

        CameraUpdate zoom = CameraUpdateFactory.zoomTo(8);
        googleMap.animateCamera(zoom);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION , Manifest.permission.ACCESS_FINE_LOCATION} , 1);
        }

        getData("http://xognsxo1491.cafe24.com/Beacon_search_connect.php", googleMap);

        fab_gps.setTag("실행");

        final LocationListener mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Double lat = location.getLatitude();
                Double lon = location.getLongitude();

                LatLng mylocation = new LatLng(lat, lon);

                googleMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
                googleMap.animateCamera(zoom);

                fab_gps.clearAnimation();
                fab_gps.setImageResource(R.drawable.baseline_my_location_white_24dp);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }

            @Override
            public void onProviderEnabled(String provider) { }

            @Override
            public void onProviderDisabled(String provider) { }

        };

        fab_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {


                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(gpsIntent);

                } else {

                    if (fab_gps.getTag().equals("실행")) {

                        fab_gps.setImageResource(R.drawable.baseline_cached_white_24dp);
                        fab_gps.startAnimation(operatingAnim);

                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, mLocationListener);
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);

                            TimerTask timerTask = new TimerTask() {
                                @Override
                                public void run() {

                                        fab_gps.clearAnimation();
                                        fab_gps.setImageResource(R.drawable.baseline_my_location_white_24dp);
                                        locationManager.removeUpdates(mLocationListener);

                                        Snackbar.make(v,"작동하지 않는다면 신호세기를 확인해주세요.",Snackbar.LENGTH_LONG).show();
                                    }
                            };

                            timer = new Timer();
                            timer.schedule(timerTask, 6500);

                    }
                }
            }
        });
    }


    protected void showList(GoogleMap googleMap) {  // php 파싱 설정
        try {
            JSONObject jsonObject = new JSONObject(myJSON);
            jsonArray = jsonObject.getJSONArray(TAG_RESULT);

            HashMap<String, String> hashMap = new HashMap<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                String uuid = object.getString(TAG_UUID);
                String major = object.getString(TAG_MAJOR);
                String minor  = object.getString(TAG_Minor);
                String str_lat = object.getString(TAG_LATITUDE);
                String str_long = object.getString(TAG_LONGITUDE);

                Double lat =  Double.parseDouble(str_lat);
                Double lon = Double.parseDouble(str_long);

                markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(lat, lon))
                        .title(uuid)
                        .snippet("Major : "+ major + "  Minor : " +minor);

                googleMap.addMarker(markerOptions);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getData(String url, final GoogleMap googleMap) { // php 파싱관련

        class GetDataJSON extends AsyncTask<String, Void, String> {

            ProgressDialog progressDialog = new ProgressDialog(BeacomMapActivity.this);

            @Override
            protected void onPreExecute() {
                progressDialog.setMessage("불러오는 중입니다.");
                progressDialog.show();
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) { // url 추출
                progressDialog.dismiss();
                myJSON = s;
                showList(googleMap);
            }

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                BufferedReader bufferedReader = null;

                try {
                    URL url = new URL(uri);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    StringBuilder builder = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        builder.append(json + "\n");
                    }
                    return builder.toString().trim();

                } catch (Exception e) {
                    return null;
                }
            }
        }
        GetDataJSON getDataJSON = new GetDataJSON();
        getDataJSON.execute(url);
    }
}
