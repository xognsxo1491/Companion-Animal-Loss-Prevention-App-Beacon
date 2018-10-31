package com.example.taehun.totalmanager.BoardRegion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.Toast;

import com.example.taehun.totalmanager.BeaconMapActivity;
import com.example.taehun.totalmanager.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BoardRegionMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    LocationManager locationManager;
    FloatingActionButton fab_gps;
    MarkerOptions markerOptions;
    Animation operatingAnim;
    Geocoder geocoder;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_region_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fab_gps = (FloatingActionButton) findViewById(R.id.fab_gps);
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        operatingAnim = AnimationUtils.loadAnimation(BoardRegionMapActivity.this, R.anim.rotate);
        operatingAnim.setInterpolator(new LinearInterpolator());
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        final LocationListener mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                    final Double lat = location.getLatitude();
                    final Double lon = location.getLongitude();

                    LatLng mylocation = new LatLng(lat, lon);

                    markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(lat, lon))
                            .title("현재위치");

                    googleMap.addMarker(markerOptions);
                    googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {

                            Intent intent = new Intent(getApplicationContext(), BoardRegionWriteActivity.class);
                            intent.putExtra("lat", lat);
                            intent.putExtra("lon", lon);
                            startActivity(intent);

                        }
                    });

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
}
