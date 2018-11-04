package com.example.taehun.totalmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.taehun.totalmanager.Request.SignInRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Sign_InActivity extends AppCompatActivity {

    String login_id,login_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        final EditText user_id = (EditText) findViewById(R.id.text_id);
        final EditText user_password = (EditText) findViewById(R.id.text_password);

        final Button btn_sign_in = (Button) findViewById(R.id.btn_sign_in);
        final Button btn_sign_up = (Button) findViewById(R.id.btn_sign_up);
        Button btn_search  = (Button)findViewById(R.id.btn_search);

        SharedPreferences preferences = getSharedPreferences("freeLogin",Context.MODE_PRIVATE); // 자동 로그인 데이터 저장
        final SharedPreferences.Editor editor = preferences.edit();

        login_id = preferences.getString("Id","");
        login_password = preferences.getString("Password","");
        String token = FirebaseInstanceId.getInstance().getToken();


        btn_sign_in.setOnClickListener(new View.OnClickListener() {  // 로그인 버튼 클릭시
            @Override
            public void onClick(View v) {

                final String userid = user_id.getText().toString();
                final String userpw = user_password.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if (success) { // 일치했을 경우
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                editor.putString("Id", userid);
                                editor.putString("Password",userpw);
                                editor.commit();
                                startActivity(intent);
                            }

                            else { // 일치하지 않은 경우
                                AlertDialog.Builder builder = new AlertDialog.Builder(Sign_InActivity.this);
                                builder.setMessage("아이디 또는 비밀번호가 \n일치하지 않습니다.")
                                        .setNegativeButton("재시도", null)
                                        .create()
                                        .show();
                                user_password.setText("");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                SignInRequest sign_inRequest = new SignInRequest(userid,userpw, responseListener); // 데이터베이스 연동
                RequestQueue queue = Volley.newRequestQueue(Sign_InActivity.this);
                queue.add(sign_inRequest);
            }
        });

        btn_sign_up.setOnClickListener(new View.OnClickListener() { // 회원가입 버튼
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Sign_UpActivity.class);
                startActivity(intent);
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {  // 아이디/비밀번호 찾기
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() { // 뒤로가기 버튼 설정
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home: { //마이페이지

                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
