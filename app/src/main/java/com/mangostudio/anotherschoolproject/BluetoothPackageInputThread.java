package com.mangostudio.anotherschoolproject;

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

    public BluetoothPackageInputThread (ObjectInputStream obInpStream, String address) {
        super();
        this.obInpStream = obInpStream;
        this.address = address;
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
                return;
            }
        }
    }
}
