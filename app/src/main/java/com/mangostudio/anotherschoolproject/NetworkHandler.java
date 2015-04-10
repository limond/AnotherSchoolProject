package com.mangostudio.anotherschoolproject;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.SocketHandler;

/**
 * Created by limond on 06.03.15.
 */
public class NetworkHandler extends Handler {
    //Setze den Messege-Handler für den UI-Thread
    public UIHandler uiHandler = new UIHandler(Looper.getMainLooper());
    public BluetoothManagement bluetooth = new BluetoothManagement();
    public ArrayList<BluetoothSocket> socketList = new ArrayList<>();

    public NetworkHandler(Looper l) {
       super(l);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch(msg.what){
            case InterThreadCom.BLUETOOTH_CONNECTION_START_REQUEST:
                Bundle data = msg.getData();
                BluetoothDevice device = data.getParcelable("device");
                try {
                    new BluetoothConnection(device);
                } catch (IOException e) {
                    e.printStackTrace();
                    InterThreadCom.updateConnectionStatus(uiHandler, (Context) msg.obj,BluetoothConnection.CONNECTION_FAILED);
                }
                break;
            case InterThreadCom.BLUETOOTH_SERVER_START_REQUEST:
                BluetoothServerSocket serverSocket;
                try {
                    serverSocket = bluetooth.startServer();
                } catch (IOException e) {
                    e.printStackTrace();
                    InterThreadCom.updateServerStatus(uiHandler, BluetoothManagement.SERVER_CREATION_FAILED, null);
                    break;
                }
                InterThreadCom.updateServerStatus(uiHandler, BluetoothManagement.SERVER_CREATION_SUCCESSFULL, serverSocket);
                try {
                    while (true) {
                        /*
                            Dieser Vorgang wird abgebrochen, indem der UI-Thread den serverSocket schließt und accept() eine Exception wirft.
                            Dieses Vorgehen ist üblich, da accept nicht auf interrupts reagiert
                         */
                        BluetoothSocket socket = serverSocket.accept();
                        socketList.add(socket);
                        InterThreadCom.newSocketOpened(uiHandler, socket, (ArrayAdapter) msg.obj);
                        Log.i("CardGames", "socket verbunden");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
