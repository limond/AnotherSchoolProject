package com.mangostudio.anotherschoolproject;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.TextView;

/**
 * Created by limond on 06.03.15.
 */
public class TextViewLogger extends Handler {

    private final TextView text;

    public TextViewLogger(Looper l, TextView text) {
        super(l);
        this.text = text;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        text.setText(text.getText()+"\n"+msg.obj);
    }
}
