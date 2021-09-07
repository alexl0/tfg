package com.example.passengerapp;

import android.bluetooth.BluetoothAdapter;

/**
 * The purpose of this class is to store the devices so other classes can easily access them
 */
public class SingletonDevices {

    /**
     * Properties that singleton stores:
     */
    BluetoothAdapter bluetoothAdapter;

    //property created only one time
    private static SingletonDevices mSingletonDevices;

    //Only this class can instantiate itself
    private SingletonDevices(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    //The other classes use this get method
    public static SingletonDevices get(){
        if(mSingletonDevices==null)
            mSingletonDevices = new SingletonDevices();
        return mSingletonDevices;
    }

    public BluetoothAdapter getBluetoothAdapter(){
        return this.bluetoothAdapter;
    }

}
