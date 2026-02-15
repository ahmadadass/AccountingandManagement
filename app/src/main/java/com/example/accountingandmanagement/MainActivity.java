package com.example.accountingandmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static User user;
    public static String token;
    private ApiService apiService;
    public boolean wifiConnected = false;

    // In your Activity or Service
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper db = new DatabaseHelper(this);
        //if (user != null) db.deleteUserPassword(user.getId());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://money-management-0301.netlify.app/") // <--- YOUR LINK HERE
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 2. Create the service (This makes the 'apiService' variable work!)
        apiService = retrofit.create(ApiService.class);

        //db.deleteDatabase();

        TextInputEditText et_username = findViewById(R.id.et_username);
        TextInputEditText et_password = findViewById(R.id.et_password);
        Button btn_login = findViewById(R.id.btn_login);

        et_username.setText("testuser");
        et_password.setText("123");

        btn_login.setOnClickListener( e ->{
            String username = et_username.getText().toString();
            String password = et_password.getText().toString();


            // Create the login data
            Map<String, Object> root = new HashMap<>();
            root.put("action", "login");
            Map<String, String> data = new HashMap<>();
            data.put("username", username);
            data.put("password", password);
            root.put("data", data);
            String json = new Gson().toJson(root);
            RequestBody body = RequestBody.create(
                    json,
                    MediaType.parse("application/json")
            );
            ProgressDialog pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Connecting to database...");
            pd.show();
            long STime = System.currentTimeMillis();
            // Make the call
            apiService.SendAndGetData(body).enqueue(new Callback<DataResponse>() {
                @Override
                public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // SUCCESS! You now have your data
                        long ETime = System.currentTimeMillis();
                        Log.i("API", "STime: " + STime + " ETime: " + ETime);
                        pd.dismiss();
                        user = response.body().user;
                        ArrayList<Transaction> transactions = response.body().transactions;
                        Settings userSettings = response.body().settings;
                        db.deleteDatabase(user, transactions, userSettings);
                        token = response.body().token;
                        Log.i("API", "Token: " + token);
                        Intent intent = new Intent(MainActivity.this, statisticsActivity.class);
                        startActivity(intent);
                    } else {
                        pd.dismiss();
                        Toast.makeText(MainActivity.this, "incorrect username or password", Toast.LENGTH_SHORT).show();
                        //Log.v("API", " onResponse error :" + response.body());
                        Log.e("API", "Server returned error: " + response.message());
                    }
                }
                @Override
                public void onFailure(Call<DataResponse> call, Throwable t) {
                    pd.dismiss();
                    Log.e("API", "Error: " + t.getMessage());
                }
            });

            /*
            if (!username.isEmpty() && !password.isEmpty()){
                if (username.equals("admin") && password.equals("123")){
                    Intent intent = new Intent(MainActivity.this, statisticsActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(this, "incorrect username or password", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "please enter a username and password", Toast.LENGTH_SHORT).show();
            }*/
             // offline login !! TODO MAKE DATABASE SYNC SOME HOW??

            if(user != null && wifiConnected) {
                boolean isCorrect = PasswordUtils.verifyPassword(password, user.getPassword());
                if (isCorrect && username.equals(user.getName())) {
                    Intent intent = new Intent(MainActivity.this, statisticsActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "incorrect username or password", Toast.LENGTH_SHORT).show();
                }
            }

        });

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                // Wi-Fi might be available, check its type
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.v("WifiStatus", "Wi-Fi Connected!");
                    wifiConnected = true;
                    // Update UI or perform actions for Wi-Fi connection
                }
            }
            @Override
            public void onLost(Network network) {
                Log.w("WifiStatus", "Network Lost!");
                wifiConnected = false;
                // Handle general network loss
            }
            // Other callbacks like onCapabilitiesChanged, onLinkPropertiesChanged
        };
        connectivityManager.registerDefaultNetworkCallback(networkCallback); // Or registerSpecificNetworkCallback

// In onDestroy() or onStopCommand():
        connectivityManager.unregisterNetworkCallback(networkCallback);


    }

/*
    private void connectivityManager(Activity activity){

        commands.add("delete form transactions where id=1;");
        DatabaseHelper db = new DatabaseHelper(activity);

        Log.i("NET_MANAGER","Starting");
        connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();

        connectivityManager.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                activity.runOnUiThread(() -> {
                    Toast.makeText(activity, "Wi-Fi Connected", Toast.LENGTH_SHORT).show();
                    WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    String thisUserIp = Formatter.formatIpAddress(wifiInfo.getIpAddress());

                    if (!thisUserIp.equals("0.0.0.0")) {
                        wifiConnected = true;
                        ArrayList<Action> actions = db.getOfflineActions();

                        for (Action action:actions){
                            if (action.getTable_name().equals(DatabaseHelper.TABLE_TRANSACTION)) {
                                Transaction transaction = Transaction.convertToTransaction(action.getPaload());
                                switch (action.getAction_type()) {
                                    case "INSART": {
                                        try {
                                            if (db.insertTransaction(transaction.getName(), transaction.getTime(), transaction.getAmount(), transaction.getType(), transaction.getNotes(), transaction.getPayment_method(), transaction.getPaidStatus(), transaction.getMarkedStatus())) {
                                                db.setAsSyncedOfflineAction(action.getId());
                                            }
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                    case "UPDATE": {
                                        try {
                                            if (db.editTransaction(transaction.getId(), transaction.getName(), transaction.getTime(), transaction.getAmount(), transaction.getType(), transaction.getNotes(), transaction.getPayment_method(), transaction.getPaidStatus(), transaction.getMarkedStatus())) {
                                                db.setAsSyncedOfflineAction(action.getId());
                                            }
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                    case "DELETE": {
                                        try {
                                            if (db.deleteTransaction(transaction.getId())) {
                                                db.setAsSyncedOfflineAction(action.getId());
                                            }
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }
                            }

                            if (action.getTable_name().equals(DatabaseHelper.TABLE_SETTINGS)) {
                                Settings settings = Settings.convertToSettings(action.getPaload());
                                switch (action.getAction_type()) {
                                    case "UPDATE": {
                                        try {
                                            if (db.updateSettings(settings.getNameV(),settings.getTypeV(),settings.getNotesV(),settings.getTimeV(),settings.getUserId())) {
                                                db.setAsSyncedOfflineAction(action.getId());
                                            }
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                    case "DELETE": {
                                        try {
                                            if (db.deleteSetting(settings.getId())) {
                                                db.setAsSyncedOfflineAction(action.getId());
                                            }
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }
                            }
                        }

                    }
                });
            }

            @Override
            public void onLost(Network network) {
                activity.runOnUiThread(() -> {
                    wifiConnected = false;
                    //et_ip.setText("Ip: ");
                    Toast.makeText(activity, "Wi-Fi Disconnected", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
*/
}