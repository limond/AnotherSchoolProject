package com.mangostudio.anotherschoolproject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by Leon on 04.04.2015.
 */
public class BluetoothManagement {

    public static final int BLUETOOTH_NOT_PRESENT = 0;
    public static final int BLUETOOTH_NOT_ENABLED = 1;
    public static final int BLUETOOTH_ENABLED = 2;
    //Bluetooth Server und Clients benötigen eine (die gleiche!) hartkodierte UUID
    public static final String UUID = "56c3c375-9b82-4be9-a43f-57ca51f6ce91";

    public BluetoothAdapter adapter;
    private Boolean isDiscovering = false;
    private BroadcastReceiver receiver;
    private OnNewDeviceListener newDeviceListener;
    private OnDiscoveryFinishedBySystemListener discoveryFinishedListener;

    public BluetoothManagement(){
        adapter = BluetoothAdapter.getDefaultAdapter();
    }

    //Gibt an, ob Bluetooth nicht vorhanden, ausgeschaltet oder eingeschaltet ist
    public int getAdapterStatus() {
        if(adapter == null) return BLUETOOTH_NOT_PRESENT;
        if(!adapter.isEnabled()) return BLUETOOTH_NOT_ENABLED;
        return BLUETOOTH_ENABLED;
    }

    //Gibt eine Liste (eigentlich ein Set, also eine ungeordnete Liste mit einzigartigen Elementen) mit Geräten zurück, die bereits verbunden worden sind
    public Set<BluetoothDevice> getPairedDevices() {
        return adapter.getBondedDevices();
    }

    //Behandelt Komplikationen mit dem BT-Adapter
    public void checkBluetooth(Activity act) {
        int status = getAdapterStatus();
        switch(status){
            case BLUETOOTH_NOT_PRESENT:
                Toast.makeText(act, R.string.NoBluetoothWarning, Toast.LENGTH_LONG).show();
                //Beendet die App (genauer: die Aktuelle Activity), wenn kein BT-Adapter vorhanden ist
                act.finish();
                break;
            case BLUETOOTH_NOT_ENABLED:
                //Fragt beim System an, den BT-Adapter-Dialog anzuzeigen (onActivityResult empfängt das Resultat)
                act.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), CardGamesActivity.INTENT_ENABLE_BLUETOOTH);
                break;
        }
    }

    //Schaltet die Geräte-Suche an
    public void discoverNewDevices(Context ctx){
        /*
            Die Funktion ist größtenteils aus der Bluetooth-Guide übernommen,
            da das so ziemlich die einzige Lösung für die Promblemstellung ist
            siehe http://developer.android.com/guide/topics/connectivity/bluetooth.html
         */
        //Erstellt einen Empfänger der vom System benachrichtigt wird, sobald die Suche beendet oder ein neues Gerät gefunden wurde
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // Wenn ein neues Gerät gefunden wird
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    //Bekomme das neue Gerät aus dem Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //Triggert den Listener für neue Geräte
                    if(newDeviceListener != null) newDeviceListener.onNewDevice(device);
                }
                else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                    //Discovery wurde vom System beendet (Triggert den Listener für das Beenden der Suche durch das System)
                    if(discoveryFinishedListener != null) discoveryFinishedListener.onFinished();
                    isDiscovering = false;
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        isDiscovering = true;
        ctx.registerReceiver(receiver, filter);
        //Startet die eigentliche Suche
        adapter.startDiscovery();
    }

    public void setOnDicoveryFinishedBySystemListener(OnDiscoveryFinishedBySystemListener listener){
        discoveryFinishedListener = listener;
    }

    public void setOnNewDeviceListener(OnNewDeviceListener listener){
        newDeviceListener = listener;
    }

    //Offensichtlich: Gibt an, ob gerade nach Geräten gesucht wird
    public Boolean isDiscovering(){
        return this.isDiscovering;
    }

    //Beendet die Geräte-Suche vor ihrer Beendung durch das System
    public void cancelDiscovery(Context ctx){
        adapter.cancelDiscovery();
        ctx.unregisterReceiver(receiver);
        isDiscovering = false;
    }

    //Name des eigenen Adapters
    public String getName(){
        return adapter.getName();
    }
}
