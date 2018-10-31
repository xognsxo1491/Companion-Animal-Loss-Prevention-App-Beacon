package com.example.taehun.totalmanager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taehun.totalmanager.Adapter.Adapter_BeaconSearch;
import com.example.taehun.totalmanager.BoardRegion.BoardRegionActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class BeaconMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private static final String TAG_RESULT = "result";
    private static final String TAG_UUID = "UUID";
    private static final String TAG_MAJOR = "Major";
    private static final String TAG_Minor = "Minor";
    private static final String TAG_LATITUDE = "Latitude";
    private static final String TAG_LONGITUDE = "Longitude";

    private FusedLocationProviderClient fusedLocationProviderClient;

    FloatingActionButton fab_gps, fab_search;
    ConstraintLayout constraintLayout;
    LocationManager locationManager;
    Adapter_BeaconSearch adapter;
    MarkerOptions markerOptions;
    JSONArray jsonArray = null;
    Animation operatingAnim;
    ListView listView;
    String myJSON;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_map);

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            Toast.makeText(this, "GPS가 켜져있어야 해당 기능을 사용할 수 있습니다.", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        constraintLayout = (ConstraintLayout) findViewById(R.id.layout_map);
        constraintLayout.setTag("확장");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fab_gps = (FloatingActionButton) findViewById(R.id.fab_gps);
        fab_search = (FloatingActionButton) findViewById(R.id.fab_search);

        listView = findViewById(R.id.listview_BeaconSearch);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        operatingAnim = AnimationUtils.loadAnimation(BeaconMapActivity.this, R.anim.rotate);
        operatingAnim.setInterpolator(new LinearInterpolator());

        adapter = new Adapter_BeaconSearch();

        listView.setAdapter(adapter);
        adapter.getData("http://xognsxo1491.cafe24.com/Beacon_search_connect.php"); // db 접속 url

        final EditText editText_UUID = (EditText) findViewById(R.id.editText_UUID);

        editText_UUID.setOnEditorActionListener(new TextView.OnEditorActionListener() { // 키보드 완료 버튼 눌렀을 시
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    String text = editText_UUID.getText().toString();

                    if (text.equals("")) {

                        imm.hideSoftInputFromWindow(editText_UUID.getWindowToken(), 0);
                        Toast.makeText(BeaconMapActivity.this, "검색란이 공백입니다.", Toast.LENGTH_SHORT).show();

                    } else { // 아닐경우
                        imm.hideSoftInputFromWindow(editText_UUID.getWindowToken(), 0);

                        listView.setVisibility(View.VISIBLE);
                        ((Adapter_BeaconSearch) listView.getAdapter()).getFilter().filter(text);
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (constraintLayout.getTag().equals("확장")) {
            finish();
        }

        else if (constraintLayout.getTag().equals("축소")) {
            constraintLayout.setVisibility(View.INVISIBLE);
            constraintLayout.setTag("확장");
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        final LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        LatLng SANGMYUNG = new LatLng(36.833584, 127.179176);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(SANGMYUNG));

        CameraUpdate zoom = CameraUpdateFactory.zoomTo(8);
        googleMap.animateCamera(zoom);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String str_Lat = adapter.getBoardList().get(position).get("Latitude");
                String str_long = adapter.getBoardList().get(position).get("Longitude");

                Double lat = Double.parseDouble(str_Lat);
                Double lon = Double.parseDouble(str_long);

                LatLng latLng = new LatLng(lat, lon);
                googleMap.addMarker(new MarkerOptions().position(latLng));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(15);
                googleMap.animateCamera(cameraUpdate);

                constraintLayout.setVisibility(View.INVISIBLE);
                constraintLayout.setTag("확장");

            }
        });


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

                        fab_gps.setImageResource(R.drawable.baseline_cached_white_48dp);
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

        final Animation animation = new AlphaAnimation(0, 1);
        animation.setDuration(1000);

        fab_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (constraintLayout.getTag().equals("확장")) {
                    constraintLayout.setVisibility(View.VISIBLE);
                    constraintLayout.startAnimation(animation);
                    constraintLayout.setTag("축소");
                    return;
                }

                else if (constraintLayout.getTag().equals("축소")) {
                    constraintLayout.setVisibility(View.INVISIBLE);
                    constraintLayout.setTag("확장");
                    return;
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
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {

                        Intent intent = new Intent(getApplicationContext(), BoardRegionActivity.class);
                        startActivity(intent);
                    }
                });

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getData(String url, final GoogleMap googleMap) { // php 파싱관련

        class GetDataJSON extends AsyncTask<String, Void, String> {

            ProgressDialog progressDialog = new ProgressDialog(BeaconMapActivity.this);

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
