package com.mangostudio.anotherschoolproject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;

/**
 * Created by Leon on 03.04.2015.
 */
public class UIHandler extends Handler {
    public UIHandler(Looper l) {
        super(l);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch(msg.what){
            case InterThreadCom.BLUETOOTH_STATUS_RESPONSE:
                handleBluetoothPresent(msg);
                break;
            case InterThreadCom.BLUETOOTH_PAIRED_DEVICES_RESPONSE:
                handleBluetoothDevices(msg);
                break;
        }
    }

    private void handleBluetoothPresent(Message msg){
        Activity ctx = (Activity) msg.obj;
        switch(msg.getData().getInt("status")){
            case BluetoothManagement.BLUETOOTH_NOT_PRESENT:
                Toast.makeText(ctx,R.string.NoBluetoothWarning,Toast.LENGTH_LONG).show();
                //Beendet die App (genauer: die Aktuelle Activity), wenn kein BT-Adapter vorhanden ist
                ctx.finish();
            case BluetoothManagement.BLUETOOTH_NOT_ENABLED:
                //Fragt beim System an, den BT-Adapter-Dialog anzuzeigen (onActivityResult empfängt das Resultat)
                ctx.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), CardGames.INTENT_ENABLE_BLUETOOTH);
                break;
        }
    }

    private void handleBluetoothDevices(Message msg){
        Parcelable[] parcelables = msg.getData().getParcelableArray("bluetoothDevices");
        BluetoothDevice[] bluetoothDevices = Arrays.copyOf(parcelables, parcelables.length, BluetoothDevice[].class);
        HostListView list = (HostListView) msg.obj;
        list.setDevices(bluetoothDevices);
        Log.d("size",""+bluetoothDevices.length);
    }
}