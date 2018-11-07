package com.example.taehun.totalmanager;

import android.content.Intent;
import android.support.v4.app.Fragment;;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.taehun.totalmanager.BeaconScan.CustomDialog;
import com.example.taehun.totalmanager.BoardRegion.BoardRegionActivity;
import com.example.taehun.totalmanager.BoardRegion.FindMapsActivity;

public class MainFragment extends Fragment {

    public MainFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main,null);

        Button btn_board = (Button) view.findViewById(R.id.btn_board);
        Button btn_board2 = (Button) view.findViewById(R.id.btn_board2);
        Button btn_beacondialog = (Button)  view.findViewById(R.id.btn_beacondialog);
        Button btn_maps = (Button)  view.findViewById(R.id.btn_maps);
        Button btnFindmMaps = (Button) view.findViewById(R.id.btn_beaconMaps);
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

        btn_beacondialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomDialog customDialog = new CustomDialog(getContext());
                customDialog.callFunction(null);
            }
        });
        btnFindmMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FindMapsActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

}
