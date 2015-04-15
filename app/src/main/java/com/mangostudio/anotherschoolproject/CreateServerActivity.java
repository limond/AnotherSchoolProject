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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class CreateServerActivity extends ActionBarActivity {
    private BluetoothManagement bluetooth;
    private NetworkHandler netHandler;
    BroadcastReceiver receiver;
    public ArrayList<String> connectedList = new ArrayList<>();
    public ArrayAdapter<String> arrAdapter;
    private ArrayList<BluetoothDevice> connectedDevices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_create_server);
        bluetooth = new BluetoothManagement();
        netHandler = ((CardGamesApplication)getApplication()).getNetworkHandler();
        registerCreateServerListeners();
    }

    @Override
    public void onStop(){
        super.onStop();
        ((CardGamesApplication)getApplication()).getUIHandler().stopServer();
        if(receiver != null) unregisterReceiver(receiver);
    }


    public void registerCreateServerListeners(){
        final TextView btname = (TextView) findViewById(R.id.btname);
        final Button toggleDiscoverable = (Button) findViewById(R.id.toggleDiscoverable);
        final ListView connectedListView =(ListView) findViewById(R.id.connectedList);
        arrAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, connectedList);
        connectedListView.setAdapter(arrAdapter);

        btname.setText(bluetooth.getName());
        toggleDiscoverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                    die folgenden drei Zeilen stammen aus der Bluetooth-Guide http://developer.android.com/guide/topics/connectivity/bluetooth.html
                    und sind die einzige (sinnvolle) Lösung, um vom System anzufragen, es sichtbar zu machen
                 */
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);
            }
        });
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("ACTION_SCAN_MODE_CHANGED")) {
                    int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.SCAN_MODE_NONE);
                    if(mode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                        toggleDiscoverable.setText(R.string.discoverable);
                        toggleDiscoverable.setEnabled(false);
                    }
                    else {
                        toggleDiscoverable.setText(R.string.makeDiscoverable);
                        toggleDiscoverable.setEnabled(true);
                    }
                }
                else if(intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED)){
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    connectedDevices.add(device);
                    updateList();
                }
                else if(intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)){
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    connectedDevices.remove(device);
                    updateList();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(receiver, filter);

        InterThreadCom.startServer(netHandler);
    }

    private void updateList() {
        arrAdapter.clear();
        for(BluetoothDevice device : connectedDevices){
            arrAdapter.add(device.getName());
        }
        arrAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_server, menu);
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
}
