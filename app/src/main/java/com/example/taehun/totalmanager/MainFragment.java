package com.example.taehun.totalmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.taehun.totalmanager.BeaconScan.CustomDialog;
import com.example.taehun.totalmanager.BoardRegion.BoardRegionActivity;
import com.example.taehun.totalmanager.BoardRegion.FindMapsActivity;

public class MainFragment extends Fragment {

    SharedPreferences preferences2;

    public MainFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main,null);

        preferences2 = getContext().getSharedPreferences("Scan", getContext().MODE_PRIVATE);

        final Switch switch_beacon = (Switch) view.findViewById(R.id.switch_beacon);

        if (preferences2.getBoolean("Scan", false)){

            switch_beacon.setText("비콘스캔 on");
            switch_beacon.setChecked(true);

        } else {

            switch_beacon.setText("비콘스캔 off");
            switch_beacon.setChecked(false);
        }

        Button btn_board = (Button) view.findViewById(R.id.btn_board);
        Button btn_board2 = (Button) view.findViewById(R.id.btn_board2);
        Button btn_maps = (Button)  view.findViewById(R.id.btn_maps);
        Button btnFindmMaps = (Button) view.findViewById(R.id.btn_beaconMaps);
        Button btn_beacon_dialog = (Button) view.findViewById(R.id.btn_beacon_dialog);

        btn_board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), Board1_Activity.class);
                startActivity(intent);
            }
        });

        btn_board2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), BoardRegionActivity.class);
                startActivity(intent);
            }
        });

        btn_maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BeaconMapActivity.class);
                startActivity(intent);
            }
        });

        btnFindmMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FindMapsActivity.class);
                startActivity(intent);
            }
        });

        btn_beacon_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomDialog customDialog = new CustomDialog(getContext());
                // 커스텀 다이얼로그를 호출한다.
                // 커스텀 다이얼로그의 결과를 출력할 TextView를 매개변수로 같이 넘겨준다.
                customDialog.callFunction(null);
            }
        });

        switch_beacon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (switch_beacon.getText().toString().equals("비콘스캔 off")) {

                    SharedPreferences.Editor editor = preferences2.edit();
                    editor.putBoolean("Scan", true);
                    editor.commit();
                    switch_beacon.setText("비콘스캔 on");
                }
                else {

                    SharedPreferences.Editor editor = preferences2.edit();
                    editor.putBoolean("Scan", false);
                    editor.commit();
                    switch_beacon.setText("비콘스캔 off");
                }
            }
        });

        return view;
    }

}
