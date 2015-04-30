package com.mangostudio.anotherschoolproject;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothServerSocket;
import android.content.Context;
import android.os.HandlerThread;
import android.os.Looper;

import java.lang.ref.WeakReference;

/**
 * Created by Leon on 09.04.2015.
 */
public class CardGamesApplication extends Application {
    private static NetworkHandler netHandler;
    //Setze den Messege-Handler für den UI-Thread
    private static UIHandler uiHandler = new UIHandler(Looper.getMainLooper());
    private HandlerThread netThread;
    private static Context context;
    private static WeakReference<Activity> currentActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
        //Erstelle den Network-Thread
        netThread = new HandlerThread("NetworkThread");
        netThread.start();
        //Setze den Message-Handler für den Netzwerkthread
        netHandler = new NetworkHandler(netThread.getLooper());
    }

    public static NetworkHandler getNetworkHandler() {
        return netHandler;
    }

    public static UIHandler getUIHandler() {
        return uiHandler;
    }

    public static Context getContext() {
        return context;
    }

    public static void setCurrentActivity(Activity act){
        currentActivity = new WeakReference<>(act);
    }
    public static Activity getCurrentActivity() throws IllegalStateException{
        Activity act =  currentActivity.get();
        if (act == null) throw new IllegalStateException("No reference to an activity found");
        return act;
    }
}
