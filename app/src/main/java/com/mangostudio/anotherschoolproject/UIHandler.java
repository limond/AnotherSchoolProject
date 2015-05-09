package com.mangostudio.anotherschoolproject;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Leon on 03.04.2015.
 */
public class UIHandler extends Handler {

    /*
        Der UIHandler bearbeitet die Nachrichten aus der MessegeQueue des Main-Threads
     */

    public UIHandler(Looper l) {
        super(l);
    }

    //Je nachdem, um was in der Nachricht aus der InterThreadCom geht, wird das Paket entsprechend behandelt
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
            case InterThreadCom.BLUETOOTH_SERVER_STOPPED_STATUS:
                handleStopServerStatus(msg);
                break;
        }
    }

    //Behandelt BluetoothPackeges, nachdem sie von der InterThreadCom zum UIThread geschickt wurden
    private void handleInputPackage(Message msg) {
        Bundle data = msg.getData();
        BluetoothPackage pkg = (BluetoothPackage) data.getSerializable("package");
        switch(pkg.getAction()){
            case BluetoothPackage.ACTION_START_CLIENT_GAME:
                //Die eigentliche Spiel-Activity wird gestartet und das Paket (mit der Liste verbundener Geräte) übergeben
                Activity act = CardGamesApplication.getCurrentActivity();
                if(act == null) break;
                Intent gameWaitIntent = new Intent(act, GameClientActivity.class);
                gameWaitIntent.putExtra("startPackage",pkg);
                act.startActivity(gameWaitIntent);
                act.finish();
                break;
        }
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
                //Oeffne die GameWaitActivity und uebergib das Geraet mit aktivem Socket
                Context ctx = CardGamesApplication.getCurrentActivity();
                if(ctx == null) break;
                Intent gameWaitIntent = new Intent(ctx, GameWaitActivity.class);
                gameWaitIntent.putExtra("device",(BluetoothDevice) data.getParcelable("device"));
                ctx.startActivity(gameWaitIntent);
                break;
        }
        try {
            Activity connectAct = CardGamesApplication.getCurrentActivity();
            View hostsListView = connectAct.findViewById(R.id.hostsListView);
            if(hostsListView != null) hostsListView.setEnabled(true);
        }
        catch(IllegalStateException e){
            e.printStackTrace();
        }
    }

    //Gibt als Nachricht aus, ob der Server gestartet werden konnte
    public void handleStopServerStatus(Message msg){
        Toast.makeText(CardGamesApplication.getContext(),msg.getData().getInt("status"), Toast.LENGTH_LONG).show();
    }
}