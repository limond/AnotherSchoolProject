package com.mangostudio.anotherschoolproject;

import android.app.Application;
import android.bluetooth.BluetoothServerSocket;
import android.content.Context;

/**
 * Created by Leon on 09.04.2015.
 */
public class CardGamesApplication extends Application {
    private NetworkHandler netHandler;
    private NetworkThread netThread;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this.getApplicationContext();
        //Erstelle den Network-Thread
        netThread = new NetworkThread();
        netThread.start();
        //Setze den Messege-Handler für den Netzwerkthread
        netHandler = new NetworkHandler(netThread.getLooper());
    }

    public NetworkHandler getNetworkHandler() {
        return netHandler;
    }

    public UIHandler getUIHandler() {
        return netHandler.uiHandler;
    }

    public static Context getContext() {
        return context;
    }
}
