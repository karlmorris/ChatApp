package edu.temple.chatapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessageService extends FirebaseMessagingService {
    public MessageService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        // Broadcast the received message to anyone that wants to hear it
        // *Not Secure* At the very least we'd want to use a Local Broadcast
        Intent messageIntent = new Intent("CHAT_APP_MESSAGE_ACTION");
        messageIntent.putExtra("message", remoteMessage.getData().get("message"));

        Log.d("Received message", remoteMessage.getData().get("message"));


        sendBroadcast(messageIntent);
    }
}
