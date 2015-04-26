package com.mangostudio.anotherschoolproject;

import android.os.Bundle;
import android.os.Parcel;

import java.io.Serializable;

/**
 * Created by Leon on 19.04.2015.
 */
public class BluetoothPackage implements Serializable {
    public static final int HANDLER_DESTINATION_UI = 1;
    public static final int HANDLER_DESTINATION_NETWORK = 2;

    public static final int ACTION_START_CLIENT_GAME = 1;

    public int destination;
    public int action;
    public byte[] extraData;

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

    //Ermöglicht es ein Bundle mit Daten anzuhängen
    public void setData(Bundle data){
        Parcel par = Parcel.obtain();
        par.writeBundle(data);
        extraData = par.marshall();
    }

    public Bundle getData(){
        Parcel par = Parcel.obtain();
        par.unmarshall(extraData,0,extraData.length);
        return par.readBundle();
    }
}
