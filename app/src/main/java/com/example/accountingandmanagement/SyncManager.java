package com.example.accountingandmanagement;

import android.content.Context;
import android.util.Log;

public class SyncManager {

    private static SyncManager instance;
    private Context context;
    DatabaseHelper db = new DatabaseHelper(context);

    private SyncManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized SyncManager getInstance(Context context) {
        if (instance == null) {
            instance = new SyncManager(context);
        }
        return instance;
    }

    public void syncPendingActions() {
        db.syncPendingActions();
        Log.d("SyncManager", "Syncing pending actions...");
    }

    public void fetchTransactions() {
        // call your API to fetch updates
        Log.d("SyncManager", "Fetching new transactions...");
    }
}
