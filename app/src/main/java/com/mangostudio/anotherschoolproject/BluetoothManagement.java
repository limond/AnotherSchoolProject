package com.mangostudio.anotherschoolproject;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.Set;

/**
 * Created by Leon on 04.04.2015.
 */
public class BluetoothManagement {

    public static final int BLUETOOTH_NOT_PRESENT = 0;
    public static final int BLUETOOTH_NOT_ENABLED = 1;
    public static final int BLUETOOTH_ENABLED = 2;

    public BluetoothAdapter adapter;
    public BluetoothManagement(){
        adapter = BluetoothAdapter.getDefaultAdapter();
    }

    public int getAdapterStatus() {
        if(adapter == null) return BLUETOOTH_NOT_PRESENT;
        if(!adapter.isEnabled()) return BLUETOOTH_NOT_ENABLED;
        return BLUETOOTH_ENABLED;
    }

    public Set<BluetoothDevice> getPairedDevices() {
        return adapter.getBondedDevices();
    }
}
