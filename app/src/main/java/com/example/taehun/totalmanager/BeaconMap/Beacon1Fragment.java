package com.example.taehun.totalmanager.BeaconMap;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taehun.totalmanager.Adapter.Adapter_BeaconSearch;
import com.example.taehun.totalmanager.R;

public class Beacon1Fragment extends Fragment {

    Adapter_BeaconSearch adapter;
    ListView listView;

    public Beacon1Fragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_beacon1, container, false);

        listView = view.findViewById(R.id.listview_BeaconSearch);
        final EditText editText_UUID = (EditText) view.findViewById(R.id.editText_UUID);
        final View bottomSheet = (View) view.findViewById(R.id.bottom_sheet1);

        adapter = new Adapter_BeaconSearch();

        listView.setAdapter(adapter);

        adapter.getData("http://xognsxo1491.cafe24.com/Beacon_search_connect.php"); // db 접속 url

        editText_UUID.setOnEditorActionListener(new TextView.OnEditorActionListener() { // 키보드 완료 버튼 눌렀을 시
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                    String text = editText_UUID.getText().toString();

                    if (text.equals("")) {

                        imm.hideSoftInputFromWindow(editText_UUID.getWindowToken(), 0);

                        Snackbar.make(view,"검색란이 공백입니다.",Snackbar.LENGTH_SHORT).show();

                    } else { // 아닐경우
                        imm.hideSoftInputFromWindow(editText_UUID.getWindowToken(), 0);

                        listView.setVisibility(View.VISIBLE);
                        ((Adapter_BeaconSearch) listView.getAdapter()).getFilter().filter(text);
                    }
                }
                return true;
            }
        });

        return view;
    }
}
