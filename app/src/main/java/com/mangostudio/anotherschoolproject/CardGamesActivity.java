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
    /*
        Activity ganz am Anfang: Zwei Buttons, die Entscheiden, ob Host oder Client
     */
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
        CardGamesApplication.setCurrentActivity(this);
    }
    public void registerMainLayoutListeners(){
        final Button host = (Button) findViewById(R.id.host);
        final Button connect = (Button) findViewById(R.id.connect);

        //Wird auf "Host" geklickt, wird die CreateServerActivity gestartet
        View.OnClickListener hostListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardGamesActivity.this, CreateServerActivity.class);
                CardGamesActivity.this.startActivity(intent);
            }
        };
        host.setOnClickListener(hostListener);
        //Wird auf "connect" geklickt, wird die ConnectActivity gestartet
        View.OnClickListener connectListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardGamesActivity.this, ConnectActivity.class);
                CardGamesActivity.this.startActivity(intent);
            }
        };
        connect.setOnClickListener(connectListener);
    }

    @Override
    public void onBackPressed() {
        //Bringe die App in den Hintergrund (App schließt sich, ohne dass der Prozess sofort beendet wird)
        moveTaskToBack(true);
    }

    @Override
    //Von der Activity gestartete Activities berichten ihr Ergebnis
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
