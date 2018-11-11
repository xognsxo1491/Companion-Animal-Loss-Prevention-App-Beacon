package com.example.taehun.totalmanager;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.estimote.internal_plugins_api.scanning.BluetoothScanner;
import com.example.taehun.totalmanager.Request.BeaconMissingRequest;
import com.example.taehun.totalmanager.Request.TokenChangeRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import org.altbeacon.beacon.BeaconManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_RESULT = "result";
    private BottomNavigationView bottomNavigationView;
    private MainFragment mainFragment;
    private Sub2Fragment sub2Fragment;
    private long time= 0;

    BluetoothAdapter bluetoothAdapter;
    String login_id,login_password;
    String TAG_TOKEN = "Token";
    JSONArray jsonArray;
    String myJSON;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View view = (View) findViewById(R.id.activity_main);

        LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED||
                checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA} , 1);
        }

        /* if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            Toast.makeText(this, "GPS를 켜주셔야 원활한 서비스 이용이 가능합니다.", Toast.LENGTH_SHORT).show();
            Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsIntent);
        }  */

        if (!bluetoothAdapter.isEnabled()) {
            Snackbar.make(view,"원활한 서비스를 위해 블루투스가 자동으로 실행되었습니다.",Snackbar.LENGTH_LONG).show();
            bluetoothAdapter.enable();
        }

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.main_nav);

        mainFragment = new MainFragment(); // 메인 엑티비티 안의 프레그먼트 설정
        sub2Fragment = new Sub2Fragment();

        setFragment(mainFragment); // 앱 접속했을 때 나오는 프레그먼트

        SharedPreferences preferences = getSharedPreferences("freeLogin", Context.MODE_PRIVATE); // 자동 로그인 데이터 저장
        final SharedPreferences.Editor editor = preferences.edit();

        login_id = preferences.getString("Id","");
        login_password = preferences.getString("Password","");
        token = FirebaseInstanceId.getInstance().getToken();

        if(!login_id.equals("")&&!login_password.equals("")) { getTokenFromDataBase("http://xognsxo1491.cafe24.com/Token_connect.php"); }

        System.out.println("Token 1 " + token);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) { // 메인 액티비티 밑의 네비게이터 버튼
                    case R.id.navigation_home:
                        setFragment(mainFragment);
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
    public void getTokenFromDataBase(String url) { // php 파싱관련

        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                SharedPreferences preference = getSharedPreferences("freeLogin",Context.MODE_PRIVATE);
                String uri = params[0];
                BufferedReader bufferedReader = null;

                String postParameter = "Id="+ preference.getString("Id","");
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
                try {
                    JSONObject jsonObject = new JSONObject(myJSON);
                    jsonArray = jsonObject.getJSONArray(TAG_RESULT);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String tokenFromDatabase = object.getString(TAG_TOKEN);
                        if(!tokenFromDatabase.equals(token)){
                            Log.d("토큰", "변경");
                            changeToken();
                        }
                    }

                } catch (JSONException e) { // 오류 캐치
                    e.printStackTrace();
                }
                changeToken();
            }
        }
        GetDataJSON getDataJSON = new GetDataJSON();
        getDataJSON.execute(url);
    }
    public void changeToken(){
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
//                                Log.d("토큰", "변경성공");
//                            }
//
//                        } catch (JSONException e) { //오류 캐치
//                            e.printStackTrace();
//                        }
                //방법이 없어서 일단 주석처리
            }
        };

        TokenChangeRequest tokenChangeRequest = new TokenChangeRequest(userId, token, responseListener); // 입력 값을 넣기 위한 request 클래스 참조
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(tokenChangeRequest);
    }
}