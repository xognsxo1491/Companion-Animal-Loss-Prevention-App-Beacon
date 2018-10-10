package com.example.taehun.totalmanager.Adapter;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.example.taehun.totalmanager.R;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Device extends BaseAdapter {

    private Context context;
    private List<BleDevice> bleDeviceList;

    public Adapter_Device(Context context) {
        this.context = context;
        bleDeviceList = new ArrayList<>();
    }

    public void addDevice(BleDevice bleDevice) {
        removeDevice(bleDevice);
        bleDeviceList.add(bleDevice);
    }

    public void removeDevice(BleDevice bleDevice) {
        for (int i = 0; i < bleDeviceList.size(); i++) {
            BleDevice device = bleDeviceList.get(i);
            if (bleDevice.getKey().equals(device.getKey())) {
                bleDeviceList.remove(i);
            }
        }
    }

    public void clearConnectedDevice() {
        for (int i = 0; i < bleDeviceList.size(); i++) {
            BleDevice device = bleDeviceList.get(i);
            if (BleManager.getInstance().isConnected(device)) {
                bleDeviceList.remove(i);
            }
        }
    }

    public void clearScanDevice() {
        for (int i = 0; i < bleDeviceList.size(); i++) {
            BleDevice device = bleDeviceList.get(i);
            if (!BleManager.getInstance().isConnected(device)) {
                bleDeviceList.remove(i);
            }
        }
    }

    public void clear() {
        clearConnectedDevice();
        clearScanDevice();
    }

    @Override
    public int getCount() {
        return bleDeviceList.size();
    }

    @Override
    public BleDevice getItem(int position) {
        if (position > bleDeviceList.size())
            return null;
        return bleDeviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {

            convertView = View.inflate(context, R.layout.adapter_device, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.img_blue = (ImageView) convertView.findViewById(R.id.img_blue);
            holder.txt_name = (TextView) convertView.findViewById(R.id.txt_name);
            holder.txt_mac = (TextView) convertView.findViewById(R.id.txt_mac);
            holder.txt_rssi = (TextView) convertView.findViewById(R.id.txt_rssi);

            holder.layout_idle = (LinearLayout) convertView.findViewById(R.id.layout_idle);
        }

        final BleDevice bleDevice = getItem(position);
        if (bleDevice != null) {

            boolean isConnected = BleManager.getInstance().isConnected(bleDevice);

            String name = bleDevice.getName();
            String mac = bleDevice.getMac();

            int rssi = bleDevice.getRssi();

            holder.txt_name.setText(name);
            holder.txt_mac.setText(mac);
            holder.txt_rssi.setText(String.valueOf(rssi));

            if (isConnected) {
                holder.img_blue.setImageResource(R.drawable.baseline_bluetooth_connected_black_24dp);
                holder.txt_name.setTextColor(0xFF1DE9B6);
                holder.txt_mac.setTextColor(0xFF1DE9B6);
                holder.layout_idle.setVisibility(View.GONE);

            } else {
                holder.img_blue.setImageResource(R.drawable.baseline_bluetooth_black_24dp);
                holder.txt_name.setTextColor(0xFF000000);
                holder.txt_mac.setTextColor(0xFF000000);
                holder.layout_idle.setVisibility(View.VISIBLE);

            }
        }

        return convertView;
    }

    class ViewHolder {
        ImageView img_blue;
        TextView txt_name;
        TextView txt_mac;
        TextView txt_rssi;
        LinearLayout layout_idle;
    }

    public interface OnDeviceClickListener {
        void onConnect(BleDevice bleDevice);

        void onDisConnect(BleDevice bleDevice);

        void onDetail(BleDevice bleDevice);
    }

    private OnDeviceClickListener mListener;

    public void setOnDeviceClickListener(OnDeviceClickListener listener) {
        this.mListener = listener;
    }

}