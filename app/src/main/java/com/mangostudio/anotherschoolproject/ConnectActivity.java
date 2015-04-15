package com.mangostudio.anotherschoolproject;

import android.bluetooth.BluetoothDevice;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class ConnectActivity extends ActionBarActivity {
    public BluetoothManagement bluetooth;
    public NetworkHandler netHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        bluetooth = new BluetoothManagement();
        netHandler = ((CardGamesApplication)getApplication()).getNetworkHandler();
        registerHostListListeners();
    }

    public void registerHostListListeners(){
        final HostListView list = (HostListView) findViewById(R.id.hostsListView);
        //Bereits bekannte Geräte werden der UI-Komponente mitgeteilt
        list.setDevices(bluetooth.getPairedDevices());
        //Klickt der Nutzer auf den Listenfuß, wird eine Suche nach neuen Geräten gestartet bzw. beendet
        list.setOnDiscoveryStatusChangeRequestListener(new OnDiscoveryStatusChangeRequestListener() {
            @Override
            public void onStatusChange(int status) {
                switch (status) {
                    case HostListView.DISCOVERY_START:
                        bluetooth.discoverNewDevices(getApplicationContext());
                        break;
                    case HostListView.DISCOVERY_STOP:
                        bluetooth.cancelDiscovery(getApplicationContext());
                        break;
                }
            }
        });
        list.setOnDeviceSelectionListener(new OnDeviceSelectionListener() {
            @Override
            public void onSelect(BluetoothDevice selectedDevice) {
                /*
                    Ein Gerät wurde ausgewählt und ein Verbindungsversuch muss unternommen werden.
                    Das blockiert den ausführenden Thread für eine lange Zeit
                    (das System kann den Pairing-Dialog dazwischenschieben, das den Thread noch länger blockiert)
                    Damit das System keinen  "application not responding" Dialog anzeigt, wird der Network-Thread benutzt
                 */
                InterThreadCom.connectToDevice(getApplicationContext(), selectedDevice);
            }
        });
        //Werden neue Geräte gefunden, werden diese der UI-Komponente HostListView mitgeteilt
        bluetooth.setOnNewDeviceListener(new OnNewDeviceListener() {
            @Override
            public void onNewDevice(BluetoothDevice device) {
                list.addDevice(device);
            }
        });
        //Wenn die BT-Suche durch das System abgebrochen wird, wird das Aussehen des Listenfußes auf "es wird nicht gesucht" gesetzt
        bluetooth.setOnDicoveryFinishedBySystemListener(new OnDiscoveryFinishedBySystemListener() {
            @Override
            public void onFinished() {
                list.setSearchingAppereance(HostListView.DISCOVERY_STOP);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_connect, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onDestroy(){
        super.onDestroy();
        //Bevor die Activity durch das System geschlossen wird, wird die Bluetooth-Suche abgebrochen
        if(bluetooth.isDiscovering()) bluetooth.cancelDiscovery(getApplicationContext());
    }
}
