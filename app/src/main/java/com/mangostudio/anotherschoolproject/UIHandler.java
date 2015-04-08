package com.mangostudio.anotherschoolproject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
            case InterThreadCom.BLUETOOTH_CONNECTION_START_RESPONSE:
                handleConnectionStatus(msg);
        }
    }

    private void handleConnectionStatus(Message msg) {
        Bundle data = msg.getData();
        switch(data.getInt("status")){
            case BluetoothConnection.CONNECTION_FAILED:
                Toast.makeText((Context) msg.obj,R.string.ConnectionFailed, Toast.LENGTH_LONG).show();
                break;
        }
    }
}