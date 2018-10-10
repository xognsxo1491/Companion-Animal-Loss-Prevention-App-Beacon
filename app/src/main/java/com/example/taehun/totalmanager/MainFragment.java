package com.example.taehun.totalmanager;

import android.content.Intent;
import android.support.v4.app.Fragment;;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.taehun.totalmanager.BeaconDetect.DetectBeaconActivity;
import com.example.taehun.totalmanager.BeaconMap.BeacomMapActivity;

public class MainFragment extends Fragment {

    public MainFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main,null);

        Button btn_board = (Button) view.findViewById(R.id.btn_board);
        Button btn_beaconList = (Button)  view.findViewById(R.id.btn_beaconList);
        Button btn_maps = (Button)  view.findViewById(R.id.btn_maps);
        Button btn_DetectBeacon = (Button)  view.findViewById(R.id.btn_DetectBeacon);

        btn_board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), Board1_Activity.class);
                startActivity(intent);
            }
        });

        btn_beaconList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BeaconListActivity.class);
                startActivity(intent);
            }
        });

        btn_maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BeacomMapActivity.class);
                startActivity(intent);
            }
        });

        btn_DetectBeacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DetectBeaconActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

}
