package com.mangostudio.anotherschoolproject;

import android.os.Bundle;
import android.os.Parcel;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Leon on 19.04.2015.
 */
public class BluetoothPackage implements Serializable {
    public static final int HANDLER_DESTINATION_UI = 1;
    public static final int HANDLER_DESTINATION_NETWORK = 2;

    public static final int ACTION_START_CLIENT_GAME = 1;

    public int destination;
    public int action;
    public HashMap additionalData;

    BluetoothPackage(int destination, int action){
        //legt fest, von welchem Handler das Paket behandelt werden soll
        this.destination = destination;
        //legt die Aktion fest, die beim Ausführen passieren soll.
        this.action = action;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getDestination() {
        return destination;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }

    /*
        Ermöglicht es zusätzliche Daten anzuhängen
        Alle Objekte (keys und values) in der HashMap müssen Serializable implementiert haben
        Primitive Datentypen sind natürlich immer serialisierbar

        Es wird kein Bundle benutzt, da sich dieses auf Parcel stützt, was nicht zwischen Geräten
        und verschiedenen OS-Versionen benutzt werden sollte.
     */
    public void setAdditionalData(HashMap data){
        this.additionalData = data;
    }

    public HashMap getAdditionalData(){
        return this.additionalData;
    }
}
