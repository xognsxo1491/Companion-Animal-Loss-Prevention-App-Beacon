package com.example.taehun.totalmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.taehun.totalmanager.BoardRegion.BoardRegionDialog;
import com.example.taehun.totalmanager.BoardRegion.BoardRegionMapActivity;
import com.example.taehun.totalmanager.Request.BeaconMissingRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Timer;
import java.util.TimerTask;

public class BeaconMissingActivity extends FragmentActivity implements OnMapReadyCallback {

    LocationManager locationManager;
    FloatingActionButton fab_gps;
    MarkerOptions markerOptions;
    String id, uuid, major, minor;
    Animation operatingAnim;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fab_gps = (FloatingActionButton) findViewById(R.id.fab_afm);
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        operatingAnim = AnimationUtils.loadAnimation(BeaconMissingActivity.this, R.anim.rotate);
        operatingAnim.setInterpolator(new LinearInterpolator());
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        Intent intent = getIntent();

        uuid = intent.getStringExtra("UUID");
        major = intent.getStringExtra("Major");
        minor = intent.getStringExtra("Minor");

        SharedPreferences preferences = getSharedPreferences("freeLogin", Context.MODE_PRIVATE); // freeLogin 이라는 키 안에 데이터 저장
        id = preferences.getString("Id", null);

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

                markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(lat, lon))
                        .title("현재위치")
                        .snippet("클릭시 위치가 선택됩니다.");

                googleMap.addMarker(markerOptions);
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(BeaconMissingActivity.this);
                        builder.setTitle("실종 위치 선택")
                                .setMessage("해당 위치로 선택하시겠습니까?");

                        builder.setPositiveButton("아니요",null);

                        builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                    }
                                };

                                BeaconMissingRequest beaconMissingRequest = new BeaconMissingRequest(id, uuid, major, minor, lat, lon, responseListener); // 입력 값을 넣기 위한 request 클래스 참조
                                RequestQueue queue = Volley.newRequestQueue(BeaconMissingActivity.this);
                                queue.add(beaconMissingRequest);

                                Toast.makeText(BeaconMissingActivity.this, "간편 실종등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });

                        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    dialog.dismiss();
                                    return true;
                                }
                                return false;
                            }
                        });
                        builder.show();
                    }
                });

                googleMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
                googleMap.animateCamera(zoom);

                fab_gps.clearAnimation();
                fab_gps.setImageResource(R.drawable.baseline_my_location_white_24dp);

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {

                        final Double lat = latLng.latitude;
                        final Double lon = latLng.longitude;

                        markerOptions.position(new LatLng(lat, lon))
                                .title("현재위치")
                                .snippet("클릭시 위치가 선택됩니다.");

                        googleMap.addMarker(markerOptions);
                        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(BeaconMissingActivity.this);
                                builder.setTitle("실종 위치 선택")
                                        .setMessage("해당 위치로 선택하시겠습니까?");

                                builder.setPositiveButton("아니요",null);

                                builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                            }
                                        };

                                        BeaconMissingRequest beaconMissingRequest = new BeaconMissingRequest(id, uuid, major, minor, lat, lon, responseListener); // 입력 값을 넣기 위한 request 클래스 참조
                                        RequestQueue queue = Volley.newRequestQueue(BeaconMissingActivity.this);
                                        queue.add(beaconMissingRequest);

                                        Toast.makeText(BeaconMissingActivity.this, "간편 실종등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });

                                builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                    @Override
                                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                                            dialog.dismiss();
                                            return true;
                                        }
                                        return false;
                                    }
                                });
                                builder.show();
                            }
                        });
                    }
                });
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
}
