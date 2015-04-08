package com.mangostudio.anotherschoolproject;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Leon on 08.04.2015.
 */
public interface OnDeviceSelectionListener {
    //Listener wird benutzt, um das Auswählen eines Gerätes aus der Liste bekannt zu machen
    void onSelect(BluetoothDevice selectedDevice);
}
