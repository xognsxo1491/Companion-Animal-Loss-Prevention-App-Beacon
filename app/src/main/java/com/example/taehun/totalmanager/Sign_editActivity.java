package com.example.taehun.totalmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.taehun.totalmanager.Request.SignEditRequest;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sign_editActivity extends AppCompatActivity {

    EditText edit_password, edit_password_check, edit_email;
    String str_password, str_password_check, str_email;
    TextView text_id,text_password;
    Dialog dialog;
    Button btn_ok;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_edit);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        edit_password = (EditText) findViewById(R.id.edit_password);
        edit_password_check = (EditText) findViewById(R.id.edit_password_check);
        edit_email = (EditText) findViewById(R.id.edit_email);

        text_id = (TextView) findViewById(R.id.text_id);
        text_password = (TextView) findViewById(R.id.text_password);

        btn_ok = (Button) findViewById(R.id.btn_ok);

        text_id.setText(id);

        edit_password.addTextChangedListener(new TextWatcher() { // 비밀번호 입력에 따른 텍스트 변화
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

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                str_password = edit_password.getText().toString();
                str_password_check = edit_password_check.getText().toString();
                str_email = edit_email.getText().toString();

                Pattern p = Pattern.compile("^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$");
                Matcher m = p.matcher(str_email);

                if (str_password.equals("") || str_password_check.equals("") || str_email.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Sign_editActivity.this);
                    dialog = builder.setMessage("필수항목을 채워주세요")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                if (str_password.length()>15 || str_password.length()<7) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Sign_editActivity.this);
                    dialog = builder.setMessage("비밀번호 길이를 확인해주세요.")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                if (!str_password.equals(str_password_check)) {

                    edit_password_check.setText("");
                    AlertDialog.Builder builder = new AlertDialog.Builder(Sign_editActivity.this);
                    dialog = builder.setMessage("비밀번호 확인란을 확인해주세요.")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                if(!m.matches()) {

                    edit_email.setText("");
                    AlertDialog.Builder builder = new AlertDialog.Builder(Sign_editActivity.this);
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(Sign_editActivity.this);
                                dialog = builder.setMessage("회원정보 수정이 완료되었습니다.")
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        })
                                        .setCancelable(false)
                                        .create();
                                dialog.show();

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Sign_editActivity.this);
                                dialog = builder.setMessage("회원정보 수정에 실패하였습니다.")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                                Toast.makeText(Sign_editActivity.this, id, Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                SignEditRequest signEditRequest = new SignEditRequest(id, str_password, str_email, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Sign_editActivity.this);
                queue.add(signEditRequest);
            }
        });
    }
}
