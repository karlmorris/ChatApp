package edu.temple.chatapp;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class ServerAPIHelper {

    private static final String APIBaseURL = "https://kamorris.com/temple/gcmdemo/";

    private static String makeAPICall(String api, JSONObject values) throws Exception {

        URL url = new URL(APIBaseURL + api);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(20000);
        conn.setConnectTimeout(20000);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Cache-Control", "no-cache");
        conn.setDoInput(true);
        conn.setDoOutput(true);


        // Post a JSON string under "data" key
        List<AbstractMap.SimpleEntry> nameValuePairs = new ArrayList<>(1);
        if (values != null)
            nameValuePairs.add(new AbstractMap.SimpleEntry<>("data", values.toString()));

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(getQuery(nameValuePairs));
        writer.flush();
        writer.close();
        os.close();

        if (values != null)
            Log.i("API Request", values.toString());

        conn.connect();

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            String result = readStream(conn.getInputStream());
            Log.i("API Response", result);
            return result;
        }
        else
            return null;
    }

    private static String makeAPICall(String api, JSONObject values, File file, String fieldName) throws Exception {

        List<AbstractMap.SimpleEntry> nameValuePairs = new ArrayList<>(2);
        if (values != null)
            nameValuePairs.add(new AbstractMap.SimpleEntry<>("data", values.toString()));
        if (file != null)
            nameValuePairs.add(new AbstractMap.SimpleEntry<>(fieldName, file));

        URL url = new URL(APIBaseURL + api);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(20000);
        conn.setConnectTimeout(20000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        OutputStream os = conn.getOutputStream();
        os.flush();
        os.close();

        conn.connect();

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
            return readStream(conn.getInputStream());
        else
            return null;
    }

    private static String getQuery(List<AbstractMap.SimpleEntry> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (AbstractMap.SimpleEntry pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode((String) pair.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode((String) pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    private static String readStream(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    public static void register(String username, String fcm_token) throws Exception {
        JSONObject data = new JSONObject();

        data.put("user_id", username);
        data.put("reg_id", fcm_token);

        makeAPICall("chat_app_register.php", data);
    }

    public static void sendMessage (String username, String recipientName, String message) throws Exception {
        JSONObject data = new JSONObject();

        data.put("user_id", username);
        data.put("recipient_id", recipientName);
        data.put("message", message);

        makeAPICall("chat_app_send_message.php", data);
    }

}
