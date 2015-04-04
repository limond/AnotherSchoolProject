package com.mangostudio.anotherschoolproject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.widget.Toast;

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

    public BluetoothDevice[] getPairedDevices() {
        Set<BluetoothDevice> set = adapter.getBondedDevices();
        BluetoothDevice[] devices = new BluetoothDevice[set.size()];
        set.toArray(devices);
        return devices;
    }

    public void checkBluetooth(Activity act) {
        int status = getAdapterStatus();
        switch(status){
            case BluetoothManagement.BLUETOOTH_NOT_PRESENT:
                Toast.makeText(act, R.string.NoBluetoothWarning, Toast.LENGTH_LONG).show();
                //Beendet die App (genauer: die Aktuelle Activity), wenn kein BT-Adapter vorhanden ist
                act.finish();
                break;
            case BluetoothManagement.BLUETOOTH_NOT_ENABLED:
                //Fragt beim System an, den BT-Adapter-Dialog anzuzeigen (onActivityResult empfängt das Resultat)
                act.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), CardGames.INTENT_ENABLE_BLUETOOTH);
                break;
        }
    }
}
