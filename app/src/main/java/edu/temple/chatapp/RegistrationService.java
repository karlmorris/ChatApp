package edu.temple.chatapp;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class RegistrationService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Here you would pass the token to the app/chat server
        // however since we don't necessarily have a username at this point
        // we have to hold off on that function.
    }

}
