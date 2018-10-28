package com.example.taehun.totalmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.taehun.totalmanager.Request.BeaconMissingRequest;

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

public class Popup3Activity extends  Activity {
    Button btn1;
    Button btn2;
    String myJSON;
    JSONArray jsonArray;
    ArrayList<BeaconListItem> missingMyBeacons= new ArrayList<>();
    private static final String TAG_UUID = "UUID";
    private static final String TAG_MAJOR = "Major";
    private static final String TAG_Minor = "Minor";
    private static final String TAG_RESULT = "result";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup3);
        btn1 = findViewById(R.id.Popup3_btn1);
        btn2 = findViewById(R.id.Popup3_btn2);
        getMissingBeaconsFromDataBase("http://xognsxo1491.cafe24.com/Beacon_connect.php");
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("freeLogin", Context.MODE_PRIVATE); // freeLogin 이라는 키 안에 데이터 저장
                String userId = preferences.getString("Id", null);
                String strUuid = missingMyBeacons.get(0).getUUID();
                String strMajor = missingMyBeacons.get(0).getMajor();
                String strminor = missingMyBeacons.get(0).getMinor();
                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success"); // php가 db 접속이 성공적일 경우 success라는 문구가 나오는데 success를 캐치

                            if (success) { // 성공일 경우
                                Intent intent = new Intent(getApplicationContext(), Board1_Activity.class);
                                startActivity(intent);
                            }

                        } catch (JSONException e) { //오류 캐치
                            e.printStackTrace();
                        }
                    }
                };

                BeaconMissingRequest board_write_request = new BeaconMissingRequest(userId, strUuid, strMajor, strminor, null, null, responseListener); // 입력 값을 넣기 위한 request 클래스 참조
                RequestQueue queue = Volley.newRequestQueue(Popup3Activity.this);
                queue.add(board_write_request);
                finish();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public void getMissingBeaconsFromDataBase(String url) { // php 파싱관련

        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                SharedPreferences preference = getSharedPreferences("freeLogin",Context.MODE_PRIVATE);
                String uri = params[0];
                BufferedReader bufferedReader = null;

                String postParameter = "Missing=true";
                String postParameter2 = "Id="+preference.getString("Id", null);
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
                    outputStream.write(postParameter2.getBytes("UTF-8"));
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
                addBeacons(missingMyBeacons);
            }

        }
        GetDataJSON getDataJSON = new GetDataJSON();
        getDataJSON.execute(url);
    }
    protected void addBeacons(ArrayList<BeaconListItem> beacons) {  // php 파싱 설정
        try {
            JSONObject jsonObject = new JSONObject(myJSON);
            jsonArray = jsonObject.getJSONArray(TAG_RESULT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String UUID = object.getString(TAG_UUID);
                String major = object.getString(TAG_MAJOR);
                String minor = object.getString(TAG_Minor);
                System.out.println("비콘 " + UUID + " " + major + " " +minor);
                beacons.add(new BeaconListItem(UUID, major, minor));

            }

        } catch (JSONException e) { // 오류 캐치
            e.printStackTrace();
        }
    }
}
