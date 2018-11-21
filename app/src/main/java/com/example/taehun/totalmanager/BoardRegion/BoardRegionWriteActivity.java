package com.example.taehun.totalmanager.BoardRegion;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.taehun.totalmanager.R;
import com.example.taehun.totalmanager.Request.BeaconMissingRequest;
import com.example.taehun.totalmanager.Request.BoardRegionRequest;
import com.example.taehun.totalmanager.Request.BoardRegionRequest2;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class BoardRegionWriteActivity extends AppCompatActivity {

    private int GALLERY = 1000;

    TextView text_gps, text_uuid, text_major, text_minor, text_form_uuid, text_form_major, text_form_minor, edit_dname, edit_dkind;
    String ConvertImage, key_uuid, key_major, key_minor;
    ByteArrayOutputStream byteArrayOutputStream;
    FloatingActionButton fab_blue, fab_image;
    HashMap<String, String> hashMap;
    ImageView showSelectedImage;
    Double lat, lon;
    AlertDialog dialog;
    Geocoder geocoder;
    byte[] byteArray;
    Bitmap FixBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_region_write);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 5);
            }
        }

        Intent intent = getIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.write_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("게시글 작성");

        lat = intent.getExtras().getDouble("lat");
        lon = intent.getExtras().getDouble("lon");

        text_gps = (TextView) findViewById(R.id.text_gps);
        text_uuid = (TextView) findViewById(R.id.text_uuid);
        text_major = (TextView) findViewById(R.id.text_major);
        text_minor = (TextView) findViewById(R.id.text_minor);

        edit_dname = (EditText) findViewById(R.id.edit_dName);
        edit_dkind = (EditText) findViewById(R.id.edit_dKind);

        text_form_uuid = (TextView) findViewById(R.id.text_form_uuid);
        text_form_major = (TextView) findViewById(R.id.text_form_major);
        text_form_minor = (TextView) findViewById(R.id.text_form_minor);

        byteArrayOutputStream = new ByteArrayOutputStream();

        geocoder = new Geocoder(getApplicationContext());
        List<Address> list = null;

        try {
            list = geocoder.getFromLocation(lat, lon, 10);

            String gps = list.get(0).toString();
            String[] cut = gps.split(" ");

            text_gps.setText(cut[1] + " " + cut[2] + " " + cut[3] + " " + cut[4]);

        } catch (IOException e) {
            e.printStackTrace();
        }

        hashMap = (HashMap<String, String>) intent.getSerializableExtra("boardList");

        Iterator<String> iterator = hashMap.keySet().iterator(); // 파싱값 별로 데이터 저장
        while (iterator.hasNext()) {

            final String key = (String) iterator.next();

            if (key.equals("UUID")) {
                text_uuid.setText(hashMap.get(key));

                if(hashMap.get(key).equals("")) {

                    text_uuid.setVisibility(View.GONE);
                    text_form_uuid.setVisibility(View.GONE);
                }
            }

            if (key.equals("Major")) {
                text_major.setText(hashMap.get(key));

                if (hashMap.get(key).equals("")) {

                    text_major.setVisibility(View.GONE);
                    text_form_major.setVisibility(View.GONE);
                }
            }

            if (key.equals("Minor")) {
                text_minor.setText(hashMap.get(key));

                if (hashMap.get(key).equals("")) {

                    text_minor.setVisibility(View.GONE);
                    text_form_minor.setVisibility(View.GONE);

                }
            }
        }

        showSelectedImage = (ImageView) findViewById(R.id.imageView1);

        fab_image = (FloatingActionButton) findViewById(R.id.fab_image);
        fab_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                choosePhotoFormGallary();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), BoardRegionActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.layout_left_in, R.anim.layout_right_out);
    }

    private void choosePhotoFormGallary() {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == this.RESULT_CANCELED) { return; }

        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();

                try {
                    FixBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    showSelectedImage.setImageBitmap(FixBitmap);
                    showSelectedImage.setVisibility(View.VISIBLE);

                } catch (IOException e) {

                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }
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

                Double now = (Math.random() *1000000000);

                String uuid = text_uuid.getText().toString();
                String major = text_major.getText().toString();
                String minor = text_minor.getText().toString();
                String str_lat = String.valueOf(lat);
                String str_lon = String.valueOf(lon);
                String boarddName = edit_dname.getText().toString();
                String boarddKind = edit_dkind.getText().toString();
                String missing = "1";
                String Region = null;
                String Region_name = text_gps.getText().toString();
                String str_now = String.valueOf(now);

                if (Region_name.contains("서울")) { Region = "1"; }
                else if (Region_name.contains("부산")) { Region = "2"; }
                else if (Region_name.contains("강원")) { Region = "3"; }
                else if (Region_name.contains("경기")) { Region = "4"; }
                else if (Region_name.contains("인천")) { Region = "5"; }
                else if (Region_name.contains("경상")) { Region = "6"; }
                else if (Region_name.contains("전라")) { Region = "7"; }
                else if (Region_name.contains("충청")) { Region = "8"; }
                else if (Region_name.contains("제주")) { Region = "9"; }

                SharedPreferences preferences = getSharedPreferences("freeLogin", Context.MODE_PRIVATE); // freeLogin 이라는 키 안에 데이터 저장
                String userId = preferences.getString("Id", null);
                String boardTitle = edit_title.getText().toString();
                String boardContent = edit_content.getText().toString();

                String format = new String("MM/dd HH시 mm분 ss초"); // 시간 설정 포맷
                SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.KOREA);

                String boardTime = dateFormat.format(new Date()); // 시간 설정 포맷

                if (boardTitle.equals("")) { // 제목이 공백일 경우
                    AlertDialog.Builder builder = new AlertDialog.Builder(BoardRegionWriteActivity.this);
                    dialog = builder.setMessage("제목을 입력하지 않았습니다.")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    break;

                } else if (boardContent.equals("")) { // 내용이 공백일 경우
                    AlertDialog.Builder builder = new AlertDialog.Builder(BoardRegionWriteActivity.this);
                    dialog = builder.setMessage("내용을 입력하지 않았습니다.")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    break;

                } else if (boarddName.equals("")) { // 내용이 공백일 경우
                    AlertDialog.Builder builder = new AlertDialog.Builder(BoardRegionWriteActivity.this);
                    dialog = builder.setMessage("반려동물 이름을 입력하지 않았습니다.")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    break;

                } else if (boarddKind.equals("")) { // 내용이 공백일 경우
                    AlertDialog.Builder builder = new AlertDialog.Builder(BoardRegionWriteActivity.this);
                    dialog = builder.setMessage("반려동물 종류을 입력하지 않았습니다.")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    break;

                } else { // 공백이 아닐 경우
                    if (showSelectedImage.getDrawable() == null) {
                        Response.Listener<String> responseListener = new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean success = jsonObject.getBoolean("success"); // php가 db 접속이 성공적일 경우 success라는 문구가 나오는데 success를 캐치

                                    if (success) { // 성공일 경우
                                        Intent intent = new Intent(getApplicationContext(), BoardRegionActivity.class);
                                        startActivity(intent);
                                    }

                                } catch (JSONException e) { //오류 캐치
                                    e.printStackTrace();
                                }
                            }
                        };

                        BoardRegionRequest boardRegionRequest = new BoardRegionRequest(userId, uuid, major, minor, str_lat, str_lon, missing, Region, Region_name, boardTitle, boardContent, boardTime, str_now, responseListener); // 입력 값을 넣기 위한 request 클래스 참조
                        RequestQueue queue = Volley.newRequestQueue(BoardRegionWriteActivity.this);
                        queue.add(boardRegionRequest);

                        if (!uuid.equals("")) {
                            BeaconMissingRequest beaconMissingRequest = new BeaconMissingRequest(userId, uuid, major, minor, lat, lon, responseListener); // 입력 값을 넣기 위한 request 클래스 참조
                            queue.add(beaconMissingRequest);
                        }
                    }

                    else {

                        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                        byteArray = byteArrayOutputStream.toByteArray();
                        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

                        Response.Listener<String> responseListener = new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean success = jsonObject.getBoolean("success");

                                    if (success) { // 성공일 경우
                                        Intent intent = new Intent(getApplicationContext(),  BoardRegionActivity.class);
                                        startActivity(intent);
                                    }

                                } catch (JSONException e) { //오류 캐치
                                    e.printStackTrace();
                                }
                            }

                        };

                        BoardRegionRequest2 boardRegionRequest2 = new BoardRegionRequest2(userId, uuid, major, minor, str_lat, str_lon, missing, Region, Region_name, boardTitle, boardContent, boardTime, str_now, ConvertImage, str_now, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        queue.add(boardRegionRequest2);
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