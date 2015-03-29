package com.mangostudio.anotherschoolproject;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by limond on 06.03.15.
 */
public class NetworkHandler extends Handler {

    public NetworkHandler(Looper l) {
        super(l);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Log.d("Thread", (String) msg.obj);
    }
}
