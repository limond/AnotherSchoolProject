package com.mangostudio.anotherschoolproject;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Message;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by Leon on 19.04.2015.
 *
 * Dieser Thread wird den ObjectInputString so lange lesen und Packages �ber die InterThreadCom-Klasse an den NetHandler schicken,
 * bis der Thread geschlossen wird
 */
public class BluetoothPackageInputThread extends Thread{
    private ObjectInputStream obInpStream;
    private String address;
    private BluetoothDevice device;

    public BluetoothPackageInputThread (ObjectInputStream obInpStream, BluetoothDevice device) {
        super();
        this.obInpStream = obInpStream;
        this.address = device.getAddress();
        this.device = device;
    }

    @Override
    public void run(){
        super.run();
        //Schicke solange Nachrichten an den netHandler, bis der Thread unterbrochen wird
        while (true){
            try {
                BluetoothPackage btPackage = (BluetoothPackage) obInpStream.readObject();
                InterThreadCom.handleInputPackage(btPackage, address);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                InterThreadCom.handleSocketClosed(address);
                /*
                    Normalerweise schickt das System nach ca. 10 Sekunden Trennung einen Broadcast, um getrennte Geräte mitzuteilen.
                    Hier wird ein Broadcast implementiert, der schneller sein soll, weil er gesendet wird, sobald der Socket schließt.
                    Hier wird auf die InterThreadCom-Klasse verzichtet, um den Broadcast gleichzeitig mit den Systemmeldungen behandeln zu können.
                 */
                Intent socketClosedIntent = new Intent(NetworkHandler.ACTION_SOCKET_CLOSED);
                socketClosedIntent.putExtra(BluetoothDevice.EXTRA_DEVICE, this.device);
                CardGamesApplication.getContext().sendBroadcast(socketClosedIntent);
                return;
            }
        }
    }
}
