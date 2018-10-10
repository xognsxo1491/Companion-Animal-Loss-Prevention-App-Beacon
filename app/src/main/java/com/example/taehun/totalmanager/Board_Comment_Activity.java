package com.example.taehun.totalmanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.taehun.totalmanager.Adapter.Adapter_BoardComment;
import com.example.taehun.totalmanager.Request.Board1CommentWriteRequest;
import com.example.taehun.totalmanager.Request.Board1DeleteRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import cz.msebera.android.httpclient.client.cache.Resource;
import cz.msebera.android.httpclient.client.utils.URIUtils;

public class Board_Comment_Activity extends AppCompatActivity {
    AlertDialog dialog;
    HashMap<String, String> hashMap;
    SharedPreferences preferences;
    EditText edit_comment_content;

    ArrayList<BoardCommentItem> commentItemList;
    Adapter_BoardComment boardCommentAdapter;
    ImageView imageView;
    JSONArray jsonArray;
    String myJSON, id;

    private static final String TAG_ID = "Id";
    private static final String TAG_CONTENT = "Content";
    private static final String TAG_TIME = "Time";
    private static final String TAG_RESULT = "result";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_comment);

        View header = getLayoutInflater().inflate(R.layout.listview_header, null, false);

        edit_comment_content = (EditText) findViewById(R.id.comentText);
        ListView listView = (ListView) findViewById(R.id.comment_list);

        listView.addHeaderView(header);

        TextView text_comment_id = (TextView) header.findViewById(R.id.text_comment_id);
        TextView text_comment_time = (TextView) header.findViewById(R.id.text_comment_time);
        TextView text_comment_title = (TextView) header.findViewById(R.id.text_comment_title);
        TextView text_comment_content = (TextView) header.findViewById(R.id.text_comment_content);

        imageView = (ImageView) header.findViewById(R.id.image_comment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_comment);

        commentItemList = new ArrayList<>();
        boardCommentAdapter = new Adapter_BoardComment(getApplicationContext(), commentItemList);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("게시글 보기");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_keyboard_backspace_white_24dp);

        preferences = getSharedPreferences("freeLogin", Context.MODE_PRIVATE); // 자동 로그인 데이터 저장

        Intent intent = getIntent(); // boardlist 라는 키 안에 저장된 데이터 값 불러오기

        hashMap = (HashMap<String, String>) intent.getSerializableExtra("boardList");

        Iterator<String> iterator = hashMap.keySet().iterator(); // 파싱값 별로 데이터 저장
        while (iterator.hasNext()) {
            final String key = (String) iterator.next();
            if (key.equals("Id")) {
                text_comment_id.setText(hashMap.get(key));
                String UserId = preferences.getString("Id", "");
                if (hashMap.get("Id").equals(UserId)) {
                    //findViewById(R.id.nav_delete).setBackgroundResource(R.drawable.baseline_delete_white_24dp);
                    //기본이미지를 비워놓고 글쓴이의 아이디와 현재 접속중인 아이디가 같을 경우 이미지를 넣으려 했는데 findVIewBYid를 못찾음
                }
            }

            if (key.equals("Time"))
                text_comment_time.setText(hashMap.get(key));

            if (key.equals("Title"))
                text_comment_title.setText(hashMap.get(key));

            if (key.equals("Content"))
                text_comment_content.setText(hashMap.get(key));

            if (key.equals("Image_path")) {
                if (hashMap.get(key).isEmpty()) {
                    imageView.setVisibility(View.GONE);
                    break;
                }

                else {
                    new DownloadImageTask((ImageView) imageView)
                            .execute(hashMap.get(key));
                }
            }
        }
        getData("http://xognsxo1491.cafe24.com/Board1_coment_connect.php");

        listView.setAdapter(boardCommentAdapter); // 리스트뷰 어댑터 셋팅
        setListViewHeightBasedOnChildren(listView);

        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // 삭제 버튼 추가
        getMenuInflater().inflate(R.menu.delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()){ // 삭제 버튼 눌렀을 시
            case R.id.nav_delete: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String UserId = preferences.getString("Id", "");

                        if (hashMap.get("Id").equals(UserId)) {

                            Response.Listener<String> responseListener = new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);

                                        boolean success = jsonObject.getBoolean("success"); // php가 db 접속이 성공적일 경우 success라는 문구가 나오는데 success를 캐치

                                        if (success) { // 성공일 경우
                                            Intent intent = new Intent(getApplicationContext(), Board1_Activity.class);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.layout_left_in, R.anim.layout_right_out);
                                        }

                                    } catch (JSONException e) { //오류 캐치
                                        e.printStackTrace();
                                    }
                                }
                            };

                            String userId = hashMap.get("Id");
                            String number = hashMap.get("Number");

                            Board1DeleteRequest board_delete_request = new Board1DeleteRequest(userId, number, responseListener); // 입력 값을 넣기 위한 request 클래스 참조
                            RequestQueue queue = Volley.newRequestQueue(Board_Comment_Activity.this);
                            queue.add(board_delete_request);
                        } else{


                            //아이콘을 나타났다 사라졌다 하게 하는 대신 차선책으로 안내메세지를 띄움. 향후 수정예정
                            Toast.makeText(Board_Comment_Activity.this, "글쓴이가 아닙니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog = builder.setMessage("정말 게시물을 삭제하시겠습니까?")
                        .setPositiveButton("취소", null)
                        .create();
                dialog.show();

                break;
            }

            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void commentButtonOnclick(View view) {
        if(edit_comment_content.getText().toString().equals("")) {
            Toast.makeText(this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
        }

        else{
            Response.Listener<String> responseListener = new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        boolean success = jsonObject.getBoolean("success"); // php가 db 접속이 성공적일 경우 success라는 문구가 나오는데 success를 캐치

                        if (success) { // 성공일 경우
                            finish();
                            startActivity(getIntent());
                            overridePendingTransition(0,0);
                        }

                    } catch (JSONException e) { //오류 캐치
                        e.printStackTrace();
                    }
                }
            };
            String number = hashMap.get("Number");
            String content = edit_comment_content.getText().toString();
            String id = preferences.getString("Id", "");
            String format = new String("MM/dd HH시 mm분 s초"); // 시간 설정 포맷

            SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.KOREA);
            String commentTime = dateFormat.format(new Date()); // 시간 설정 포맷

            Board1CommentWriteRequest board_comment_request = new Board1CommentWriteRequest(number, id, content, commentTime, responseListener, FirebaseInstanceId.getInstance().getToken()); // 입력 값을 넣기 위한 request 클래스 참조
            RequestQueue queue = Volley.newRequestQueue(Board_Comment_Activity.this);
            queue.add(board_comment_request);

            getData("http://xognsxo1491.cafe24.com/Board1_coment_connect.php");
        }

    }

    protected void showList() {  // php 파싱 설정
        try {
            JSONObject jsonObject = new JSONObject(myJSON);
            jsonArray = jsonObject.getJSONArray(TAG_RESULT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String id = object.getString(TAG_ID);
                String content = object.getString(TAG_CONTENT);
                String time = object.getString(TAG_TIME);

                boardCommentAdapter.addItem(id, content, time);
                boardCommentAdapter.notifyDataSetChanged();
            }

        } catch (JSONException e) { // 오류 캐치
            e.printStackTrace();
        }
    }

    public void getData(String url) { // php 파싱관련

        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                BufferedReader bufferedReader = null;

                String postParameter = "Number="+ hashMap.get("Number");
                Log.d("Number", hashMap.get("Number"));

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
                showList();
            }

        }
        GetDataJSON getDataJSON = new GetDataJSON();
        getDataJSON.execute(url);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            //listItem.measure(0, 0);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight;
        listView.setLayoutParams(params);

        listView.requestLayout();
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        ProgressDialog progressDialog = new ProgressDialog(Board_Comment_Activity.this);

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("불러오는 중입니다.");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            progressDialog.dismiss();
            bmImage.setImageBitmap(result);
            Log.d("이미지변경", "실행");
        }
    }
}
