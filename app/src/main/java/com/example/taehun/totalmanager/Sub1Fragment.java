package com.example.taehun.totalmanager;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.taehun.totalmanager.BeaconScan.CustomDialog;
import com.example.taehun.totalmanager.BeaconScan.CustomDialog2;

public class Sub1Fragment extends Fragment {

    Button btn_beacon_dialog;

    public Sub1Fragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_sub1, container, false);

        btn_beacon_dialog = view.findViewById(R.id.btn_beacon_dialog);

        btn_beacon_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomDialog customDialog  = new CustomDialog(getContext());
                // 커스텀 다이얼로그를 호출한다.
                // 커스텀 다이얼로그의 결과를 출력할 TextView를 매개변수로 같이 넘겨준다.
                customDialog.callFunction(null);
            }
        });

        return view;
    }
}

