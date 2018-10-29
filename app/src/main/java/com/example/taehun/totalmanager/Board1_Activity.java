package com.example.taehun.totalmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Board1_Activity extends AppCompatActivity {

    private static final String TAG_ID = "Id";
    private static final String TAG_TiTIE = "Title";
    private static final String TAG_CONTENT = "Content";
    private static final String TAG_TIME = "Time";
    private static final String TAG_RESULT = "result";
    private static final String TAG_NUMBER = "Number";
    private static final String TAG_IMAGE_PATH = "Image_path";
    private static final String TAG_IMAGE_NAME = "Image_name";

    ArrayList<HashMap<String, String>> boardList;

    String myJSON;
    JSONArray jsonArray = null;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board1);

        boardList = new ArrayList<HashMap<String, String>>();

        listView = (ListView) findViewById(R.id.image_comment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.board1_toolbar); // 툴바 설정

        getData("http://xognsxo1491.cafe24.com/Board1_list_connect.php"); // 카페24 db 접속 url

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("자유게시판");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 활성화
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_keyboard_backspace_white_24dp);

    }

    @Override
    public void onBackPressed() { // 뒤로가기 버튼 설정
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.layout_left_in, R.anim.layout_right_out);
    }

    protected void showList() {  // php 파싱 설정
        try {
            JSONObject jsonObject = new JSONObject(myJSON);
            jsonArray = jsonObject.getJSONArray(TAG_RESULT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String number = object.getString(TAG_NUMBER);
                String Id = object.getString(TAG_ID);
                String Title = object.getString(TAG_TiTIE);
                String Content = object.getString(TAG_CONTENT);
                String Time = object.getString(TAG_TIME);
                String Image_Path = object.getString(TAG_IMAGE_PATH);
                String Image_Name = object.getString(TAG_IMAGE_NAME);

                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put(TAG_NUMBER, number);
                hashMap.put(TAG_ID, Id);
                hashMap.put(TAG_TiTIE, Title);
                hashMap.put(TAG_CONTENT, Content);
                hashMap.put(TAG_TIME, Time);
                hashMap.put(TAG_IMAGE_PATH, Image_Path);
                hashMap.put(TAG_IMAGE_NAME, Image_Name);

                boardList.add(hashMap);
            }

            final BaseAdapter adapter = new SimpleAdapter(Board1_Activity.this, boardList, R.layout.listview_item, new String[]{TAG_ID, TAG_TiTIE, TAG_CONTENT, TAG_TIME},
                    new int[]{R.id.text_board_id, R.id.text_board_title, R.id.text_board_content, R.id.text_board_time}); // 리스트뷰의 어댑터 속성

            adapter.getCount();

            listView.setAdapter(adapter); // 리스트뷰 어댑터 셋팅

            final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.board1_swipe);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(true);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            boardList.clear();
                            getData("http://xognsxo1491.cafe24.com/Board1_list_connect.php");
                            adapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);

                        }
                    }, 500);
                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // 리스트뷰 안의 아이템 클릭시
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), Board_Comment_Activity.class);
                    intent.putExtra("boardList", boardList.get(position));

                    startActivity(intent);
                }
            });

        } catch (JSONException e) { // 오류 캐치
            e.printStackTrace();
        }
    }

    public void getData(String url) { // php 파싱관련

        class GetDataJSON extends AsyncTask<String, Void, String> {

            ProgressDialog progressDialog = new ProgressDialog(Board1_Activity.this);

            @Override
            protected void onPreExecute() {
                progressDialog.setMessage("불러오는 중입니다.");
                progressDialog.show();
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) { // url 추출
                progressDialog.dismiss();
                myJSON = s;
                showList();
            }

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                BufferedReader bufferedReader = null;

                try {
                    URL url = new URL(uri);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    StringBuilder builder = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        builder.append(json + "\n");
                    }
                    return builder.toString().trim();

                } catch (Exception e) {
                    return null;
                }
            }
        }
        GetDataJSON getDataJSON = new GetDataJSON();
        getDataJSON.execute(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // 툴바에 메뉴 아이템 생성
        getMenuInflater().inflate(R.menu.write, menu);
        getMenuInflater().inflate(R.menu.search, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // 메뉴 아이템 클릭시

        switch (item.getItemId()) {

            case R.id.nav_write: {

                Intent intent = new Intent(getApplicationContext(), Board1_Write_Activity.class);
                startActivity(intent);
                break;
            }

            case R.id.nav_search: {

                Intent intent = new Intent(getApplicationContext(), Board1_Search_Activity.class);
                startActivity(intent);
                break;
            }

            case android.R.id.home: {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.layout_left_in, R.anim.layout_right_out);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}



