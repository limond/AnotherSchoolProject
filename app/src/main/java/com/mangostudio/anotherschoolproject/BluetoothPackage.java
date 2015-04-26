package com.mangostudio.anotherschoolproject;

import java.io.Serializable;

/**
 * Created by Leon on 19.04.2015.
 */
public class BluetoothPackage implements Serializable {
    public static final int HANDLER_DESTINATION_UI = 1;
    public static final int HANDLER_DESTINATION_NETWORK = 2;
    public int destination;
    BluetoothPackage(int destination){
        //legt fest, von welchem Handler das Paket behandelt werden soll
        this.destination = destination;
    }
}
