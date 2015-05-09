package com.mangostudio.anotherschoolproject;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Leon on 02.04.2015.
 */
public class HostListView extends ListView {
    /*
        Die HostListView stellt eine Liste von Geräten dar, wobei am Fuß der Liste, immer ein Eintrag zum Suchen neuer Geräte ist.
        Es gibt zwei Listeners, die von außen gehört werden können:
        der OnDiscoveryStatusChangeRequestListener wird aufgerufen, sobald die Suche nach neuen Geräten gestartet/beendet werden soll
        der OnDeviceSelectionListener wird aufgerufen, sobald ein Gerät aus der Liste ausgewählt wurde
     */
    public final static int DISCOVERY_START = 0;
    public final static int DISCOVERY_STOP = 1;

    private ArrayList<String> HostList = new ArrayList<>();
    private ArrayAdapter<String> arrAdapter;
    private boolean isSearching = false;
    private OnDiscoveryStatusChangeRequestListener statusListener;
    private OnDeviceSelectionListener selectListener;
    private LinkedHashSet<BluetoothDevice> devices;

    private final TextView status;
    private final ProgressBar progressSpinner;

    public HostListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        devices = new LinkedHashSet<>();
        //Der arrAdapter erzeugt für jeden Eintrag in HostList eine View android.R.layout.simple_list_item_1, die nur eine TextView beinhaltet
        arrAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, HostList);
        this.setAdapter(arrAdapter);
        /*
            Die folgenden 2 Zeilen stammen aus http://stackoverflow.com/questions/4265228/how-to-add-a-footer-in-listview
            Sie fügen an die Liste einen Footer an (Eintrag, der die Suche nach neuen Geräten erlaubt)
        */
        final View footerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.hosts_list_footer, null, false);
        this.addFooterView(footerView);

        status = (TextView) footerView.findViewById(R.id.discoveryStatus);
        progressSpinner = (ProgressBar) footerView.findViewById(R.id.discoveryProgressBar);

        this.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int count, long l) {
                if (view.equals(footerView)) {
                    //Footer wurde angeklickt (Suche nach Geräten beginnt oder endet)
                    if (!isSearching) {
                        if (statusListener != null) statusListener.onStatusChange(DISCOVERY_START);
                        setSearchingAppereance(DISCOVERY_START);
                    } else {
                        if (statusListener != null) statusListener.onStatusChange(DISCOVERY_STOP);
                        setSearchingAppereance(DISCOVERY_STOP);
                    }
                }
                else{
                    if(isSearching) {
                        if (statusListener != null) statusListener.onStatusChange(DISCOVERY_STOP);
                        setSearchingAppereance(DISCOVERY_STOP);
                    }
                    //ein Gerät wurde angeklickt
                    /*
                        Im folgenden wird über das LinkedHashSet "devices" iteriert, um das ausgewählte zu finden
                     */
                    Iterator<BluetoothDevice> it = devices.iterator();
                    BluetoothDevice selectedDevice = it.next();
                    for(int i = 0; i<count; i++){
                        selectedDevice = it.next();
                    }
                    if(selectListener != null) selectListener.onSelect(selectedDevice);
                }
            }
        });
    }
    //Ändert das Aussehen des letzten Eintrags der Liste (z.B drehender Kreis während der Suche)
    public void setSearchingAppereance(int statusCode){
        if(statusCode == DISCOVERY_START){
            isSearching = true;
            status.setText(R.string.DiscoveryRunning);
            progressSpinner.setVisibility(View.VISIBLE);
        }
        else if (statusCode == DISCOVERY_STOP){
            isSearching = false;
            status.setText(R.string.DiscoveryStart);
            progressSpinner.setVisibility(View.GONE);
        }
    }

    public void setOnDiscoveryStatusChangeRequestListener(OnDiscoveryStatusChangeRequestListener statusListener){
        this.statusListener = statusListener;
    }

    public void setOnDeviceSelectionListener(OnDeviceSelectionListener selectListener){
        this.selectListener = selectListener;
    }

    //Setzt die Liste der Geräte / updated das UI
    public void setDevices(Set<BluetoothDevice> devices){
        this.devices.clear();
        this.devices.addAll(devices);
        updateListView();
    }
    //Fügt ein Gerät zur Liste hinzu (, wenn es noch nicht vorhanden ist)
    public void addDevice(BluetoothDevice device){
        this.devices.add(device);
        updateListView();
    }
    //Füllt die UI-Komponente mit den neuen Einträgen
    private void updateListView(){
        HostList.clear();
        for (BluetoothDevice device : devices) {
            String name = device.getName();
            //Wenn Android noch nicht den Namen vom Gerät erfahren hat, wird stattdessen die Hardware-Adresse benutzt
            if(name == null) name = device.getAddress();
            //Namenszusatz "[paired]", wenn das Gerät bereits bekannt ist
            if(device.getBondState() == BluetoothDevice.BOND_BONDED) name += " [paired]";
            HostList.add(name);
        }
        //Leitet das eigentliche Update der UI-Komponente ein
        arrAdapter.notifyDataSetChanged();
    }
}