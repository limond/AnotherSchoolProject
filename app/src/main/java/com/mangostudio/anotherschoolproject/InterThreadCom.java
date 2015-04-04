package com.mangostudio.anotherschoolproject;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.Set;

/**
 * Created by Leon on 03.04.2015.
 */
public class InterThreadCom {
    public final static int BLUETOOTH_STATUS_REQUEST = 1;
    public final static int BLUETOOTH_STATUS_RESPONSE = 2;
    public final static int BLUETOOTH_PAIRED_DEVICES_REQUEST = 3;
    public final static int BLUETOOTH_PAIRED_DEVICES_RESPONSE = 4;

    public static void checkBluetooth(Handler netHandler, Activity ctx){
        Message msg = netHandler.obtainMessage();
        msg.what = BLUETOOTH_STATUS_REQUEST;
        msg.obj = ctx;
        netHandler.sendMessage(msg);
    }

    public static void bluetoothPresentStatus(Handler uiHandler, Activity ctx, int status){
        Message msg = uiHandler.obtainMessage();
        msg.what = BLUETOOTH_STATUS_RESPONSE;
        Bundle data = new Bundle();
        data.putInt("status",status);
        msg.setData(data);
        msg.obj = ctx;
        uiHandler.sendMessage(msg);
    }

    public static void getPairedDevices(Handler netHandler, HostListView ctx){
        Message msg = netHandler.obtainMessage();
        msg.what = BLUETOOTH_PAIRED_DEVICES_REQUEST;
        msg.obj = ctx;
        netHandler.sendMessage(msg);
    }

    public static void sendBluetoothDevices(Handler uiHandler, HostListView ctx, Set<BluetoothDevice> devices){
        Message msg = uiHandler.obtainMessage();
        msg.what = BLUETOOTH_PAIRED_DEVICES_RESPONSE;
        Bundle data = new Bundle();
        BluetoothDevice[] bd = new BluetoothDevice[devices.size()];
        devices.toArray(bd);
        data.putParcelableArray("bluetoothDevices", bd);
        msg.setData(data);
        msg.obj = ctx;
        uiHandler.sendMessage(msg);
    }

}
