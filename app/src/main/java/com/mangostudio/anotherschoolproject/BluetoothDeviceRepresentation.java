package com.mangostudio.anotherschoolproject;

import java.io.Serializable;

/**
 * Created by Leon on 29.04.2015.
 */
public class BluetoothDeviceRepresentation implements Serializable{
    /*
        Eine minimalistische Klasse, die die Informationen Adresse und Name eines Bluetooth-Gerätes bündelt
        und serialisierbar ist
     */
    public String name;
    public String address;

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public BluetoothDeviceRepresentation(String name, String address){
        this.name = name;
        this.address = address;
    }
}
