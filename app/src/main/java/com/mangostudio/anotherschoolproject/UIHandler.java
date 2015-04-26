package com.mangostudio.anotherschoolproject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Leon on 03.04.2015.
 */
public class UIHandler extends Handler {
    private BluetoothServerSocket currentServerSocket;
    public UIHandler(Looper l) {
        super(l);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch(msg.what){
            case InterThreadCom.BLUETOOTH_CONNECTION_START_RESPONSE:
                handleConnectionStatus(msg);
                break;
            case InterThreadCom.BLUETOOTH_SERVER_STATUS_RESPONSE:
                handleStatusResponse(msg);
                break;
            case InterThreadCom.BLUETOOTH_HANDLE_INPUT_PACKAGE:
                handleInputPackage(msg);
                break;
            //case InterThreadCom.
        }
    }

    private void handleInputPackage(Message msg) {
    }

    //Gibt als Nachricht aus, ob der Server gestartet werden konnte
    private void handleStatusResponse(Message msg) {
        Bundle data = msg.getData();
        switch(data.getInt("status")){
            case BluetoothManagement.SERVER_CREATION_FAILED:
                Toast.makeText(CardGamesApplication.getContext(),R.string.ServerCreationFailed, Toast.LENGTH_LONG).show();
                break;
            case BluetoothManagement.SERVER_CREATION_SUCCESSFULL:
                Toast.makeText(CardGamesApplication.getContext(),R.string.ServerCreationSuccessfull, Toast.LENGTH_LONG).show();
                currentServerSocket = (BluetoothServerSocket) msg.obj;
                break;
        }
    }

    //Gibt als Nachricht aus, wenn keine Verbindung zum Host aufgebaut werden konnte
    private void handleConnectionStatus(Message msg) {
        Bundle data = msg.getData();
        switch(data.getInt("status")){
            case BluetoothManagement.CONNECTION_FAILED:
                Toast.makeText(CardGamesApplication.getContext(),R.string.ConnectionFailed, Toast.LENGTH_LONG).show();
                break;
            case BluetoothManagement.CONNECTION_SUCCESSFULL:
                //Oeffne die GameClientActivity und uebergib das Geraet mit aktivem Socket
                Context ctx = CardGamesApplication.getContext();
                Intent gameClientIntent = new Intent(ctx, GameClientActivity.class);
                gameClientIntent.putExtra("device",(BluetoothDevice) data.getParcelable("device"));
                //Ermoeglicht das Oeffnen von Activities aussserhalb des Activity-Kontextes
                gameClientIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(gameClientIntent);
                break;
        }
    }

    //Gibt als Nachricht aus, ob der Server gestartet werden konnte
    public void stopServer(){
        if (currentServerSocket == null) return;
        try {
            currentServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(CardGamesApplication.getContext(),R.string.ServerStopFailed, Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(CardGamesApplication.getContext(),R.string.ServerStopSuccessfully, Toast.LENGTH_LONG).show();
    }
}