package com.mangostudio.anotherschoolproject;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class GameWaitActivity extends ActionBarActivity {
    private View decorView;
    private BluetoothDevice hostDevice;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_client);
        decorView = getWindow().getDecorView();
        //Bekomme das Intent und das übergebene Gerät aus dem UI-Handler (das übergebene Gerät ist das mit aktivem Socket)
        Intent intent = getIntent();
        hostDevice = intent.getParcelableExtra("device");
        TextView waitMessage = (TextView) findViewById(R.id.waitMessage);
        //Setzte die vom Gerätenamen abhänige Wartenachricht (bis Spielbeginn);
        waitMessage.setText(getString(R.string.waitForGameStart,hostDevice.getName()));
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Gehe zurück zum Verbindungsbildschirm, wenn Verbindung getrennt wird
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getAddress().equals(hostDevice.getAddress())) finish();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(NetworkHandler.ACTION_SOCKET_CLOSED);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(receiver != null) unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_client, menu);
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

    /*
        Funktion aus https://developer.android.com/training/system-ui/immersive.html
        Aktiviert den Sticky-Immersive-Mode, bei dem das System-UI nur sichtbar ist, wenn am Bildschirmrand gewischt wird
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
