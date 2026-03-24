package com.example.accountingandmanagement;

import static com.example.accountingandmanagement.MainActivity.token;
import static com.example.accountingandmanagement.MainActivity.user;

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
    public static final int DATABASE_VERSION = 1;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://money-management-0301.netlify.app/") // this is the middle man with the database 🫡
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private ApiService apiService = retrofit.create(ApiService.class);

    Context Contextt;
    // tables
    public static final String TABLE_TRANSACTION = "transactions";
    public static final String TABLE_SETTINGS = "settings";
    public static final String TABLE_USER = "user";
    public static final String TABLE_OFFLINE_ACTIONS = "offline_actions";
    public static final String TABLE_PRODUCTS_DATA = "products_data";
    public static final String TABLE_PRODUCTS = "products";

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
    public static final String COL_TRANSACTION_BOOKMARK = "bookmark";
    public static final String COL_SETTINGS_ID = "id";
    public static final String COL_SETTINGS_NAME_VISIBILITY = "name_visibility";
    public static final String COL_SETTINGS_TYPE_VISIBILITY = "type_visibility";
    public static final String COL_SETTINGS_NOTES_VISIBILITY = "notes_visibility";
    public static final String COL_SETTINGS_TIME_VISIBILITY = "time_visibility";
    public static final String COL_SETTINGS_PAYMENT_METHOD_LIST = "payment_method_list";
    public static final String COL_SETTINGS_TYPE_LIST = "type_list";
    public static final String COL_SETTINGS_USER_ID = "user_id";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_USERNAME = "username";
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
    public static final String COL_PRODUCTS_DATA_ID = "id";
    public static final String COL_PRODUCTS_DATA_NAME = "name";
    public static final String COL_PRODUCTS_DATA_TYPE = "type";
    public static final String COL_PRODUCTS_ID = "id";
    public static final String COL_PRODUCTS_TRANSACTION_ID = "transaction_id";
    public static final String COL_PRODUCTS_COUNT = "count";
    public static final String COL_PRODUCTS_EXPIRY_DATE = "expiry_date";
    public static final String COL_PRODUCTS_PRICE = "price";

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
                COL_TRANSACTION_BOOKMARK + " INTEGER)");

        db.execSQL("CREATE TABLE `"+ TABLE_SETTINGS +"` (" +
                COL_SETTINGS_ID + " INTEGER PRIMARY KEY, " +
                COL_SETTINGS_NAME_VISIBILITY + " INTEGER, " +
                COL_SETTINGS_TYPE_VISIBILITY + " INTEGER, " +
                COL_SETTINGS_NOTES_VISIBILITY + " INTEGER, " +
                COL_SETTINGS_TIME_VISIBILITY + " INTEGER, " +
                COL_SETTINGS_USER_ID + " INTEGER, " +
                COL_SETTINGS_PAYMENT_METHOD_LIST + " TEXT, " +
                COL_SETTINGS_TYPE_LIST + " TEXT);");

        db.execSQL("CREATE TABLE `" + TABLE_USER + "` (" +
                COL_USER_ID + " INTEGER PRIMARY KEY, " +
                COL_USER_USERNAME + " TEXT, " +
                COL_USER_PASSWORD + " TEXT, " +
                COL_USER_SUBSCRIPTION + " TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS `" + TABLE_OFFLINE_ACTIONS + "` (" +
                COL_ACTIONS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ACTIONS_TABLE_NAME + " TEXT NOT NULL," +
                COL_ACTIONS_ACTION_TYPE + " TEXT NOT NULL," +
                COL_ACTIONS_LOCAL_ID + " TEXT NOT NULL," +
                COL_ACTIONS_SREVER_ID + " INTEGER ," +
                COL_ACTIONS_PAYLOAD + " TEXT ," +
                COL_ACTIONS_TIME + " INTEGER ," +
                COL_ACTIONS_SYNCED + " INTEGER)");

        db.execSQL("CREATE TABLE `" + TABLE_PRODUCTS_DATA + "` (" +
                COL_PRODUCTS_DATA_ID + " TEXT PRIMARY KEY, " +
                COL_PRODUCTS_DATA_NAME + " TEXT, " +
                COL_PRODUCTS_DATA_TYPE + " TEXT)");

        db.execSQL("CREATE TABLE `" + TABLE_PRODUCTS + "` (" +
                COL_PRODUCTS_ID + " TEXT, " +
                COL_PRODUCTS_TRANSACTION_ID + " BINARY(16), " +
                COL_PRODUCTS_COUNT + " INT, " +
                COL_PRODUCTS_EXPIRY_DATE + " INT(11), " +
                COL_PRODUCTS_PRICE + " DOUBLE," +
                "PRIMARY KEY (" + COL_PRODUCTS_ID + ", " + COL_PRODUCTS_TRANSACTION_ID + "))");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_OFFLINE_ACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    public void syncSingleAction(int ActionId, String json){
        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json")
        );
        Log.i("SYNCING-ACTION","Action id: " + ActionId + ", json: " + json);
        apiService.SendAndGetData(body).enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {

                if (response.isSuccessful()) {
                    Log.i("ACTION-SYNCED","Action id: " + ActionId);
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

        if (actions == null || actions.size() == 0) return;
        for (Action action : actions) {
            syncSingleAction(action.getId(), action.getPaload());
        }
        //TODO make database update from clued 
    }
    public void deleteDatabase(User user, ArrayList<Transaction> transactions, Settings settings, ArrayList<ProductData> productsData, ArrayList<Product> products){ // TODO add TABLE_PRODUCTS_DATA and TABLE_PRODUCTS to online Database
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
            insertSettings(settings.getNameV(),settings.getTypeV(),settings.getNotesV(),settings.getTimeV(),settings.getUserId(),settings.getPaymentMethodList(),settings.getTypeList());

        for (ProductData productData:productsData){
            insertProductDataLocal(productData.getId(),productData.getName(),productData.getType());
        }
        for (Product product:products){
            insertProductLocal(product.getId(),product.getTransactionId(),product.getCunt(),product.getExpiryDate(),product.getPrice());
        }
    }
    public void insertProductData(String id, String name, String type){
        Map<String, Object> root = new HashMap<>();
        root.put("action", "insert_product_data");
        root.put("token", token);

        Map<String, String> data = new HashMap<>();
        data.put(COL_PRODUCTS_DATA_ID,id);
        data.put(COL_PRODUCTS_DATA_NAME,name);
        data.put(COL_PRODUCTS_DATA_TYPE,type);

        root.put("data", data);

        String json = new Gson().toJson(root);

        insertProductDataLocal(id, name, type);

        long action_id = addOfflineAction(TABLE_PRODUCTS_DATA,ACTION_INSART,id,json,0);

        //ArrayList<Action> allActions = getOfflineActions();
        //syncSingleAction(allActions.get(allActions.size()-1).getId(), json);
        syncSingleAction((int) action_id, json);
    }
    public long insertProductDataLocal(String id, String name, String type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_PRODUCTS_ID,id);
        values.put(COL_PRODUCTS_DATA_NAME,name);
        values.put(COL_PRODUCTS_DATA_TYPE,type);

        Toast.makeText(Contextt, "product data inserted", Toast.LENGTH_SHORT).show();
        return db.insert(TABLE_PRODUCTS_DATA, null, values);
    }
    public void updateProductData(String id, String name, String type){
        Map<String, Object> root = new HashMap<>();
        root.put("action", "update_product_data");
        root.put("token", token);

        Map<String, String> data = new HashMap<>();
        data.put(COL_PRODUCTS_DATA_ID,id);
        data.put(COL_PRODUCTS_DATA_NAME,name);
        data.put(COL_PRODUCTS_DATA_TYPE,type);

        root.put("data", data);

        String json = new Gson().toJson(root);

        updateProductDataLocal(id, name, type);

        addOfflineAction(TABLE_PRODUCTS_DATA,ACTION_UPDATE,id,json,0);

        ArrayList<Action> allActions = getOfflineActions();

        syncSingleAction(allActions.get(allActions.size()-1).getId(), json);
    }
    public boolean updateProductDataLocal(String id, String name, String type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_PRODUCTS_DATA_NAME,name);
        values.put(COL_PRODUCTS_DATA_TYPE,type);

        Toast.makeText(Contextt, "product data updated", Toast.LENGTH_SHORT).show();
        int rows = db.update(TABLE_PRODUCTS_DATA, values, COL_PRODUCTS_ID + " = " + id,null);
        return rows > 0;
    }
    public void deleteProductData(String id){
        Map<String, Object> root = new HashMap<>();
        root.put("action", "delete_product_data");
        root.put("token", token);

        Map<String, String> data = new HashMap<>();
        data.put("id", id);

        root.put("data", data);

        String json = new Gson().toJson(root);

        deleteProductDataLocal(id);

        int action_id = (int) addOfflineAction(TABLE_PRODUCTS_DATA,ACTION_DELETE,id,json,0);

        syncSingleAction(action_id, json);

    }
    public boolean deleteProductDataLocal(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.i("DB","Deleting Product where user id = " + id);

        Toast.makeText(Contextt, "product data deleted", Toast.LENGTH_SHORT).show();
        int rows = db.delete(TABLE_PRODUCTS_DATA, COL_PRODUCTS_ID + " = " + id, null);
        return rows > 0;
    }
    public void insertProduct(String id,byte[] transaction_uuid, int count, long expiryDate, double price){
        Map<String, Object> root = new HashMap<>();
        root.put("action", "insert_product");
        root.put("token", token);

        Map<String, String> data = new HashMap<>();
        data.put(COL_PRODUCTS_ID,id);
        data.put(COL_PRODUCTS_TRANSACTION_ID,transaction_uuid.toString());
        data.put(COL_PRODUCTS_COUNT, String.valueOf(count));
        data.put(COL_PRODUCTS_EXPIRY_DATE, String.valueOf(expiryDate));
        data.put(COL_PRODUCTS_PRICE, String.valueOf(price));

        root.put("data", data);

        String json = new Gson().toJson(root);

        insertProductLocal(id,transaction_uuid,count,expiryDate,price);

        long action_id = addOfflineAction(TABLE_PRODUCTS,ACTION_INSART,id + "," + transaction_uuid.toString(),json,0);

        syncSingleAction((int) action_id, json);
    }
    public long insertProductLocal(String id,byte[] transaction_uuid, int count, Long expiryDate, double price){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_PRODUCTS_ID,id);
        values.put(COL_PRODUCTS_TRANSACTION_ID,transaction_uuid);
        values.put(COL_PRODUCTS_COUNT,count);
        values.put(COL_PRODUCTS_EXPIRY_DATE,expiryDate);
        values.put(COL_PRODUCTS_PRICE,price);

        Toast.makeText(Contextt, "product inserted", Toast.LENGTH_SHORT).show();
        return db.insert(TABLE_PRODUCTS, null, values);
    }
    public void updateProduct(String id,byte[] transaction_uuid, int count, Long expiryDate, double price){ // these don't change id and transaction_uuid
        Map<String, Object> root = new HashMap<>();
        root.put("action", "update_product");
        root.put("token", token);

        Map<String, String> data = new HashMap<>();
        data.put(COL_PRODUCTS_ID,id);
        data.put(COL_PRODUCTS_TRANSACTION_ID,transaction_uuid.toString());
        data.put(COL_PRODUCTS_COUNT, String.valueOf(count));
        data.put(COL_PRODUCTS_EXPIRY_DATE, String.valueOf(expiryDate));
        data.put(COL_PRODUCTS_PRICE, String.valueOf(price));

        root.put("data", data);

        String json = new Gson().toJson(root);

        updateProductLocal(id,transaction_uuid,count,expiryDate,price);

        addOfflineAction(TABLE_PRODUCTS,ACTION_UPDATE,id + "," + transaction_uuid.toString(),json,0);

        ArrayList<Action> allActions = getOfflineActions();

        syncSingleAction(allActions.get(allActions.size()-1).getId(), json);
    }
    public boolean updateProductLocal(String id,byte[] transaction_uuid, int count, Long expiryDate, double price){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_PRODUCTS_COUNT,count);
        values.put(COL_PRODUCTS_EXPIRY_DATE,expiryDate);
        values.put(COL_PRODUCTS_PRICE,price);

        String uuid = Transaction.bytesToUUID(transaction_uuid).toString();
        String hexId = uuid.replace("-", "");

        Toast.makeText(Contextt, "product updated", Toast.LENGTH_SHORT).show();
        int rows = db.update(TABLE_PRODUCTS, values, COL_PRODUCTS_ID + " = " + id + " AND " + COL_PRODUCTS_TRANSACTION_ID + " = x'" + hexId + "'",null);
        return rows > 0;
    }
    public void deleteProduct(String id,byte[] transaction_uuid){
        Map<String, Object> root = new HashMap<>();
        root.put("action", "delete_product");
        root.put("token", token);

        Map<String, String> data = new HashMap<>();
        data.put(COL_PRODUCTS_ID, id);
        data.put(COL_PRODUCTS_TRANSACTION_ID, transaction_uuid.toString());

        root.put("data", data);

        String json = new Gson().toJson(root);

        deleteProductLocal(id,transaction_uuid);

        int action_id = (int) addOfflineAction(TABLE_PRODUCTS,ACTION_DELETE,id + "," + transaction_uuid.toString(),json,0);

        syncSingleAction(action_id, json);
    }
    public boolean deleteProductLocal(String id,byte[] transaction_uuid){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.i("DB","Deleting Product where user id = " + id + " and transaction UUID = " + Transaction.bytesToUUID(transaction_uuid));

        String uuid = Transaction.bytesToUUID(transaction_uuid).toString();
        String hexId = uuid.replace("-", "");

        Toast.makeText(Contextt, "product deleted", Toast.LENGTH_SHORT).show();
        int rows = db.delete(TABLE_PRODUCTS, COL_PRODUCTS_ID + " = " + id + " AND " + COL_PRODUCTS_TRANSACTION_ID + " = x'" + hexId + "'", null);
        return rows > 0;
    }
    public void deleteTransaction(String uuid) {
        Map<String, Object> root = new HashMap<>();
        root.put("action", "delete_transaction");
        root.put("token", token);

        Map<String, String> data = new HashMap<>();
        data.put("id", String.valueOf(uuid));

        root.put("data", data);

        String json = new Gson().toJson(root);

        deleteTransactionLocal(uuid);

        addOfflineAction(TABLE_TRANSACTION,ACTION_DELETE,uuid,json,0);

        ArrayList<Action> allActions = getOfflineActions();

        syncSingleAction(allActions.get(allActions.size()-1).getId(), json);

        /*RequestBody body = RequestBody.create(
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
        return deleted;*/
    }
    public boolean deleteTransactionLocal(String uuid){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.i("DB","Deleting Transaction where user id = " + uuid);
        String hexId = uuid.replace("-", "");
        int rows = db.delete(TABLE_TRANSACTION, COL_TRANSACTION_ID + " = x'" + hexId + "'", null);
        return rows > 0;
    }
    public boolean deleteUser(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.i("DB","Deleting user where user id = " + id);
        int rows = db.delete(TABLE_USER , COL_USER_ID + " = ?", new String[]{String.valueOf(id)});
        return rows > 0;
    }
    public void insertTransaction(String Name, Long Time, Double Amount, String Type, String Notes, String PaymentMethod, boolean Paid, boolean Marked) {
        Map<String, Object> root = new HashMap<>();
        root.put("action", "insert_transaction");
        root.put("token", token);

        UUID uuid = UUID.randomUUID();

        byte[] uuidBytes = Transaction.uuidToBytes(uuid);

        Map<String, String> data = new HashMap<>();
        data.put(COL_TRANSACTION_ID, uuid.toString());
        data.put(COL_TRANSACTION_NAME, Name);
        data.put(COL_TRANSACTION_TIME, String.valueOf(Time));
        data.put(COL_TRANSACTION_AMOUNT, String.valueOf(Amount));
        data.put(COL_TRANSACTION_TYPE, Type);
        data.put(COL_TRANSACTION_NOTES, Notes);
        data.put(COL_TRANSACTION_PAYMENT_METHOD, PaymentMethod);
        data.put(COL_TRANSACTION_PAID, String.valueOf(Paid ? 1 : 0));
        data.put(COL_TRANSACTION_BOOKMARK, String.valueOf(Marked ? 1 : 0));

        root.put("data", data);

        String json = new Gson().toJson(root);

        insertTransactionLocal(uuidBytes,user.getId(),Name,Time,Amount,Type,Notes,PaymentMethod,Paid, Marked);

        int action_id = (int) addOfflineAction(TABLE_TRANSACTION,ACTION_INSART,uuid.toString(),json,0);

        //ArrayList<Action> allActions = getOfflineActions();
        //syncSingleAction(allActions.get(allActions.size()-1).getId(), json);
        syncSingleAction(action_id, json);
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
        values.put(COL_TRANSACTION_BOOKMARK, Marked ? 1 : 0);

        return db.insert(TABLE_TRANSACTION, null, values);  // returns the new user's ID
    }
    public long insertSettings(boolean nameVisibility, boolean typeVisibility, boolean notesVisibility, boolean timeVisibility, int user_id, String paymentMethodList, String typeList) {
        SQLiteDatabase db = this.getWritableDatabase();

        //db.delete(TABLE_SETTINGS, "user_id=?", new String[]{String.valueOf(user_id)});

        ContentValues values = new ContentValues();
        values.put(COL_SETTINGS_USER_ID, user_id);
        values.put(COL_SETTINGS_NAME_VISIBILITY, nameVisibility ? 1 : 0);
        values.put(COL_SETTINGS_TYPE_VISIBILITY, typeVisibility ? 1 : 0);
        values.put(COL_SETTINGS_NOTES_VISIBILITY, notesVisibility ? 1 : 0);
        values.put(COL_SETTINGS_TIME_VISIBILITY, timeVisibility ? 1 : 0);
        values.put(COL_SETTINGS_PAYMENT_METHOD_LIST, paymentMethodList);
        values.put(COL_SETTINGS_TYPE_LIST, typeList);

        return db.insert(TABLE_SETTINGS, null, values);
    }
    public long insertUser(int id, String name, String password, String subscription) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, id);
        values.put(COL_USER_USERNAME, name);
        values.put(COL_USER_PASSWORD, password);
        values.put(COL_USER_SUBSCRIPTION, subscription);

        return db.insert(TABLE_USER, null, values);
    }
    public void updateTransaction(String uuid, String Name, Long Time, Double Amount, String Type, String Notes, String PaymentMethod, boolean Paid, boolean Marked, String products) {
        Map<String, Object> root = new HashMap<>();
        root.put("action", "update_transaction");
        root.put("token", token);

        Map<String, String> data = new HashMap<>();
        data.put(COL_TRANSACTION_ID, uuid);
        data.put(COL_TRANSACTION_NAME, Name);
        data.put(COL_TRANSACTION_TIME, String.valueOf(Time));
        data.put(COL_TRANSACTION_AMOUNT, String.valueOf(Amount));
        data.put(COL_TRANSACTION_TYPE, Type);
        data.put(COL_TRANSACTION_NOTES, Notes);
        data.put(COL_TRANSACTION_PAYMENT_METHOD, PaymentMethod);
        data.put(COL_TRANSACTION_PAID, String.valueOf(Paid ? 1 : 0));
        data.put(COL_TRANSACTION_BOOKMARK, String.valueOf(Marked ? 1 : 0));

        root.put("data", data);

        String json = new Gson().toJson(root);

        updateTransactionLocal(uuid,user.getId(),Name,Time,Amount,Type,Notes,PaymentMethod,Paid,Marked,products);

        int action_id = (int) addOfflineAction(TABLE_TRANSACTION,ACTION_UPDATE,uuid,json,0);

        //ArrayList<Action> allActions = getOfflineActions();
        //syncSingleAction(allActions.get(allActions.size()-1).getId(), json);

        syncSingleAction(action_id, json);

        /*RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json")
        );

        edited = Boolean.parseBoolean(null);

        editTransactionLocal(uuid,user.getId(),Name,Time,Amount,Type,Notes,PaymentMethod,Paid,Marked,products);

        apiService.SendAndGetData(body).enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(Contextt, "Successful transaction modification", Toast.LENGTH_SHORT).show();

                    edited = true;
                } else {
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
        });
        int cunter = 0;
        while (edited == Boolean.parseBoolean(null)) {
            cunter++;
            Thread.sleep(100);
            if (cunter >= 30){
                // this mens the netilfy did not respond
                edited = false;
                break;
            }
        }
        return edited;*/
    }
    public boolean updateTransactionLocal(String uuid, int userId, String Name, Long Time, Double Amount, String Type, String Notes, String PaymentMethod, boolean Paid, boolean Marked, String products) {
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
        values.put(COL_TRANSACTION_BOOKMARK, Marked ? 1 : 0);

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
    public ArrayList<Product> getAllProdcuts(){
        ArrayList<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS,null);
            if (cursor.moveToFirst()){
                do {
                  String id = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCTS_ID));
                  byte[] transactionId = cursor.getBlob(cursor.getColumnIndexOrThrow(COL_PRODUCTS_TRANSACTION_ID));
                  int count = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCTS_COUNT));
                  Long expiryDate = cursor.getLong(cursor.getColumnIndexOrThrow(COL_PRODUCTS_EXPIRY_DATE));
                  double prics = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRODUCTS_PRICE));

                  products.add(new Product(id,transactionId,count,expiryDate,prics));

                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }


        return products;
    }
    public ArrayList<Product> getAllProdcutsWhereClause(String whereClause){
        ArrayList<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + whereClause,null);
            if (cursor.moveToFirst()){
                do {
                    String id = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCTS_ID));
                    byte[] transactionId = cursor.getBlob(cursor.getColumnIndexOrThrow(COL_PRODUCTS_TRANSACTION_ID));
                    int count = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCTS_COUNT));
                    Long expiryDate = cursor.getLong(cursor.getColumnIndexOrThrow(COL_PRODUCTS_EXPIRY_DATE));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRODUCTS_PRICE));

                    products.add(new Product(id,transactionId,count,expiryDate,price));

                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }

        return products;
    }
    public ArrayList<ProductData> getAllProdcutsData(){
        ArrayList<ProductData> productData = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS_DATA,null);
            if (cursor.moveToFirst()){
                do {
                    String id = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCTS_DATA_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCTS_DATA_NAME));
                    String expiryDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCTS_DATA_TYPE));

                    productData.add(new ProductData(id,name,expiryDate));

                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }


        return productData;
    }
    public ArrayList<ProductData> getAllProdcutsDataWhereClause(String whereClause){
        ArrayList<ProductData> productData = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS_DATA + " WHERE " + whereClause,null);
            if (cursor.moveToFirst()){
                do {
                    String id = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCTS_DATA_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCTS_DATA_NAME));
                    String expiryDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCTS_DATA_TYPE));

                    productData.add(new ProductData(id,name,expiryDate));

                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }


        return productData;
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
                    int Marked = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TRANSACTION_BOOKMARK));

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
                    int Marked = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TRANSACTION_BOOKMARK));

                    String Id = Transaction.bytesToUUID(Byte).toString();

                    transactions.add(new Transaction(Id,userId,Name,Time,Amount,Type,Notes,PaymentMethod,Paid,Marked));

                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }
        return transactions;
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
                    int Marked = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TRANSACTION_BOOKMARK));

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
    public ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_USER , null);
            if (cursor.moveToFirst()) {
                do {
                    int Id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID));
                    String Name = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_USERNAME));
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
    public void setSettings() {

        Settings settings = getSettings();

        TransactionAdapter.settings = settings;

        /*SQLiteDatabase db = this.getReadableDatabase();
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
        }*/
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
                    boolean nameVisibility = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SETTINGS_NAME_VISIBILITY)) == 1;
                    boolean typeVisibility = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SETTINGS_TYPE_VISIBILITY)) == 1;
                    boolean notesVisibility = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SETTINGS_NOTES_VISIBILITY)) == 1;
                    boolean timeVisibility = cursor.getInt((cursor.getColumnIndexOrThrow(COL_SETTINGS_TIME_VISIBILITY))) == 1;
                    String paymentPethodList = cursor.getString((cursor.getColumnIndexOrThrow(COL_SETTINGS_PAYMENT_METHOD_LIST)));
                    String typeList = cursor.getString((cursor.getColumnIndexOrThrow(COL_SETTINGS_TYPE_LIST)));

                    setting = new Settings(id,user_id,nameVisibility,typeVisibility,notesVisibility,timeVisibility,paymentPethodList,typeList);
                } while (cursor.moveToNext());

            }
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
            //if (db != null && db.isOpen()) db.close();
        }
        return setting;
    }
    /*public boolean deleteSetting(int id) throws InterruptedException {
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
    }*/
    public void updateSettings(boolean nameVisibility, boolean typeVisibility, boolean notesVisibility, boolean timeVisibility, int user_id, String paymetMethodList, String typeList) {
        SQLiteDatabase db = this.getWritableDatabase();
        Map<String, Object> root = new HashMap<>();
        root.put("action", "update_settings");
        root.put("token", token);

        Map<String, String> data = new HashMap<>();
        data.put(COL_SETTINGS_USER_ID, String.valueOf(user_id));
        data.put(COL_SETTINGS_NAME_VISIBILITY, String.valueOf(nameVisibility ? 1 : 0));
        data.put(COL_SETTINGS_TYPE_VISIBILITY, String.valueOf(typeVisibility ? 1 : 0));
        data.put(COL_SETTINGS_NOTES_VISIBILITY, String.valueOf(notesVisibility ? 1 : 0));
        data.put(COL_SETTINGS_TIME_VISIBILITY, String.valueOf(timeVisibility ? 1 : 0));
        data.put(COL_SETTINGS_PAYMENT_METHOD_LIST, paymetMethodList);
        data.put(COL_SETTINGS_TYPE_LIST, typeList);

        root.put("data", data);

        String json = new Gson().toJson(root);

        db.delete(TABLE_SETTINGS,"1 = 1",null);

        insertSettings(nameVisibility, typeVisibility, notesVisibility, timeVisibility, user.getId(), paymetMethodList, typeList);
        setSettings();

        int action_id = (int) addOfflineAction(TABLE_SETTINGS,ACTION_UPDATE,String.valueOf(user.getId()),json,0);

        //ArrayList<Action> allActions = getOfflineActions();
        //syncSingleAction(allActions.get(allActions.size()-1).getId(), json);
        syncSingleAction(action_id, json);

        /*
        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json")
        );

        edited = Boolean.parseBoolean(null);

        db.delete(TABLE_SETTINGS,"1 = 1",null);

        insertSettings(nameVisibility, typeVisibility, notesVisibility, timeVisibility, user.getId(), paymetMethodList, typeList);
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
        });
        int cunter = 0;
        while (edited == Boolean.parseBoolean(null)) {
            cunter++;
            Thread.sleep(100);
            if (cunter >= 30){
                // this mens the netilfy did not respond
                edited = false;
                break;
            }
        }
        return edited;*/
    }
    /*public void setSettingsFromCloud() {
        SQLiteDatabase db = this.getWritableDatabase();

        Map<String, Object> root = new HashMap<>();
        root.put("action", "get_settings");
        root.put("token", token);

        String json = new Gson().toJson(root);

        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json")
        );

        apiService.SendAndGetData(body).enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Settings settings = response.body().settings;

                    db.delete(TABLE_SETTINGS,"1 = 1",null);

                    insertSettings(settings.getNameV(),settings.getTypeV(),settings.getNotesV(),settings.getTimeV(),user.getId());

                } else {
                    Log.v("apiService onResponse message:", response.message());
                    try {
                        String errorResponse = response.errorBody() != null ? response.errorBody().string() : "No error body available";
                        Log.e("API", "Server returned error: " + errorResponse);
                    } catch (IOException e) {
                        Log.e("API", "Error reading the error body: " + e.getMessage());
                    }
                }
            }
            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                added = false;
                Log.e("API", "Error: " + t.getMessage());
            }
        });
    }*/
    public ArrayList<Transaction> searchTransactionByEverything(String SearchText ,int marked ,int paid, int searchSettings) {
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
                            q,
                            q,
                            q,
                            q,
                            q,
                            q,
                            q
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
                    int Marked = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TRANSACTION_BOOKMARK));

                    String Id = Transaction.bytesToUUID(Byte).toString();


                    if (searchSettings == 0 || (marked == Marked && paid == Paid)) {
                        transactions.add(new Transaction(Id,userId,Name,Time,Amount,Type,Notes,PaymentMethod,Paid,Marked));
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
    public long addOfflineAction(String tableName, String actionType, String localId, String paload, int synced) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cursor = new ContentValues();

        cursor.put(COL_ACTIONS_TABLE_NAME, tableName);
        cursor.put(COL_ACTIONS_ACTION_TYPE, actionType);
        cursor.put(COL_ACTIONS_LOCAL_ID, localId);
        cursor.put(COL_ACTIONS_PAYLOAD, paload);
        cursor.put(COL_ACTIONS_TIME, System.currentTimeMillis());
        cursor.put(COL_ACTIONS_SYNCED, synced);

        Log.i("addOfflineAction",  "tableName: " + tableName + ",actionType: " + actionType + ",localId: " + localId + ",paload: " + paload + ",synced: " + synced);
        return db.insert(TABLE_OFFLINE_ACTIONS, null, cursor);
    }
    public ArrayList<Action> getOfflineActions(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        ArrayList<Action> Actions = new ArrayList<>();

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
        setDataFromCloud(); // it is inefficient takes 3 - 5 sends
    }
    public void setDataFromCloud() {
        SQLiteDatabase db = this.getWritableDatabase();

        Map<String, Object> root = new HashMap<>();
        root.put("action", "get_data");
        root.put("token", token);

        String json = new Gson().toJson(root);

        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json")
        );

        apiService.SendAndGetData(body).enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayList<Transaction> transactions = response.body().transactions;
                    Settings settings = response.body().settings;

                    db.delete(TABLE_TRANSACTION,"1 = 1",null);
                    db.delete(TABLE_SETTINGS,"1 = 1",null);

                    for (Transaction transaction:transactions){
                        UUID uuid = UUID.fromString(transaction.getId());

                        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
                        bb.putLong(uuid.getMostSignificantBits());
                        bb.putLong(uuid.getLeastSignificantBits());

                        insertTransactionLocal(bb.array(),transaction.getUserId(),transaction.getName(),transaction.getTime(),transaction.getAmount(),transaction.getType(),transaction.getNotes(),transaction.getPayment_method(),transaction.getPaidStatus(),transaction.getMarkedStatus());
                    }
                    if (settings != null)
                        insertSettings(settings.getNameV(),settings.getTypeV(),settings.getNotesV(),settings.getTimeV(),settings.getUserId(),settings.getPaymentMethodList(),settings.getTypeList());


                    Log.d("Broadcast", "Sending UPDATE_LIST");
                    Intent intent = new Intent("UPDATE_LIST");
                    intent.setPackage(Contextt.getPackageName());
                    Contextt.sendBroadcast(intent);
                } else {
                    Log.v("apiService onResponse message:", response.message());
                    try {
                        String errorResponse = response.errorBody() != null ? response.errorBody().string() : "No error body available";
                        Log.e("API", "Server returned error: " + errorResponse);
                    } catch (IOException e) {
                        Log.e("API", "Error reading the error body: " + e.getMessage());
                    }
                }
            }
            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
            }
        });
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