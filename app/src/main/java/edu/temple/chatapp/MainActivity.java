package edu.temple.chatapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {
    EditText usernameEditText, recipientEditText, messageEditText;

    IntentFilter filter;


    // Handle chat messages received from the server

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String messageString = "";
            try {
                JSONObject messageObject = new JSONObject(intent.getStringExtra("message"));
                messageString = messageObject.getString("user_id") + ": " + messageObject.getString("message");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            addMessage(messageString, false);
        }
    };

    LinearLayout messageList;


    @Override
    protected void onResume() {
        super.onResume();

        // Listen for message broadcasts
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a filter to match the message broadcast
        filter = new IntentFilter();
        filter.addAction("CHAT_APP_MESSAGE_ACTION");

        messageList = (LinearLayout) findViewById(R.id.message_list);

        usernameEditText = (EditText) findViewById(R.id.username);
        recipientEditText = (EditText) findViewById(R.id.recipient);
        messageEditText = (EditText) findViewById(R.id.message);


        findViewById(R.id.registerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Register our username and token with the app/chat server
                new Thread() {
                    public void run (){
                        try {
                            ServerAPIHelper.register(usernameEditText.getText().toString(),
                                    FirebaseInstanceId.getInstance().getToken());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

            }
        });

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Our message is added here because we haven't written our API to
                // indicate whether a message was successfully sent
                addMessage(messageEditText.getText().toString(), true);

                // Send our message to the chat/app server for routing to the
                // user having the provided recipient id
                new Thread() {
                    public void run(){
                        try {
                            ServerAPIHelper.sendMessage(usernameEditText.getText().toString(),
                                    recipientEditText.getText().toString(),
                                    messageEditText.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    // Add a message to the list view
    private void addMessage(String message, boolean me){
        TextView messageTextView = new TextView(MainActivity.this);
        messageTextView.setText(message);
        messageTextView.setPadding(5,5,5,5);


        if (me) {
            messageTextView.setGravity(Gravity.RIGHT);
        } else {
            messageTextView.setGravity(Gravity.LEFT);
        }

        messageList.addView(messageTextView);
    }
}
