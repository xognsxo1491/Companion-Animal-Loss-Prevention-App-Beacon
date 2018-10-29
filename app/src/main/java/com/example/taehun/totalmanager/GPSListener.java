package com.example.taehun.totalmanager;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
     * 리스너 클래스 정의
     */
  public class GPSListener implements LocationListener {
        /**
         * 위치 정보가 확인될 때 자동 호출되는 메소드
         */
        double latitude, longitude;

        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }