package com.mangostudio.anotherschoolproject;

import android.content.DialogInterface;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class CardGames extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_games);
        final TextView text = (TextView) findViewById(R.id.textView);

        final Button host = (Button) findViewById(R.id.host);
        final Button connect = (Button) findViewById(R.id.connect);

        View.OnClickListener hostListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setText("test click 1");
            }
        };
        host.setOnClickListener(hostListener);
        View.OnClickListener connectListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setText("test click 2");
            }
        };
        connect.setOnClickListener(connectListener);

        Toast.makeText(getApplicationContext(), "test Toast", Toast.LENGTH_SHORT).show();

        TextViewLogger textLogger = new TextViewLogger(getMainLooper(), text);
        NetworkThread thread = new NetworkThread();
        thread.start();
        NetworkHandler netHandler = new NetworkHandler(thread.getLooper());

        Message msg = netHandler.obtainMessage();
        msg.obj = "Hello world";
        netHandler.sendMessage(msg);

    }


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
}
