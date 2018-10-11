package com.example.taehun.totalmanager.BeaconScan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.taehun.totalmanager.R;
import com.example.taehun.totalmanager.Request.BeaconWriteRequest;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomDialog implements BeaconConsumer{
    private BeaconManager beaconManager;
    private List<Beacon> beaconList = new ArrayList<>();
    private Context context;
    private  ArrayList<BeaconItem> listViewBeacon = new ArrayList<>();
    private ListView listView;
    DialogAdapter dialogAdapter;
    public CustomDialog(Context context) {
        this.context = context;
    }

    public BluetoothAdapter bluetoothAdapter;

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction(final TextView main_label) {


        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.fragment_dialog);

        // 커스텀 다이얼로그를 노출한다..
        beaconManager = BeaconManager.getInstanceForApplication(context);
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        listView = (ListView) dlg.findViewById(R.id.beacon_list);
        dialogAdapter = new DialogAdapter(context, listViewBeacon);
        listView.setAdapter(dialogAdapter);

        final Animation operatingAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        operatingAnim.setInterpolator(new LinearInterpolator());

        final FloatingActionButton fab_scan = (FloatingActionButton) dlg.findViewById(R.id.fab_scan);
        fab_scan.setTag("실행");

        fab_scan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (fab_scan.getTag().equals("실행")) {

                    fab_scan.setTag("중단");
                    fab_scan.setImageResource(R.drawable.baseline_cached_white_24dp);
                    fab_scan.setAnimation(operatingAnim);

                    beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215, i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
                    beaconManager.bind(CustomDialog.this);

                }

                else if(fab_scan.getTag().equals("중단")) {

                    fab_scan.setTag("실행");
                    fab_scan.setImageResource(R.drawable.baseline_play_arrow_white_24dp);
                    fab_scan.clearAnimation();

                    beaconManager.unbind(CustomDialog.this);
                }

                handler.sendEmptyMessage(0);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(final AdapterView<?> adapterView, View view, int i, long l) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        AlertDialog dialog;

                        builder.setPositiveButton("취소", null);

                        final String uuid = dialogAdapter.getItem(i).getUuid();
                        final String major = dialogAdapter.getItem(i).getMajor();
                        final String minor = dialogAdapter.getItem(i).getMajor();

                        dialog = builder.setMessage("해당 비콘을 등록하시겠습니까?")
                                .setNegativeButton("확인", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        SharedPreferences preferences = context.getSharedPreferences("freeLogin", Context.MODE_PRIVATE); // freeLogin 이라는 키 안에 데이터 저장
                                        String userId = preferences.getString("Id", null);

                                        Response.Listener<String> responseListener = new Response.Listener<String>() {

                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject jsonObject = new JSONObject(response);

                                                    boolean success = jsonObject.getBoolean("success"); // php가 db 접속이 성공적일 경우 success라는 문구가 나오는데 success를 캐치

                                                    if (success) { // 성공일 경우
                                                        Toast.makeText(context, "비콘 등록에 성공하였습니다", Toast.LENGTH_SHORT).show();
                                                    }else {
                                                        Toast.makeText(context, "비콘 등록에 실패하였습니다", Toast.LENGTH_SHORT).show();
                                                    }

                                                } catch (JSONException e) { //오류 캐치
                                                    e.printStackTrace();
                                                }
                                            }
                                        };

                                        BeaconWriteRequest board_delete_request = new BeaconWriteRequest(userId, uuid, responseListener); // 입력 값을 넣기 위한 request 클래스 참조
                                        RequestQueue queue = Volley.newRequestQueue(context);
                                        queue.add(board_delete_request);
                                    }
                                })
                                .create();
                        dialog.show();
                    }
                });

            }
        });

    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {

                if (collection.size() > 0) {

                    beaconList.clear();
                    for (Beacon beacon : collection) {
                        beaconList.add(beacon);
                    }
                }
            }
        });
        try{

            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        }catch(RemoteException e){}
    }

    @Override
    public Context getApplicationContext() {
        return context;
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {

    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return false;
    }

    Handler handler = new Handler(){
        public void handleMessage(Message msg){

            listViewBeacon.clear();

            for(Beacon beacon : beaconList){
                dialogAdapter.addItem(beacon.getId1().toString(), beacon.getId2().toString(), beacon.getId3().toString(),  String.format("%.3f", beacon.getDistance()));
            }

            dialogAdapter.notifyDataSetChanged();
            handler.sendEmptyMessageDelayed(0, 1000);
        }
    };
}
