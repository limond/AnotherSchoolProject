package com.mangostudio.anotherschoolproject;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Leon on 02.04.2015.
 */
public class HostListView extends ListView {
    public ArrayList<String> HostList = new ArrayList<String>();
    public Boolean isSearching = false;

    public HostListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //Nur zum Testen (Hier stehen später bereits gefundene Geräte)
        HostList.add("test1");
        HostList.add("test2");
        HostList.add("test3");
        HostList.add("test1");
        HostList.add("test2");
        HostList.add("test3");
        HostList.add("test1");
        HostList.add("test2");
        HostList.add("test3");
        HostList.add("test1");
        HostList.add("test2");
        HostList.add("test3");
        HostList.add("test1");
        HostList.add("test2");
        HostList.add("test3");
        this.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, HostList));
        /*
            Die folgenden 2 Zeilen stammen aus http://stackoverflow.com/questions/4265228/how-to-add-a-footer-in-listview
            Sie fügen an die Liste einen Footer an (Eintrag der die Suche nach neuen Geräten erlaubt)
        */
        final View footerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.hosts_list_footer, null, false);
        this.addFooterView(footerView);
        final TextView status = (TextView) footerView.findViewById(R.id.discoveryStatus);
        final ProgressBar progressSpinner = (ProgressBar) footerView.findViewById(R.id.discoveryProgressBar);

        this.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(view.hashCode() == footerView.hashCode()){
                    //Footer wurde angeklickt (Suche nach Geräten beginnt oder endet)
                    if(!isSearching){
                        isSearching = true;
                        status.setText(R.string.DiscoveryRunning);
                        progressSpinner.setVisibility(View.VISIBLE);
                    }
                    else{
                        isSearching = false;
                        status.setText(R.string.DiscoveryStart);
                        progressSpinner.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
}