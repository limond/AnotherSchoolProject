package com.mangostudio.anotherschoolproject;

/**
 * Created by Leon on 03.04.2015.
 */
public interface OnDiscoveryStatusChangeRequestListener {
    //Listener wird benutzt, um bekannt zu machen, dass der Nutzer den Start oder den Stopp der Geräte-Suche wünscht
    void onStatusChange(int status);
}
