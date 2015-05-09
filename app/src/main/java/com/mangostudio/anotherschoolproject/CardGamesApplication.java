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
    /*
        Die Application ist, wie der Name sagt, die eigentliche Anwendung.
        Im Manifast ist der Eintrag android:name=".CardGamesApplication" dafür zuständig, dass diese erweiterte Klasse benutzt wird.
        An dieser Stelle wird der NetzwerkThread gestartet und die Handler gesetzt.
    */
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
    //Statische Methoden um von überall aus die Handler zu bekommen
    public static NetworkHandler getNetworkHandler() {
        return netHandler;
    }

    public static UIHandler getUIHandler() {
        return uiHandler;
    }

    //Statische Methode, um den Application Context zu bekommen (z.B für Toast-Notifications)
    public static Context getContext() {
        return context;
    }

    /*
        Statische Methoden um die aktuelle Activity zu bekommen.
        In vielen Beispielen von Google werden UI-Komponenten durch Nebenthreads durchgeschleift, wenn das Resultat auf dem Hauptthread angezeigt werden soll.
        Diese Methode ist aber unschön, da UI-Komponenten nichts im NetzwerkThread zu suchen haben.
        Activities speichern deshalb hier eine Referenz bei ihrer Erstellung, Wiederherstellung nach Fokuswechsel, etc.
        WeakReferences ermöglichen, dass der GarbageCollector nicht gestört wird, wenn eine Activity beendet wird, dafür
        muss man eine Exception abfangen, wenn man die Funktion zu einem ungünstigen Zeitpunkt aufruft .
     */
    public static void setCurrentActivity(Activity act){
        currentActivity = new WeakReference<>(act);
    }
    public static Activity getCurrentActivity() throws IllegalStateException{
        Activity act =  currentActivity.get();
        if (act == null) throw new IllegalStateException("No reference to an activity found");
        return act;
    }
}
