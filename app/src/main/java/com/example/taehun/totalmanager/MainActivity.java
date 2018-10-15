package com.example.taehun.totalmanager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private MainFragment mainFragment;
    private Sub1Fragment sub1Fragment;
    private Sub2Fragment sub2Fragment;
    private long time= 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED||
                checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA} , 1);
        }

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.main_nav);

        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("위피펫");

        mainFragment = new MainFragment(); // 메인 엑티비티 안의 프레그먼트 설정
        sub1Fragment = new Sub1Fragment();
        sub2Fragment = new Sub2Fragment();

        setFragment(mainFragment); // 앱 접속했을 때 나오는 프레그먼트

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) { // 메인 액티비티 밑의 네비게이터 버튼
                    case R.id.navigation_home:
                        setFragment(mainFragment);
                        return true;

                    case R.id.navigation_dashboard:
                        setFragment(sub1Fragment);
                        return true;

                    case R.id.navigation_notifications:
                        setFragment(sub2Fragment);
                        return true;
                }
                return false;
            }
        });
    }

    private void setFragment(Fragment fragment) { // 프레그먼트 설정

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

        }
        return super.onOptionsItemSelected(item);
    }

    @Override // 뒤로가기 버튼 2번 클릭시 종료
    public void onBackPressed(){
        if(System.currentTimeMillis()-time>=2000){
            time=System.currentTimeMillis();
            Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 종료합니다.",Toast.LENGTH_SHORT).show();
        }else if(System.currentTimeMillis()-time<2000){
            finishAffinity();
        }
    }
}