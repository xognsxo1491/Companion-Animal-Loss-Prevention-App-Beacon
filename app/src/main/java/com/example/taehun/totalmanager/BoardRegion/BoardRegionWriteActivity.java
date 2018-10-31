package com.example.taehun.totalmanager.BoardRegion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.taehun.totalmanager.BeaconScan.CustomDialog;
import com.example.taehun.totalmanager.R;

public class BoardRegionWriteActivity extends AppCompatActivity {

    TextView text_gps;
    FloatingActionButton fab_blue, fab_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_region_write);

        Intent intent = getIntent();

        String gps = intent.getExtras().getString("GPS");
        String[] cut = gps.split(" ");

        text_gps = (TextView) findViewById(R.id.text_gps);
        text_gps.setText(cut[1] +" "+ cut[2] + " " + cut[3]+ " " + cut[4]);

        fab_blue = (FloatingActionButton) findViewById(R.id.fab_blue);
        fab_blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BoardRegionDialog boardRegionDialog = new BoardRegionDialog(getApplication());
                boardRegionDialog.callFunction(null);

            }
        });


    }
}
