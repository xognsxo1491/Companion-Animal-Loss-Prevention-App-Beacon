package com.example.taehun.totalmanager.BoardRegion;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taehun.totalmanager.Adapter.Adapter_ListView;
import com.example.taehun.totalmanager.Adapter.Adapter_ListView_Region;
import com.example.taehun.totalmanager.Board_Comment_Activity;
import com.example.taehun.totalmanager.R;

public class BoardRegionSearchActivity extends AppCompatActivity {

    ListView listView;
    Adapter_ListView_Region adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_region_search);

        final EditText edit_search_result = (EditText) findViewById(R.id.edit_search_result);
        final TextView text_board1_result = (TextView) findViewById(R.id.text_board1_result);

        listView = (ListView) findViewById(R.id.listview2);

        edit_search_result.requestFocus(); // 검색창 키보드 포커스 추가
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        adapter = new Adapter_ListView_Region();
        listView.setAdapter(adapter);

        adapter.getData("http://xognsxo1491.cafe24.com/Board_Region_connect.php"); // db 접속 url

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // 리스트뷰 아이템 클릭시
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), BoardRegionCommentActivity.class);
                intent.putExtra("boardList", adapter.getBoardList().get(position));

                startActivity(intent);
            }

        });

        edit_search_result.setOnEditorActionListener(new TextView.OnEditorActionListener() { // 키보드 완료 버튼 눌렀을 시
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    String text = edit_search_result.getText().toString();

                    if (text.equals("")) // 공백일 경우
                        Toast.makeText(getApplicationContext(), "검색란이 공백입니다.", Toast.LENGTH_SHORT).show();

                    else { // 아닐경우
                        Log.d("검색어", "onEditorAction: "+ text);
                        listView.setVisibility(View.VISIBLE);
                        text_board1_result.setVisibility(View.GONE);
                        ((Adapter_ListView_Region) listView.getAdapter()).getFilter().filter(text);
                    }
                }
                return true;
            }
        });
    }
}