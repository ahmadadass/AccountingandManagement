package com.example.accountingandmanagement;

import static com.example.accountingandmanagement.MainActivity.token;
import static com.example.accountingandmanagement.MainActivity.user;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "accountingManager.db";
    public static final int DATABASE_VERSION = 8;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://money-management-0301.netlify.app/") // <--- YOUR LINK HERE
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private ApiService apiService = retrofit.create(ApiService.class);

    Context Contextt;
    // tables
    public static final String TABLE_TRANSACTION = "transactions";
    public static final String TABLE_SETTINGS = "settings";
    public static final String TABLE_USER = "user";
    public static final String TABLE_OFFLINE_ACTIONS = "offline_actions";

    // Tables Columns
    public static final String COL_TRANSACTION_ID = "id";
    public static final String COL_TRANSACTION_ID_USER_ID = "user_id";
    public static final String COL_TRANSACTION_NAME = "name";
    public static final String COL_TRANSACTION_TIME = "time";
    public static final String COL_TRANSACTION_AMOUNT = "amount";
    public static final String COL_TRANSACTION_TYPE = "type";
    public static final String COL_TRANSACTION_NOTES = "notes";
    public static final String COL_TRANSACTION_PAYMENT_METHOD = "payment_method";
    public static final String COL_TRANSACTION_PAID = "paid";
    public static final String COL_TRANSACTION_BOOK_MARK = "bookmark";
    public static final String COL_SETTINGS_ID = "id";
    public static final String COL_SETTINGS_NAME_VISIVILITY = "name_visibility";
    public static final String COL_SETTINGS_TYPE_VISIVILITY = "type_visibility";
    public static final String COL_SETTINGS_NOTES_VISIVILITY = "notes_visibility";
    public static final String COL_SETTINGS_TIME_VISIVILITY = "time_visibility";
    public static final String COL_SETTINGS_USER_ID = "user_id";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_NAME = "username";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_SUBSCRIPTION = "subscription";
    public static final String COL_ACTIONS_ID = "id";
    public static final String COL_ACTIONS_TABLE_NAME = "table_name";
    public static final String COL_ACTIONS_ACTION_TYPE = "action_type";
    public static final String COL_ACTIONS_LOCAL_ID = "local_id";
    public static final String COL_ACTIONS_SREVER_ID = "server_id";
    public static final String COL_ACTIONS_PAYLOAD = "payload";
    public static final String COL_ACTIONS_TIME = "time";
    public static final String COL_ACTIONS_SYNCED = "synced";
    public static final String ACTION_INSART = "INSART";
    public static final String ACTION_UPDATE = "UPDATE";
    public static final String ACTION_DELETE = "DELETE";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Contextt = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE `" + TABLE_TRANSACTION + "` (" +
                COL_TRANSACTION_ID + " BINARY(16) PRIMARY KEY, " +
                COL_TRANSACTION_ID_USER_ID + " INTEGER, " +
                COL_TRANSACTION_NAME + " TEXT, " +
                COL_TRANSACTION_TIME + " INT(11), " +
                COL_TRANSACTION_AMOUNT + " DOUBLE, " +
                COL_TRANSACTION_TYPE + " TEXT, " +
                COL_TRANSACTION_NOTES + " TEXT, " +
                COL_TRANSACTION_PAYMENT_METHOD + " TEXT, " +
                COL_TRANSACTION_PAID + " INTEGER, " +
                COL_TRANSACTION_BOOK_MARK + " INTEGER)");

        db.execSQL("CREATE TABLE `"+ TABLE_SETTINGS +"` (" +
                COL_SETTINGS_ID + " INTEGER PRIMARY KEY, " +
                COL_SETTINGS_NAME_VISIVILITY + " INTEGER, " +
                COL_SETTINGS_TYPE_VISIVILITY + " INTEGER, " +
                COL_SETTINGS_NOTES_VISIVILITY + " INTEGER, " +
                COL_SETTINGS_TIME_VISIVILITY + " INTEGER, " +
                COL_SETTINGS_USER_ID + " INTEGER);");

        db.execSQL("CREATE TABLE `" + TABLE_USER + "` (" +
                COL_USER_ID + " INTEGER PRIMARY KEY, " +
                COL_USER_NAME + " TEXT, " +
                COL_USER_PASSWORD + " TEXT, " +
                COL_USER_SUBSCRIPTION + " TEXT)");

        db.execSQL("CREATE TABLE `" + TABLE_OFFLINE_ACTIONS + "` (" +
                COL_ACTIONS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ACTIONS_TABLE_NAME + " TEXT NOT NULL," +
                COL_ACTIONS_ACTION_TYPE + " TEXT NOT NULL," +
                COL_ACTIONS_LOCAL_ID + " TEXT NOT NULL," +
                COL_ACTIONS_SREVER_ID + " INTEGER ," +
                COL_ACTIONS_PAYLOAD + " TEXT ," +
                COL_ACTIONS_TIME + " INTEGER ," +
                COL_ACTIONS_SYNCED + " INTEGER)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OFFLINE_ACTIONS);
        onCreate(db);
    }

    public void syncSingleAction(int ActionId, String uuid, String json){
        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json")
        );

        apiService.SendAndGetData(body).enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {

                if (response.isSuccessful()) {
                    setAsSyncedOfflineAction(ActionId);
                } else {
                    // keep unsynced
                }
            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                // keep unsynced
            }
        });
    }

    /*private boolean isSyncing = false;

public void syncPendingActions() {
    if (isSyncing) return;

    isSyncing = true;
    syncNext();
}

private void syncNext() {
    Action action = getNextUnsyncedAction();

    if (action == null) {
        isSyncing = false;
        return;
    }

    syncSingleAction(action.getId(), action.getLocal_id(), action.getPaload());
}*/
    public void syncPendingActions() {

        if (!NetworkUtils.isConnected(Contextt)) return;

        ArrayList<Action> actions = getOfflineActions();

        for (Action action : actions) {
            syncSingleAction(action.getId(),action.getLocal_id(), action.getPaload());
        }
    }

    public void deleteDatabase(User user, ArrayList<Transaction> transactions, Settings settings){
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db,DATABASE_VERSION-1,DATABASE_VERSION);

        insertUser(user.getId(),user.getName(), user.getPassword(), user.getSubscription());
        for (Transaction transaction:transactions){
            UUID uuid = UUID.fromString(transaction.getId());

            ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
            bb.putLong(uuid.getMostSignificantBits());
            bb.putLong(uuid.getLeastSignificantBits());

            insertTransactionLocal(bb.array(),transaction.getUserId(),transaction.getName(),transaction.getTime(),transaction.getAmount(),transaction.getType(),transaction.getNotes(),transaction.getPayment_method(),transaction.getPaidStatus(),transaction.getMarkedStatus());
        }
        if (settings != null)
            insertSettings(settings.getNameV(),settings.getTypeV(),settings.getNotesV(),settings.getTimeV(),settings.getUserId());
    }
    boolean deleted = false;
    public boolean deleteTransaction(String uuid) {
        Map<String, Object> root = new HashMap<>();
        root.put("action", "delete_transaction");
        root.put("token", token);

        Map<String, String> data = new HashMap<>();
        data.put("id", String.valueOf(uuid));

        root.put("data", data);

        String json = new Gson().toJson(root);

        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json")
        );

        deleted = Boolean.parseBoolean(null);

        deleteTransactionLocal(uuid);

        apiService.SendAndGetData(body).enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    deleted = true;
                    Toast.makeText(Contextt, "Successful transaction deletion", Toast.LENGTH_SHORT).show();

                } else {
                    deleted = false;
                    Toast.makeText(Contextt, "Error deleting transaction", Toast.LENGTH_SHORT).show();
                    Log.e("API", "Server returned error: " + response.message());
                }

                addOfflineAction(TABLE_TRANSACTION,ACTION_DELETE,uuid,json,deleted ? 1 : 0);
            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                deleted = false;
                Log.e("API", "Error: " + t.getMessage());

                addOfflineAction(TABLE_TRANSACTION,ACTION_DELETE,uuid,json,deleted ? 1 : 0);
            }
        });
        /*
        int cunter = 0;
        while (deleted == Boolean.parseBoolean(null)) {
            cunter++;
            Thread.sleep(100);
            if (cunter >= 30){
                // this mens the netilfy did not respond
                deleted = false;
                break;
            }
        }*/
        return deleted;
    }
    public boolean deleteTransactionLocal(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.i("DB","Deleting Transaction where user id = " + id);
        String hexId = id.replace("-", "");
        int rows = db.delete(TABLE_TRANSACTION, COL_TRANSACTION_ID + " = x'" + hexId + "'", null);
        return rows > 0;
    }
    public boolean deleteUser(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.i("DB","Deleting user where user id = " + id);
        int rows = db.delete(TABLE_USER , COL_USER_ID + " = ?", new String[]{String.valueOf(id)});
        return rows > 0;
    }
    boolean added = false;
    public void insertTransaction(String Name, Long Time, Double Amount, String Type, String Notes, String PaymentMethod, boolean Paid, boolean Marked) {
        Map<String, Object> root = new HashMap<>();
        root.put("action", "insert_transaction");
        root.put("token", token);

        UUID uuid = UUID.randomUUID();

        byte[] uuidBytes = Transaction.uuidToBytes(uuid);

        Map<String, String> data = new HashMap<>();
        data.put("id", uuid.toString());
        data.put("name", Name);
        data.put("time", String.valueOf(Time));
        data.put("amount", String.valueOf(Amount));
        data.put("type", Type);
        data.put("notes", Notes);
        data.put("payment_method", PaymentMethod);
        data.put("paid", String.valueOf(Paid ? 1 : 0));
        data.put("bookmark", String.valueOf(Marked ? 1 : 0));

        root.put("data", data);

        String json = new Gson().toJson(root);

        insertTransactionLocal(uuidBytes,user.getId(),Name,Time,Amount,Type,Notes,PaymentMethod,Paid, Marked);

        addOfflineAction(TABLE_TRANSACTION,ACTION_INSART,uuid.toString(),json,0);

        ArrayList<Action> allActions = getOfflineActions();

        syncSingleAction(allActions.get(allActions.size()-1).getId(), uuid.toString(), json);
/*
        apiService.SendAndGetData(body).enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // SUCCESS! You now have your data
                    //Log.v("apiService onResponse:", response.body().print());

                    //String insertId = response.body().getResultSetHeader().getInsertId();
                    Toast.makeText(Contextt, "Successful transaction addition ", Toast.LENGTH_LONG).show();

                    added = true;
                } else {
                    Log.v("apiService onResponse message:", response.message());
                    added = false;
                    Toast.makeText(Contextt, "Error adding transaction", Toast.LENGTH_SHORT).show();
                    try {
                        String errorResponse = response.errorBody() != null ? response.errorBody().string() : "No error body available";
                        Log.e("API", "Server returned error: " + errorResponse);
                    } catch (IOException e) {
                        Log.e("API", "Error reading the error body: " + e.getMessage());
                    }
                }
                addOfflineAction(TABLE_TRANSACTION,ACTION_INSART,uuid.toString(),json,added ? 1 : 0);
            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                added = false;
                Log.e("API", "Error: " + t.getMessage());
                addOfflineAction(TABLE_TRANSACTION,ACTION_INSART,uuid.toString(),json,added ? 1 : 0);
            }
        });
        int cunter = 0;
        while (added == Boolean.parseBoolean(null)) {
            cunter++;
            Thread.sleep(100);
            if (cunter >= 30){
                // this mens the netilfy did not respond
                added = false;
                break;
            }
        }*/
    }
    public long insertTransactionLocal(byte[] id, int userId, String Name, Long Time, Double Amount, String Type, String Notes, String PaymentMethod, boolean Paid, boolean Marked) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_TRANSACTION_ID, id);
        values.put(COL_TRANSACTION_ID_USER_ID, userId);
        values.put(COL_TRANSACTION_NAME, Name);
        values.put(COL_TRANSACTION_TIME, Time);
        values.put(COL_TRANSACTION_AMOUNT, Amount);
        values.put(COL_TRANSACTION_TYPE, Type);
        values.put(COL_TRANSACTION_NOTES, Notes);
        values.put(COL_TRANSACTION_PAYMENT_METHOD, PaymentMethod);
        values.put(COL_TRANSACTION_PAID, Paid ? 1 : 0);
        values.put(COL_TRANSACTION_BOOK_MARK, Marked ? 1 : 0);
        return db.insert(TABLE_TRANSACTION, null, values);  // returns the new user's ID
    }
    public long insertSettings(boolean nameVisibility, boolean typeVisibility, boolean notesVisibility, boolean timeVisibility, int user_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_SETTINGS, "user_id=?", new String[]{String.valueOf(user_id)});

        ContentValues values = new ContentValues();
        values.put(COL_SETTINGS_USER_ID, user_id);
        values.put(COL_SETTINGS_NAME_VISIVILITY, nameVisibility ? 1 : 0);
        values.put(COL_SETTINGS_TYPE_VISIVILITY, typeVisibility ? 1 : 0);
        values.put(COL_SETTINGS_NOTES_VISIVILITY, notesVisibility ? 1 : 0);
        values.put(COL_SETTINGS_TIME_VISIVILITY, timeVisibility ? 1 : 0);

        return db.insert(TABLE_SETTINGS, null, values);
    }
    public long insertUser(int id, String name, String password, String subscription) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, id);
        values.put(COL_USER_NAME, name);
        values.put(COL_USER_PASSWORD, password);
        values.put(COL_USER_SUBSCRIPTION, subscription);

        return db.insert(TABLE_USER, null, values);
    }
    boolean edited = false;
    public boolean editTransaction(String uuid, String Name, Long Time, Double Amount, String Type, String Notes, String PaymentMethod, boolean Paid, boolean Marked) {
        Map<String, Object> root = new HashMap<>();
        root.put("action", "update_transaction");
        root.put("token", token);

        Map<String, String> data = new HashMap<>();
        data.put("id", uuid);
        data.put("name", Name);
        data.put("time", String.valueOf(Time));
        data.put("amount", String.valueOf(Amount));
        data.put("type", Type);
        data.put("notes", Notes);
        data.put("payment_method", PaymentMethod);
        data.put("paid", String.valueOf(Paid ? 1 : 0));
        data.put("bookmark", String.valueOf(Marked ? 1 : 0));

        root.put("data", data);

        String json = new Gson().toJson(root);

        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json")
        );
        ProgressDialog pd = new ProgressDialog(Contextt);
        pd.setMessage("Connecting to database...");
        pd.show();

        edited = Boolean.parseBoolean(null);

        editTransactionLocal(uuid,user.getId(),Name,Time,Amount,Type,Notes,PaymentMethod,Paid,Marked);

        apiService.SendAndGetData(body).enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pd.dismiss();
                    Toast.makeText(Contextt, "Successful transaction modification", Toast.LENGTH_SHORT).show();

                    edited = true;
                } else {
                    pd.dismiss();
                    edited = false;
                    Toast.makeText(Contextt, "Error editing transaction", Toast.LENGTH_SHORT).show();
                    Log.e("API", "Server returned error: " + response.message());
                }
                addOfflineAction(TABLE_TRANSACTION,ACTION_UPDATE,uuid,json,edited ? 1 : 0);
            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                edited = false;
                Log.e("API", "Error: " + t.getMessage());
                Toast.makeText(Contextt, "Error editing transaction", Toast.LENGTH_SHORT).show();
                addOfflineAction(TABLE_TRANSACTION,ACTION_UPDATE,uuid,json,edited ? 1 : 0);
            }
        });/*
        int cunter = 0;
        while (edited == Boolean.parseBoolean(null)) {
            cunter++;
            Thread.sleep(100);
            if (cunter >= 30){
                // this mens the netilfy did not respond
                edited = false;
                break;
            }
        }*/
        return edited;
    }
    public boolean editTransactionLocal(String uuid, int userId, String Name, Long Time, Double Amount, String Type, String Notes, String PaymentMethod, boolean Paid, boolean Marked) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_TRANSACTION_NAME, Name);
        values.put(COL_TRANSACTION_ID_USER_ID, userId);
        values.put(COL_TRANSACTION_TIME, Time);
        values.put(COL_TRANSACTION_AMOUNT, Amount);
        values.put(COL_TRANSACTION_TYPE, Type);
        values.put(COL_TRANSACTION_NOTES, Notes);
        values.put(COL_TRANSACTION_PAYMENT_METHOD, PaymentMethod);
        values.put(COL_TRANSACTION_PAID, Paid ? 1 : 0);
        values.put(COL_TRANSACTION_BOOK_MARK, Marked ? 1 : 0);

        String hexId = uuid.replace("-", "");
        int rows = db.update(TABLE_TRANSACTION, values, COL_TRANSACTION_ID + " = x'" + hexId + "'", null);
        return rows > 0;

    }
    public boolean transactionExists(String transactionId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String hexId = transactionId.replace("-", "");
        Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_TRANSACTION + " WHERE " + COL_TRANSACTION_ID + " = x'" + hexId + "' LIMIT 1",null);
        /*
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + TABLE_TRANSACTION + " WHERE " + COL_TRANSACTION_ID + " = ? LIMIT 1",
                new String[]{Arrays.toString(Transaction.uuidToBytes(UUID.fromString(transactionId)))}
        );*/

        boolean exists = (cursor != null && cursor.moveToFirst()) ;
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }
    public ArrayList<Transaction> getAllTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_TRANSACTION + " ORDER BY " + COL_TRANSACTION_TIME + " DESC", null);
            if (cursor.moveToFirst()) {
                do {
                    byte[] Byte = cursor.getBlob(cursor.getColumnIndexOrThrow(COL_TRANSACTION_ID));
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TRANSACTION_ID_USER_ID));
                    String Name = cursor.getString(cursor.getColumnIndexOrThrow(COL_TRANSACTION_NAME));
                    Long Time = cursor.getLong(cursor.getColumnIndexOrThrow(COL_TRANSACTION_TIME));
                    Double Amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_TRANSACTION_AMOUNT));
                    String Type = cursor.getString(cursor.getColumnIndexOrThrow(COL_TRANSACTION_TYPE));
                    String Notes = cursor.getString(cursor.getColumnIndexOrThrow(COL_TRANSACTION_NOTES));
                    String PaymentMethod = cursor.getString(cursor.getColumnIndexOrThrow(COL_TRANSACTION_PAYMENT_METHOD));

                    int Paid = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TRANSACTION_PAID));
                    int Marked = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TRANSACTION_BOOK_MARK));

                    String Id = Transaction.bytesToUUID(Byte).toString();

                    transactions.add(new Transaction(Id,userId,Name,Time,Amount,Type,Notes,PaymentMethod,Paid,Marked));

                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }
        return transactions;
    }
    public ArrayList<Transaction> getAllTransactionsWhereClause(String whereClause) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_TRANSACTION + " WHERE " + whereClause + " ORDER BY " + COL_TRANSACTION_TIME + " DESC", null);
            if (cursor.moveToFirst()) {
                do {
                    byte[] Byte = cursor.getBlob(cursor.getColumnIndexOrThrow(COL_TRANSACTION_ID));
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TRANSACTION_ID_USER_ID));
                    String Name = cursor.getString(cursor.getColumnIndexOrThrow(COL_TRANSACTION_NAME));
                    Long Time = cursor.getLong(cursor.getColumnIndexOrThrow(COL_TRANSACTION_TIME));
                    Double Amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_TRANSACTION_AMOUNT));
                    String Type = cursor.getString(cursor.getColumnIndexOrThrow(COL_TRANSACTION_TYPE));
                    String Notes = cursor.getString(cursor.getColumnIndexOrThrow(COL_TRANSACTION_NOTES));
                    String PaymentMethod = cursor.getString(cursor.getColumnIndexOrThrow(COL_TRANSACTION_PAYMENT_METHOD));

                    int Paid = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TRANSACTION_PAID));
                    int Marked = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TRANSACTION_BOOK_MARK));

                    String Id = Transaction.bytesToUUID(Byte).toString();

                    transactions.add(new Transaction(Id,userId,Name,Time,Amount,Type,Notes,PaymentMethod,Paid,Marked));

                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }
        return transactions;
    }
    public ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_USER , null);
            if (cursor.moveToFirst()) {
                do {
                    int Id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID));
                    String Name = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME));
                    String Password = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASSWORD));
                    String Subscription = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_SUBSCRIPTION));

                    users.add(new User(Id,Name,Password,Subscription));

                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }
        return users;
    }
    public Transaction getTransactionById(String transactionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Transaction transaction = null;
        //String id = Transaction.uuidToBytes(UUID.fromString(transactionId)).toString();
        String hexId = transactionId.replace("-", "");
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_TRANSACTION + " WHERE " + COL_TRANSACTION_ID + " = x'" + hexId + "'", null);
            /*
            cursor = db.rawQuery(
                    "SELECT * FROM " + TABLE_TRANSACTION + " WHERE " + COL_TRANSACTION_ID + " = ?",
                    new String[]{id}
            );*/
            if (cursor.moveToFirst()) {
                do {
                    byte[] Byte = cursor.getBlob(cursor.getColumnIndexOrThrow(COL_TRANSACTION_ID));
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TRANSACTION_ID_USER_ID));
                    String Name = cursor.getString(cursor.getColumnIndexOrThrow(COL_TRANSACTION_NAME));
                    Long Time = cursor.getLong(cursor.getColumnIndexOrThrow(COL_TRANSACTION_TIME));
                    Double Amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_TRANSACTION_AMOUNT));
                    String Type = cursor.getString(cursor.getColumnIndexOrThrow(COL_TRANSACTION_TYPE));
                    String Notes = cursor.getString(cursor.getColumnIndexOrThrow(COL_TRANSACTION_NOTES));
                    String PaymentMethod = cursor.getString(cursor.getColumnIndexOrThrow(COL_TRANSACTION_PAYMENT_METHOD));

                    int Paid = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TRANSACTION_PAID));
                    int Marked = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TRANSACTION_BOOK_MARK));

                    String Id = Transaction.bytesToUUID(Byte).toString();

                    transaction = new Transaction(Id,userId,Name,Time,Amount,Type,Notes,PaymentMethod,Paid,Marked);

                } while (cursor.moveToNext());

            }
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
            //if (db != null && db.isOpen()) db.close();
        }
        return transaction;
    }
    public void setSettings() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_SETTINGS + " WHERE " + COL_SETTINGS_ID + " = 1", null);
            if (cursor.moveToFirst()) {
                do {

                    int Id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SETTINGS_ID));
                    TransactionAdapter.nameVisibility = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SETTINGS_NAME_VISIVILITY)) == 1;
                    TransactionAdapter.typeVisibility = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SETTINGS_TYPE_VISIVILITY)) == 1;
                    TransactionAdapter.notesVisibility = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SETTINGS_NOTES_VISIVILITY)) == 1;
                    TransactionAdapter.timeVisibility = cursor.getInt((cursor.getColumnIndexOrThrow(COL_SETTINGS_TIME_VISIVILITY))) == 1;

                } while (cursor.moveToNext());

            }
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
            //if (db != null && db.isOpen()) db.close();
        }
    }
    public Settings getSettings() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Settings setting = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_SETTINGS + " WHERE " + COL_SETTINGS_ID + " = 1", null);
            if (cursor.moveToFirst()) {
                do {

                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SETTINGS_ID));
                    int user_id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SETTINGS_USER_ID));
                    boolean nameVisibility = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SETTINGS_NAME_VISIVILITY)) == 1;
                    boolean typeVisibility = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SETTINGS_TYPE_VISIVILITY)) == 1;
                    boolean notesVisibility = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SETTINGS_NOTES_VISIVILITY)) == 1;
                    boolean timeVisibility = cursor.getInt((cursor.getColumnIndexOrThrow(COL_SETTINGS_TIME_VISIVILITY))) == 1;

                    setting = new Settings(id,user_id,nameVisibility,typeVisibility,notesVisibility,timeVisibility);
                } while (cursor.moveToNext());

            }
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
            //if (db != null && db.isOpen()) db.close();
        }
        return setting;
    }
    public boolean updateSettings(boolean nameVisibility, boolean typeVisibility, boolean notesVisibility, boolean timeVisibility, int user_id) {
        Map<String, Object> root = new HashMap<>();
        root.put("action", "update_settings");
        root.put("token", token);

        Map<String, String> data = new HashMap<>();
        data.put("user_id", String.valueOf(user_id));
        data.put("name_visibility", String.valueOf(nameVisibility ? 1 : 0));
        data.put("type_visibility", String.valueOf(typeVisibility ? 1 : 0));
        data.put("notes_visibility", String.valueOf(notesVisibility ? 1 : 0));
        data.put("time_visibility", String.valueOf(timeVisibility ? 1 : 0));

        root.put("data", data);

        String json = new Gson().toJson(root);

        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json")
        );

        edited = Boolean.parseBoolean(null);

        insertSettings(nameVisibility, typeVisibility, notesVisibility, timeVisibility,user.getId());
        setSettings();

        apiService.SendAndGetData(body).enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // SUCCESS! You now have your data
                    Toast.makeText(Contextt, "Successful settings modification", Toast.LENGTH_SHORT).show();
                    edited = true;
                } else {
                    edited = false;
                    Toast.makeText(Contextt, "Error editing settings", Toast.LENGTH_SHORT).show();
                    Log.e("API", "Server returned error: " + response.message());
                }
                addOfflineAction(TABLE_SETTINGS,ACTION_UPDATE,String.valueOf(user.getId()),json,edited ? 1 : 0);
            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                edited = false;
                Log.e("API", "Error: " + t.getMessage());
                addOfflineAction(TABLE_SETTINGS,ACTION_UPDATE,String.valueOf(user.getId()),json,edited ? 1 : 0);

            }
        });/*
        int cunter = 0;
        while (edited == Boolean.parseBoolean(null)) {
            cunter++;
            Thread.sleep(100);
            if (cunter >= 30){
                // this mens the netilfy did not respond
                edited = false;
                break;
            }
        }*/
        return edited;
    }
    public ArrayList<Transaction> searchTransactionByEverything(String SearchText ,int marked ,int paid) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String q = "%" + SearchText + "%";

            cursor = db.rawQuery(
                    "SELECT * FROM " + TABLE_TRANSACTION + " WHERE " +
                            COL_TRANSACTION_ID + " = ? OR " +
                            COL_TRANSACTION_NAME + " LIKE ? OR " +
                            COL_TRANSACTION_TYPE + " LIKE ? OR " +
                            COL_TRANSACTION_NOTES + " LIKE ? OR " +
                            COL_TRANSACTION_PAYMENT_METHOD + " LIKE ? OR " +
                            "strftime('%Y-%m-%d', " + COL_TRANSACTION_TIME + ", 'unixepoch', 'localtime') LIKE ? OR " +
                            "CAST(" + COL_TRANSACTION_AMOUNT + " AS TEXT) LIKE ? OR " +
                            "CAST(" + COL_TRANSACTION_PAID + " AS TEXT) LIKE ? " +
                            " ORDER BY " + COL_TRANSACTION_TIME + " DESC",
                    new String[]{
                            SearchText, // ID exact match
                            q,          // name
                            q,          // type
                            q,          // notes
                            q,          // payment method
                            q,          // time
                            q,          // amount
                            q           // paid
                    }
            );
            if (cursor.moveToFirst()) {
                do {
                    byte[] Byte = cursor.getBlob(cursor.getColumnIndexOrThrow(COL_TRANSACTION_ID));
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TRANSACTION_ID_USER_ID));
                    String Name = cursor.getString(cursor.getColumnIndexOrThrow(COL_TRANSACTION_NAME));
                    Long Time = cursor.getLong(cursor.getColumnIndexOrThrow(COL_TRANSACTION_TIME));
                    Double Amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_TRANSACTION_AMOUNT));
                    String Type = cursor.getString(cursor.getColumnIndexOrThrow(COL_TRANSACTION_TYPE));
                    String Notes = cursor.getString(cursor.getColumnIndexOrThrow(COL_TRANSACTION_NOTES));
                    String PaymentMethod = cursor.getString(cursor.getColumnIndexOrThrow(COL_TRANSACTION_PAYMENT_METHOD));

                    int Paid = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TRANSACTION_PAID));
                    int Marked = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TRANSACTION_BOOK_MARK));

                    String Id = Transaction.bytesToUUID(Byte).toString();

                   // if (userId != user)
                    if (marked == Marked && paid == Paid) {
                        transactions.add(new Transaction(Id, userId, Name, Time, Amount, Type, Notes, PaymentMethod, Paid, Marked));
                    }

                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }
        return transactions;
    }
    public int getTransactionCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TRANSACTION, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        if (!cursor.isClosed()) cursor.close();
        //if (db.isOpen()) db.close();
        return count;
    }
    public int getUserCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USER, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        if (!cursor.isClosed()) cursor.close();
        //if (db.isOpen()) db.close();
        return count;
    }
    public void addOfflineAction(String tableName, String actionType, String localId, String paload, int synced) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cursor = new ContentValues();

        cursor.put(COL_ACTIONS_TABLE_NAME, tableName);
        cursor.put(COL_ACTIONS_ACTION_TYPE, actionType);
        cursor.put(COL_ACTIONS_LOCAL_ID, localId);
        cursor.put(COL_ACTIONS_PAYLOAD, paload);
        cursor.put(COL_ACTIONS_TIME, System.currentTimeMillis());
        cursor.put(COL_ACTIONS_SYNCED, synced);

        Log.i("addOfflineAction",  "tableName: " + tableName + ",actionType: " + actionType + ",localId: " + localId + ",paload: " + paload + ",synced: " + synced);
        db.insert(TABLE_OFFLINE_ACTIONS, null, cursor);
    }
    public ArrayList<Action> getOfflineActions(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        ArrayList<Action> Actions = null;

        try {
            cursor = db.rawQuery(
                    "SELECT * FROM " + TABLE_OFFLINE_ACTIONS + " WHERE " + COL_ACTIONS_SYNCED + " = ? ORDER BY " + COL_ACTIONS_TIME + " ASC",
                    new String[]{"0"});
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ACTIONS_ID));
                    String local_id = cursor.getString(cursor.getColumnIndexOrThrow(COL_ACTIONS_LOCAL_ID));
                    int server_id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ACTIONS_SREVER_ID));
                    String paload = cursor.getString(cursor.getColumnIndexOrThrow(COL_ACTIONS_PAYLOAD));
                    String table_name = cursor.getString(cursor.getColumnIndexOrThrow(COL_ACTIONS_TABLE_NAME));
                    String action_type = cursor.getString(cursor.getColumnIndexOrThrow(COL_ACTIONS_ACTION_TYPE));
                    long time = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ACTIONS_TIME));
                    int synced = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ACTIONS_SYNCED));

                    Actions.add(new Action(id,local_id,server_id,table_name,action_type,time,synced,paload));

                } while (cursor.moveToNext());

            }
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }
        return Actions;
    }
    public void setAsSyncedOfflineAction(int Id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cursor = new ContentValues();

        cursor.put(COL_ACTIONS_SYNCED, 1);

        db.update(TABLE_OFFLINE_ACTIONS, cursor, COL_ACTIONS_ID + "=?", new String[]{String.valueOf(Id)});
    }
    public boolean deleteSetting(int id) throws InterruptedException {
        Map<String, Object> root = new HashMap<>();
        root.put("action", "delete_setting");
        root.put("token", token);

        Map<String, String> data = new HashMap<>();
        data.put("id", String.valueOf(id));

        root.put("data", data);

        String json = new Gson().toJson(root);

        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json")
        );
        ProgressDialog pd = new ProgressDialog(Contextt);
        pd.setMessage("Connecting to database...");
        pd.show();

        deleted = Boolean.parseBoolean(null);
        apiService.SendAndGetData(body).enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                pd.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(Contextt, "Successful setting deletion", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(Contextt, statisticsActivity.class);
                    Contextt.startActivity(intent1);
                    deleted = true;
                } else {
                    deleted = false;
                    Toast.makeText(Contextt, "Error setting transaction", Toast.LENGTH_SHORT).show();
                    Log.e("API", "Server returned error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                deleted = false;
                Log.e("API", "Error: " + t.getMessage());
            }
        });
        int cunter = 0;
        while (deleted == Boolean.parseBoolean(null)) {
            cunter++;
            Thread.sleep(100);
            if (cunter >= 30){
                // this mens the netilfy did not respond
                deleted = false;
                break;
            }
        }
        return deleted;
    }
}

/*
public boolean updateUserIp(int userId, String newIp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_IP, newIp);
        int rows = db.update(TABLE_USER, values, COL_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        return rows > 0;
    }
 */