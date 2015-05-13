package com.mangostudio.anotherschoolproject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class ConnectActivity extends ActionBarActivity {
    /*
        Die Activity bietet an sich mit einem bekannten oder neu gefundenen Gerät zu verbinden
     */
    //public BluetoothManagement bluetooth;
    private HostListView hostList;
    private WifiManagement wifi = CardGamesApplication.getWifiManagement();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CardGamesApplication.setCurrentActivity(this);
        setContentView(R.layout.activity_connect);
        //bluetooth = new BluetoothManagement();
        registerHostListListeners();
        //InterThreadCom.releaseAllSockets();
    }

    @Override
    protected void onResume(){
        super.onResume();
        CardGamesApplication.setCurrentActivity(this);
        //InterThreadCom.releaseAllSockets();
        hostList.setEnabled(true);
    }

    public void registerHostListListeners(){
        hostList = (HostListView) findViewById(R.id.hostsListView);
        //Bereits bekannte Geräte werden der UI-Komponente mitgeteilt
        //hostList.setDevices(bluetooth.getPairedDevices());
        //Klickt der Nutzer auf den Listenfuß, wird eine Suche nach neuen Geräten gestartet bzw. beendet
        hostList.setOnDiscoveryStatusChangeRequestListener(new OnDiscoveryStatusChangeRequestListener() {
            @Override
            public void onStatusChange(int status) {
                switch (status) {
                    case HostListView.DISCOVERY_START:
                        //bluetooth.discoverNewDevices(getApplicationContext());
                        wifi.startDiscovery();
                        break;
                    case HostListView.DISCOVERY_STOP:
                        //bluetooth.cancelDiscovery(getApplicationContext());
                        wifi.stopDiscovery();
                        break;
                }
            }
        });
        hostList.setOnDeviceSelectionListener(new OnDeviceSelectionListener() {
            @Override
            public void onSelect(WifiP2pDevice selectedDevice) {
                /*
                    Ein Gerät wurde ausgewählt und ein Verbindungsversuch muss unternommen werden.
                    Das blockiert den ausführenden Thread für eine lange Zeit
                    (das System kann den Pairing-Dialog dazwischenschieben, das den Thread noch länger blockiert)
                    Damit das System keinen  "application not responding" Dialog anzeigt, wird der Network-Thread benutzt
                 */
                //InterThreadCom.connectToDevice(selectedDevice);
                //Die View wird gesperrt, damit nicht mehrere Verbindungsversuche unternommen werden können
                wifi.connect(selectedDevice);
                hostList.setEnabled(false);
            }
        });
        //Werden neue Geräte gefunden, werden diese der UI-Komponente HostListView mitgeteilt
        /*bluetooth.setOnNewDeviceListener(new OnNewDeviceListener() {
            @Override
            public void onNewDevice(BluetoothDevice device) {
                hostList.addDevice(device);
            }
        });*/
        wifi.setOnPeersChangedListener(new OnPeersChangedListener() {
            @Override
            public void onChange(WifiP2pDeviceList wifiP2pDeviceList) {

                hostList.setDevices(wifiP2pDeviceList.getDeviceList());
            }
        });

        wifi.setOnDiscoveryStatusListener(new OnDiscoveryStatusListener() {
            @Override
            public void onStatus(int status) {
                if(status == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED){
                    hostList.setSearchingAppereance(HostListView.DISCOVERY_START);
                }
                else{
                    hostList.setSearchingAppereance(HostListView.DISCOVERY_STOP);
                }
            }
        });
        //Wenn die BT-Suche durch das System abgebrochen wird, wird das Aussehen des Listenfußes auf "es wird nicht gesucht" gesetzt
        /*bluetooth.setOnDicoveryFinishedBySystemListener(new OnDiscoveryFinishedBySystemListener() {
            @Override
            public void onFinished() {
                hostList.setSearchingAppereance(HostListView.DISCOVERY_STOP);
            }
        });*/
        wifi.registerReceiver();
    }

    protected void onStop(){
        super.onStop();
        //Bevor die Activity durch das System geschlossen wird, wird die Bluetooth-Suche abgebrochen
        //if(bluetooth.isDiscovering()) bluetooth.cancelDiscovery(getApplicationContext());
        wifi.stopDiscovery();
        wifi.unregisterReceiver();
    }
}
