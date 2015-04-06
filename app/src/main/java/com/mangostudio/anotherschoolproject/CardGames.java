package com.mangostudio.anotherschoolproject;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class CardGames extends ActionBarActivity {
    public int currentLayout;
    public NetworkHandler netHandler;
    public BluetoothManagement bluetooth;

    public static final int INTENT_ENABLE_BLUETOOTH = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_games);

        //Erstelle den Network-Thread
        NetworkThread thread = new NetworkThread();
        thread.start();
        //Setze den Messege-Handler für den Netzwerkthread
        netHandler = new NetworkHandler(thread.getLooper());

        bluetooth = new BluetoothManagement();
        //Fragt, ob es einen Bluetooth-Adapter gibt und ob dieser aktiviert ist und handelt, wenn nicht
        bluetooth.checkBluetooth(this);
    }

    public void registerMainLayoutListeners(){
        final Button host = (Button) findViewById(R.id.host);
        final Button connect = (Button) findViewById(R.id.connect);
        final TextView text = (TextView) findViewById(R.id.textView);

        View.OnClickListener hostListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setText("test click 1");
            }
        };
        host.setOnClickListener(hostListener);
        //Wird auf "connect" geklickt, wechselt das layout zur Liste mit BT-Geräten
        View.OnClickListener connectListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.hosts_list);
                //text.setText("test click 2");
            }
        };
        connect.setOnClickListener(connectListener);
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

    /*
        Die folgenden zwei Funktionen sind automatisch generiert und behandeln das Menü, das mit der Menü-Taste aufgerufen wird
        Wird dieses Menü nicht gebracht, kann man die Funktionen in der Zukunft löschen
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_card_games, menu);
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
    @Override
    public void onBackPressed() {
        /*
           Wird der Back-Button gedrückt wird eine Aktion basierend auf dem aktuellen Layout aufgerufen
         */
        switch(currentLayout){
            case R.layout.hosts_list:
                //Gehe zurück zum Hauptbildschirm
                setContentView(R.layout.activity_card_games);
                break;
            default:
                //Bringe die App in den Hintergrund (App schließt sich, ohne dass der Prozess sofort beendet wird)
                moveTaskToBack(true);
                break;
        }
    }
    @Override
    public void setContentView(final int id) {
        //Wird das Layout verändert, wird das aktuelle Layout gespeichert um Entscheidungen aufgrund des Layouts treffen zu können
        super.setContentView(id);
        currentLayout = id;
        //Je nachdem, zu welchem Layout gewechselt wird, werden entsprechende Listener registriert und Funktionen aufgerufen
        switch(id){
            case R.layout.activity_card_games:
                registerMainLayoutListeners();
                break;
            case R.layout.hosts_list:
                registerHostListListeners();
                break;
        }
    }

    @Override
    //Von der App (eigentlich der Activity) gestartete Apps (eigentlich Activities) berichten ihr Ergebnis
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case INTENT_ENABLE_BLUETOOTH:
                //Wenn nach der BT-Aktivierung durch den Benutzer gefragt wurde, diese aber abgelehnt wurde, wird die App mit einer Nachricht beendet
                if(resultCode != RESULT_OK){
                    Toast.makeText(this, R.string.BluetoothNotActivated, Toast.LENGTH_LONG).show();
                    //Beendet die App (genauer: die Aktuelle Activity), wenn der Nutzer BT nicht aktiviert
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //Bevor die App durch das System gänzlich geschlossen wird, wird die Bluetooth-Suche abgebrochen
        if(bluetooth.isDiscovering()) bluetooth.cancelDiscovery(getApplicationContext());
    }
}
