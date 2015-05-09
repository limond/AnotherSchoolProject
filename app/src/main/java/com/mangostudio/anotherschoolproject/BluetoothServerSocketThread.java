package com.mangostudio.anotherschoolproject;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;

/**
 * Created by Leon on 30.04.2015.
 */
public class BluetoothServerSocketThread extends Thread{
    /*
        Die Klasse öffnet einen BluetoothServerSocket und nimmt solange Verbindungen an, bis der NetzwerkThread den BluetoothServerSocket schließt.
     */
    @Override
    public void run() {
        super.run();
        BluetoothManagement bluetooth = new BluetoothManagement();
        BluetoothServerSocket serverSocket;

        //Es wird versucht den BluetoothServerSocket zu öffnen. Über den Erfolg wird der UI-Thread benachrichtigt
        try {
            serverSocket = bluetooth.startServer();
            InterThreadCom.handleServerSocketOpened(serverSocket);
            InterThreadCom.updateServerStatus(BluetoothManagement.SERVER_CREATION_SUCCESSFULL);

        } catch (IOException e) {
            e.printStackTrace();
            InterThreadCom.updateServerStatus(BluetoothManagement.SERVER_CREATION_FAILED);
            return;
        }

        //Es werden immer wieder Verbindungen aufgenommen
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
    }
}
