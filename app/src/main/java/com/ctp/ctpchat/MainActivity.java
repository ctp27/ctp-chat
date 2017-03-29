package com.ctp.ctpchat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private Button sendButton;
    private EditText messageArea;
    private Client client;
    private boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sendButton = (Button) findViewById(R.id.sendBtn);
        messageArea = (EditText) findViewById(R.id.chatArea);
        initializeClient();
        sendButton.setOnClickListener(sendBtnListener);


    }

    View.OnClickListener sendBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            client.writeMessage(new ChatMessage(ChatMessage.MESSAGE, messageArea.getText().toString()));
            messageArea.setText("");

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void initializeClient(){
        String username = "clinton";
        // empty username ignore it
        if(username.length() == 0)
            return;
        // empty serverAddress ignore it
        String server = "192.168.1.188";
        if(server.length() == 0)
            return;
        // empty or invalid port numer, ignore it
        String portNumber = "300";
        if(portNumber.length() == 0)
            return;
        int port = 0;
        try {
            port = Integer.parseInt(portNumber);
        }
        catch(Exception en) {
            return;   // nothing I can do if port number is not valid
        }

        // try creating a new Client with GUI
        client = new Client(server, port, username, MainActivity.this);
        // test if we can start the Client
        client.execute();
//        messageArea.setText("");
        connected = true;

    }
}


