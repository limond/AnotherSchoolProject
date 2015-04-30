package com.mangostudio.anotherschoolproject;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;

/**
 * Created by Leon on 30.04.2015.
 */
public class BluetoothServerSocketThread extends Thread{
    @Override
    public void run() {
        super.run();
        BluetoothManagement bluetooth = new BluetoothManagement();
        try {
            BluetoothServerSocket serverSocket = bluetooth.startServer();
            InterThreadCom.handleServerSocketOpened(serverSocket);
            InterThreadCom.updateServerStatus(BluetoothManagement.SERVER_CREATION_SUCCESSFULL);
            while(true){
                try {
                    BluetoothSocket socket = serverSocket.accept();
                    InterThreadCom.handleSocketOpened(socket);
                }
                catch (IOException e){
                    e.printStackTrace();
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            InterThreadCom.updateServerStatus(BluetoothManagement.SERVER_CREATION_FAILED);
        }
    }
}
