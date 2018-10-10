package com.example.taehun.totalmanager.BeaconMap;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.taehun.totalmanager.Adapter.Adapter_BeaconSearch;
import com.example.taehun.totalmanager.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;


public class BeacomMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private BottomSheetBehavior bottomSheetBehavior;
    private BottomNavigationView bottomNavigationView;
    private FusedLocationProviderClient fusedLocationProviderClient;

    ListView listView;
    Adapter_BeaconSearch adapter;

    String beaconUUID, beaconMajor, beaconMinor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beaconmap);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SharedPreferences preferences = getSharedPreferences("BeaconInfo", MODE_PRIVATE);
        preferences.getString("BeaconUUID", beaconUUID);
        preferences.getString("BeaconMajor", beaconMajor);
        preferences.getString("BeaconMinor", beaconMinor);

        String[] items = { beaconUUID };

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.beacon_navi);


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

        View bottomSheet = (View) findViewById(R.id.bottom_sheet1);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheet.setNestedScrollingEnabled(false);

        bottomSheetBehavior.setPeekHeight(160);
        bottomSheetBehavior.setHideable(false);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
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

        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        googleMap.animateCamera(zoom);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION , Manifest.permission.ACCESS_FINE_LOCATION} , 1);
        }

        else {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        }

        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {

            @Override
            public boolean onMyLocationButtonClick() {

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(gpsIntent);
                } else {

                }
                return true;
            }


        });
    }

}
