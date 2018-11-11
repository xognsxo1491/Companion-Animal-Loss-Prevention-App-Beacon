package com.example.taehun.totalmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.taehun.totalmanager.BeaconScan.SignUpBeaconDialog;
import com.example.taehun.totalmanager.Request.DuplicateRequest;
import com.example.taehun.totalmanager.Request.SignUpRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sign_UpActivity extends AppCompatActivity {

    EditText editname, editTextid, editTextpw, editTextcheck, editTextemail;
    TextView text_id_check, text_password;
    Boolean duplicate = false;
    AlertDialog dialog;
    String data = "";
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final Button btn_id_check = (Button) findViewById(R.id.btn_id_check);

        editname = (EditText) findViewById(R.id.edit_name);
        editTextid = (EditText) findViewById(R.id.edit_id);
        editTextpw = (EditText) findViewById(R.id.edit_password);
        editTextcheck = (EditText) findViewById(R.id.edit_password_check);
        editTextemail = (EditText) findViewById(R.id.edit_email);

        text_id_check = (TextView)findViewById(R.id.text_id_check);
        text_password = (TextView)findViewById(R.id.text_password);
        toolbar = (Toolbar) findViewById(R.id.toolbar3);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("회원가입");

        editTextid.addTextChangedListener(new TextWatcher() { // 아이디 입력에 따른 텍스트 변화
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length()>12 || s.length()<5)
                    text_id_check.setText("아이디 길이를 맞춰주세요.");

                else
                    text_id_check.setText(null);
            }
        });

        editTextpw.addTextChangedListener(new TextWatcher() { // 비밀번호 입력에 따른 텍스트 변화
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length()>15 || s.length()<7) {
                    text_password.setText("비밀번호 길이를 맞춰주세요");
                }

                else
                    text_password.setText(null);
            }
        });

        btn_id_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editTextid.getText().toString();

                if (duplicate) {
                    return;
                }

                if (id.equals("")) {
                    Snackbar.make(v,"아이디 입력란이 공백입니다.",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (id.length()>12 || id.length()<5) {
                    Snackbar.make(v,"아이디 길이를 맞춰주세요.",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if (success) {
                                text_id_check.setText("사용가능한 아이디입니다.");
                                btn_id_check.setEnabled(false);
                                btn_id_check.setText("확인 완료");
                                editTextid.setEnabled(false);
                                duplicate = true;
                            } else {
                                text_id_check.setText("사용할 수 없는 아이디입니다.");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                DuplicateRequest duplicateRequest = new DuplicateRequest(id, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Sign_UpActivity.this);
                queue.add(duplicateRequest);
            }
        });

        Button btn_submit = (Button) findViewById(R.id.btn_ok);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = editname.getText().toString();
                String password_check = editTextcheck.getText().toString();

                final String id = editTextid.getText().toString();
                final String password = editTextpw.getText().toString();
                final String emali = editTextemail.getText().toString();

                Pattern p = Pattern.compile("^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$");
                Matcher m = p.matcher(emali);

                FirebaseMessaging.getInstance().subscribeToTopic("news");
                String token = FirebaseInstanceId.getInstance().getToken();

                if (!duplicate) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Sign_UpActivity.this);
                    dialog = builder.setMessage("중복체크를 해주세요")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                if (name.equals("") || id.equals("") || password.equals("") || emali.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Sign_UpActivity.this);
                    dialog = builder.setMessage("필수항목을 채워주세요")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                if (!password.equals(password_check)) {

                    editTextcheck.setText("");
                    AlertDialog.Builder builder = new AlertDialog.Builder(Sign_UpActivity.this);
                    dialog = builder.setMessage("비밀번호 확인란을 확인해주세요.")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                if (password.length()>15 || password.length()<7) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Sign_UpActivity.this);
                    dialog = builder.setMessage("비밀번호 길이를 확인해주세요.")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                if(!m.matches()) {

                    editTextemail.setText("");
                    AlertDialog.Builder builder = new AlertDialog.Builder(Sign_UpActivity.this);
                    dialog = builder.setMessage("이메일 형식을 확인해주세요.")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if (success) {

                                SharedPreferences preferences = getSharedPreferences("freeLogin", Context.MODE_PRIVATE); // 자동 로그인 데이터 저장
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("Id", id);
                                editor.putString("Password", password);
                                editor.commit();

                                AlertDialog.Builder builder = new AlertDialog.Builder(Sign_UpActivity.this);
                                dialog = builder.setMessage("회원등록이 완료되었습니다.")
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                SignUpBeaconDialog signUpBeaconDialog = new SignUpBeaconDialog(Sign_UpActivity.this);
                                                // 커스텀 다이얼로그를 호출한다.
                                                // 커스텀 다이얼로그의 결과를 출력할 TextView를 매개변수로 같이 넘겨준다.
                                                signUpBeaconDialog.callFunction(null);
                                            }
                                        })
                                        .setCancelable(false)
                                        .create();
                                dialog.show();

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Sign_UpActivity.this);
                                dialog = builder.setMessage("회원등록에 실패하였습니다.")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                SignUpRequest sign_upRequest = new SignUpRequest(name, id, password, emali,  token, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Sign_UpActivity.this);
                queue.add(sign_upRequest);

                SharedPreferences sharedPreferences = getSharedPreferences("BeaconId", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("BeaconId",id);
                editor.commit();

            }
        });
    }

    @Override
    protected void onStop() {

        super.onStop();

        if (dialog != null)
        {
            dialog.dismiss();
            dialog = null;
        }
    }
}

