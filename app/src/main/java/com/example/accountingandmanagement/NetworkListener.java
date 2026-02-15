package com.example.accountingandmanagement;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

public class NetworkListener {

    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private boolean isConnected = false;

    public void start(Context context) {

        connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        networkCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);

                // 🔥 Internet is back
                if (!isConnected) {
                    isConnected = true;

                    SyncManager.getInstance(context).syncPendingActions();
                }
            }

            @Override
            public void onLost(Network network) {
                super.onLost(network);

                isConnected = false;
            }
        };

        connectivityManager.registerDefaultNetworkCallback(networkCallback);
    }

    public void stop() {
        if (connectivityManager != null && networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }
}
