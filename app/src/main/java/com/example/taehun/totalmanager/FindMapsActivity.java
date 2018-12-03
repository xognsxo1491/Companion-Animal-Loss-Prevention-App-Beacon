package com.example.taehun.totalmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

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
import java.util.Timer;
import java.util.TimerTask;

public class FindMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG_UUID = "UUID";
    private static final String TAG_MAJOR = "Major";
    private static final String TAG_MINOR = "Minor";
    private static final String TAG_RESULT = "result";
    private static final String TAG_TIME = "Time";
    private static final String TAG_LATITUDE = "Latitude";
    private static final String TAG_LONGTITUDE = "Longtidue";

    GoogleMap mMap;

    LocationManager locationManager;
    FloatingActionButton fab_gps;
    MarkerOptions markerOptions;
    Animation operatingAnim;
    JSONArray jsonArray;
    String myJSON;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fab_gps = (FloatingActionButton) findViewById(R.id.fab_afm);
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        operatingAnim = AnimationUtils.loadAnimation(FindMapsActivity.this, R.anim.rotate);
        operatingAnim.setInterpolator(new LinearInterpolator());

        getData("http://xognsxo1491.cafe24.com/Treace_Beacon_connect.php");

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;

        try {
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastLocation != null) {

                Double lat = lastLocation.getLatitude();
                Double lon = lastLocation.getLongitude();

                LatLng latLng = new LatLng(lat,lon);

                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                CameraUpdate zoom = CameraUpdateFactory.zoomTo(7);
                googleMap.animateCamera(zoom);
            } else {

                LatLng latLng = new LatLng(36.397201, 127.852390);

                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                CameraUpdate zoom = CameraUpdateFactory.zoomTo(7);
                googleMap.animateCamera(zoom);

            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }

        final LocationListener mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {

                final Double lat = location.getLatitude();
                final Double lon = location.getLongitude();

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

        fab_gps.setTag("실행");
        fab_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(gpsIntent);

                } else {

                    getData("http://xognsxo1491.cafe24.com/Treace_Beacon_connect.php");

                    if (fab_gps.getTag().equals("실행")) {

                        fab_gps.setImageResource(R.drawable.baseline_cached_white_24dp);
                        fab_gps.startAnimation(operatingAnim);

                        try {

                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, mLocationListener);
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);

                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }

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

    public void getData(String url) { // php 파싱관련

            class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                SharedPreferences preference2 = getSharedPreferences("freeLogin",Context.MODE_PRIVATE);
                String uri = params[0];

                BufferedReader bufferedReader = null;

                String postParameter = "Id="+ preference2.getString("Id","");

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

    protected void addMaker() {

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
                    markerOptions.position(new LatLng(lat, lon)).title("발견 시간 ").snippet(time);

                    mMap.addMarker(markerOptions);

            }

        } catch (JSONException e) { // 오류 캐치
            e.printStackTrace();
        }
    }
}
