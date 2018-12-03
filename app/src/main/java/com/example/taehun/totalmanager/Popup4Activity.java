package com.example.taehun.totalmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.taehun.totalmanager.BoardRegion.BoardRegionWriteActivity;
import com.example.taehun.totalmanager.Request.BeaconNickNameRequest;
import com.example.taehun.totalmanager.Request.Board1WriteRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class Popup4Activity extends AppCompatActivity {

    private Context context;

    SharedPreferences sharedPreferences;
    String id, uuid, major, minor;
    EditText edit_nickname;
    Button btn_ok;
    Dialog dlg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dlg = new Dialog(this);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.activity_popup4);
        // 커스텀 다이얼로그를 노출한다..
        dlg.show();

        dlg.setCancelable(false);

        Intent intent = getIntent();

        id = intent.getStringExtra("id");
        uuid = intent.getStringExtra("uuid");
        major = intent.getStringExtra("major");
        minor = intent.getStringExtra("minor");
        edit_nickname = (EditText) dlg.findViewById(R.id.edit_nickname);

        btn_ok = (Button) dlg.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nickname = edit_nickname.getText().toString();

                if (nickname.equals(""))
                    Toast.makeText(context, "입력란이 공백입니다.", Toast.LENGTH_SHORT).show();

                else {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success"); // php가 db 접속이 성공적일 경우 success라는 문구가 나오는데 success를 캐치

                                if (success) { // 성공일 경우
                                    Toast.makeText(Popup4Activity.this, "비콘등록이 완료되었습니다.", Toast.LENGTH_LONG).show();
                                    dlg.dismiss();
                                    finish();
                                }

                            } catch (JSONException e) { //오류 캐치
                                e.printStackTrace();
                            }
                        }
                    };

                    BeaconNickNameRequest beaconNickNameRequest = new BeaconNickNameRequest(id, uuid, major, minor, nickname, responseListener); // 입력 값을 넣기 위한 request 클래스 참조
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    queue.add(beaconNickNameRequest);
                }
            }
        });
    }
}
