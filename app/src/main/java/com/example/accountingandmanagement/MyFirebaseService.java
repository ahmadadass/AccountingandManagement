package com.example.accountingandmanagement;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseService extends FirebaseMessagingService {

    private static final String TAG = "FCM";

    @Override
    public void onMessageReceived(RemoteMessage message) {
        super.onMessageReceived(message);

        Log.d(TAG, "FCM message received");

        if (message.getData().size() > 0) {

            String type = message.getData().get("type");

            Log.d(TAG, "Type: " + type);

            if ("sync".equals(type)) {
                // 🔥 CALL YOUR SYNC SYSTEM
                SyncManager.getInstance(getApplicationContext()).syncPendingActions();
            }

            if ("new_transaction".equals(type)) {
                // Fetch latest data from server
                SyncManager.getInstance(getApplicationContext()).fetchTransactions();
            }
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        Log.d(TAG, "New token: " + token);

        // 🔥 send token to your server
        sendTokenToServer(token);
    }

    private void sendTokenToServer(String token) {
        // TODO: implement API call to save token
    }
}
