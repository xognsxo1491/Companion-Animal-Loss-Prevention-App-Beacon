package com.example.taehun.totalmanager.BeaconScan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.IntentCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.taehun.totalmanager.Adapter.Adapter_Dialog;
import com.example.taehun.totalmanager.MainActivity;
import com.example.taehun.totalmanager.MainFragment;
import com.example.taehun.totalmanager.Popup3Activity;
import com.example.taehun.totalmanager.Popup4Activity;
import com.example.taehun.totalmanager.R;
import com.example.taehun.totalmanager.Request.BeaconWriteRequest;
import com.example.taehun.totalmanager.Sign_InActivity;

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

public class RegistBeaconDialog extends Activity implements BeaconConsumer{

    private  ArrayList<BeaconItem> listViewBeacon = new ArrayList<>();
    private List<Beacon> beaconList = new ArrayList<>();
    private BeaconManager beaconManager;
    private ListView listView;
    private Context context;
    SharedPreferences preferences, sharedPreferences;// 자동 로그인 데이터 저장
    SharedPreferences.Editor editor;
    FloatingActionButton fab_scan;
    Adapter_Dialog adapterDialog;
    Animation operatingAnim;
    ImageView image_cach;

    public RegistBeaconDialog(Context context) {
        this.context = context;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction(final TextView main_label) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.dialog_beacon_scan);

        // 커스텀 다이얼로그를 노출한다..
        beaconManager = BeaconManager.getInstanceForApplication(context);
        dlg.show();
        preferences = context.getSharedPreferences("Beacon",getApplicationContext().MODE_PRIVATE);
        editor = preferences.edit();
        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        listView = (ListView) dlg.findViewById(R.id.beacon_list);
        image_cach = (ImageView) dlg.findViewById(R.id.image_cach);

        operatingAnim = AnimationUtils.loadAnimation(context, R.anim.rotate);
        operatingAnim.setInterpolator(new LinearInterpolator());

        adapterDialog = new Adapter_Dialog(context, listViewBeacon);
        listView.setAdapter(adapterDialog);

        fab_scan = (FloatingActionButton) dlg.findViewById(R.id.fab_scan);
        fab_scan.setTag("실행");
        dlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                editor.putBoolean("BeaconTreaceOn", true);
                editor.commit();
            }
        });
        fab_scan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!bluetoothAdapter.isEnabled()) {

                    Toast.makeText(context, "스캔을 위해 블루투스를 켜주세요.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                    getApplicationContext().startActivity(intent);
                }

                else {

                    if (fab_scan.getTag().equals("실행")) {

                        final Animation animation = new AlphaAnimation(1, 0);
                        animation.setDuration(1000);

                        fab_scan.setTag("중단");
                        fab_scan.setAnimation(animation);

                        image_cach.setAnimation(operatingAnim);
                        image_cach.setVisibility(View.VISIBLE);

                        fab_scan.setVisibility(View.INVISIBLE);

                        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215, i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
                        beaconManager.bind(RegistBeaconDialog.this);

                    }
                }

                handler.sendEmptyMessage(0);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(final AdapterView<?> adapterView, final View view, int i, long l) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        final AlertDialog dialog;

                        builder.setPositiveButton("취소", null);

                        final String uuid = adapterDialog.getItem(i).getUuid();
                        final String major = adapterDialog.getItem(i).getMajor();
                        final String minor = adapterDialog.getItem(i).getMinor();

                        dialog = builder.setMessage("해당 비콘을 등록하시겠습니까?")
                                .setNegativeButton("확인", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        SharedPreferences preferences = context.getSharedPreferences("freeLogin", Context.MODE_PRIVATE); // freeLogin 이라는 키 안에 데이터 저장
                                        final String userId = preferences.getString("Id", null);

                                        Response.Listener<String> responseListener = new Response.Listener<String>() {

                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject jsonObject = new JSONObject(response);

                                                    boolean success = jsonObject.getBoolean("success"); // php가 db 접속이 성공적일 경우 success라는 문구가 나오는데 success를 캐치

                                                    if (success) { // 성공일 경우

                                                        Intent intent = new Intent(context, Popup4Activity.class);
                                                        intent.putExtra("id",userId);
                                                        intent.putExtra("uuid",uuid);
                                                        intent.putExtra("major",major);
                                                        intent.putExtra("minor",minor);
                                                        context.startActivity(intent);

                                                    }else {
                                                        Toast.makeText(context, "이미 등록된 비콘입니다.", Toast.LENGTH_SHORT).show();
                                                    }

                                                } catch (JSONException e) { //오류 캐치
                                                    e.printStackTrace();
                                                }
                                            }
                                        };

                                        BeaconWriteRequest beaconWriteRequest = new BeaconWriteRequest(userId, uuid, major, minor, responseListener); // 입력 값을 넣기 위한 request 클래스 참조
                                        RequestQueue queue = Volley.newRequestQueue(context);
                                        queue.add(beaconWriteRequest);
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
                adapterDialog.addItem(beacon.getId1().toString(), beacon.getId2().toString(), beacon.getId3().toString(),  String.format("%.3f", beacon.getDistance()));
            }

            adapterDialog.notifyDataSetChanged();
            handler.sendEmptyMessageDelayed(0, 1000);
        }
    };
}
