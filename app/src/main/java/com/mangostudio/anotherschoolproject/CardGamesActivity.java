package com.mangostudio.anotherschoolproject;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class CardGamesActivity extends ActionBarActivity {
    public BluetoothManagement bluetooth;

    public static final int INTENT_ENABLE_BLUETOOTH = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_games);

        bluetooth = new BluetoothManagement();
        //Fragt, ob es einen Bluetooth-Adapter gibt und ob dieser aktiviert ist und handelt, wenn nicht
        bluetooth.checkBluetooth(this);
        registerMainLayoutListeners();
        InterThreadCom.releaseAllSockets();
    }
    protected void onResume(){
        super.onResume();
        InterThreadCom.releaseAllSockets();
    }
    public void registerMainLayoutListeners(){
        final Button host = (Button) findViewById(R.id.host);
        final Button connect = (Button) findViewById(R.id.connect);

        View.OnClickListener hostListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardGamesActivity.this, CreateServerActivity.class);
                CardGamesActivity.this.startActivity(intent);
            }
        };
        host.setOnClickListener(hostListener);
        //Wird auf "connect" geklickt, wechselt das layout zur Liste mit BT-Geräten
        View.OnClickListener connectListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardGamesActivity.this, ConnectActivity.class);
                CardGamesActivity.this.startActivity(intent);
            }
        };
        connect.setOnClickListener(connectListener);
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
        //Bringe die App in den Hintergrund (App schließt sich, ohne dass der Prozess sofort beendet wird)
        moveTaskToBack(true);
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
}
