package com.example.taehun.totalmanager;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.taehun.totalmanager.Request.BeaconMissingRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class Popup2Activity extends Activity {

    Button btn;
    Button btn2;

    String strMajor;
    String strminor;
    String strUuid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup2);
        btn = findViewById(R.id.notification2_btn1);
        btn2 = findViewById(R.id.notification2_btn2);
        Intent intent = getIntent();

        strUuid = intent.getStringExtra("UUID");
        strMajor = intent.getStringExtra("Major");
        strminor = intent.getStringExtra("Minor");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("freeLogin", Context.MODE_PRIVATE); // freeLogin 이라는 키 안에 데이터 저장
                String userId = preferences.getString("Id", null);
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

                    BeaconMissingRequest board_write_request = new BeaconMissingRequest(userId, strUuid, strMajor, strminor, responseListener); // 입력 값을 넣기 위한 request 클래스 참조
                    RequestQueue queue = Volley.newRequestQueue(Popup2Activity.this);
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

}