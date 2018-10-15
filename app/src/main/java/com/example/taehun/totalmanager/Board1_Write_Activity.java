package com.example.taehun.totalmanager;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.text.PrecomputedText;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.taehun.totalmanager.Request.Board1WriteRequest;
import com.example.taehun.totalmanager.Request.Board1WriteRequest2;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Board1_Write_Activity extends AppCompatActivity {

    private static final int INTENT_REQUEST_GET_IMAGES = 13;

    ArrayList<Uri> image_uris = new ArrayList<Uri>();
    ByteArrayOutputStream byteArrayOutputStream;
    private ViewGroup mSelectedImagesContainer;
    AlertDialog dialog;
    String ConvertImage;
    byte[] byteArray;
    Bitmap FixBitmap;
    ImageView ShowSelectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board1_write);

        mSelectedImagesContainer = (ViewGroup) findViewById(R.id.selected_photos_container);

        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.write_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("게시글 작성");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 추가
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_keyboard_backspace_white_24dp);

        byteArrayOutputStream = new ByteArrayOutputStream();

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if ( Build.VERSION.SDK_INT != Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 5); }
        }

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(Board1_Write_Activity.this, "사진은 최대 10개까지 선택가능합니다.", Toast.LENGTH_LONG).show();
                choosePhotoFormGallary();
            }
        });
    }

    private void choosePhotoFormGallary() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == this.RESULT_CANCELED) { return; }

        if (requestCode == INTENT_REQUEST_GET_IMAGES && resultCode == RESULT_OK && null != data) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_send: {  // 작성 버튼 눌렀을 시

                final EditText edit_title = (EditText) findViewById(R.id.edit_title);
                final EditText edit_content = (EditText) findViewById(R.id.edit_content);

                SharedPreferences preferences = getSharedPreferences("freeLogin", Context.MODE_PRIVATE); // freeLogin 이라는 키 안에 데이터 저장
                String userId = preferences.getString("Id", null);
                String boardTitle = edit_title.getText().toString();
                String boardContent = edit_content.getText().toString();

                String format = new String("MM/dd HH시 mm분 ss초"); // 시간 설정 포맷
                SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.KOREA);

                String boardTime = dateFormat.format(new Date()); // 시간 설정 포맷

                if (boardTitle.equals("")) { // 제목이 공백일 경우
                    AlertDialog.Builder builder = new AlertDialog.Builder(Board1_Write_Activity.this);
                    dialog = builder.setMessage("제목을 입력하지 않았습니다.")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    break;

                } else if (boardContent.equals("")) { // 내용이 공백일 경우
                    AlertDialog.Builder builder = new AlertDialog.Builder(Board1_Write_Activity.this);
                    dialog = builder.setMessage("내용을 입력하지 않았습니다.")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    break;

                } else { // 공백이 아닐 경우
                    if (ShowSelectedImage.getDrawable() == null) {
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

                        Board1WriteRequest board_write_request = new Board1WriteRequest(userId, boardTitle, boardContent, boardTime, responseListener); // 입력 값을 넣기 위한 request 클래스 참조
                        RequestQueue queue = Volley.newRequestQueue(Board1_Write_Activity.this);
                        queue.add(board_write_request);
                    }

                    else {

                        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                        byteArray = byteArrayOutputStream.toByteArray();
                        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

                        String imageName = Long.toString(System.currentTimeMillis());

                        Response.Listener<String> responseListener = new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean success = jsonObject.getBoolean("success");

                                    if (success) { // 성공일 경우
                                        Intent intent = new Intent(getApplicationContext(), Board1_Activity.class);
                                        startActivity(intent);
                                    }

                                } catch (JSONException e) { //오류 캐치
                                    e.printStackTrace();
                                }
                            }

                        };

                        Board1WriteRequest2 board_write_request = new Board1WriteRequest2(userId, boardTitle, boardContent, boardTime, ConvertImage, imageName, responseListener); // 입력 값을 넣기 위한 request 클래스 참조
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        queue.add(board_write_request);
                    }
                    break;
                }
            }

            case android.R.id.home: // 뒤로가기 버튼
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
