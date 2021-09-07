package com.example.passengerapp;

import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Handler;

public class BluetoothThread extends Thread{

    @Override
    public void run(){
        //Find BLE devices
        bluetoothLeScanner.startScan(leScanCallback);


    }

    /**
     * Find BLE devices
     */
    private BluetoothLeScanner bluetoothLeScanner = SingletonDevices.get().getBluetoothAdapter().getBluetoothLeScanner();
    //private LeDeviceListAdapter leDeviceListAdapter = new LeDeviceListAdapter();

    // Device scan callback.
    private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    //leDeviceListAdapter.addDevice(result.getDevice());
                    //leDeviceListAdapter.notifyDataSetChanged();
                }
            };

}
