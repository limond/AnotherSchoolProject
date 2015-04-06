package com.mangostudio.anotherschoolproject;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Leon on 06.04.2015.
 */
public interface OnNewDeviceListener {
    //Listener wird benutzt, um bekannt zu machen, dass ein neues BT-Ger�t gefunden wurde
    void onNewDevice(BluetoothDevice device);
}
