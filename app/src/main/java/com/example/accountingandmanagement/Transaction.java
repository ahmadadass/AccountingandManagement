package com.example.accountingandmanagement;

import com.google.gson.Gson;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Transaction {
    String id;
    int user_id;
    String name;
    Long time;
    Double amount;
    String type;
    String notes;
    String payment_method;
    int paid;
    int bookmark;

    public Transaction(String Id , int userId, String Name, Long Time, Double Amount, String Type, String Notes, String PaymentMethod, int Paid, int Marked){
        this.id = Id;
        this.user_id = userId;
        this.name = Name;
        this.time = Time;
        this.type = Type;
        this.amount = Amount;
        this.notes = Notes;
        this.payment_method = PaymentMethod;
        this.paid = Paid;
        this.bookmark = Marked;
    }
    public String getId(){
        UUID uuid = UUID.fromString(this.id);

        ByteBuffer bytes = ByteBuffer.wrap(new byte[16]);
        bytes.putLong(uuid.getMostSignificantBits());
        bytes.putLong(uuid.getLeastSignificantBits());

        ByteBuffer bb = ByteBuffer.wrap(bytes.array());
        long high = bb.getLong();
        long low = bb.getLong();
        return new UUID(high, low).toString();
    }
    public int getUserId(){
        return this.user_id;
    }
    public String getName(){
        return this.name;
    }
    public Long getTime(){
        return this.time;
    }
    public Double getAmount(){
        return this.amount;
    }
    public String getPayment_method(){
        return this.payment_method;
    }
    public String getType(){
        return this.type;
    }
    public String getNotes(){
        return this.notes;
    }
    public boolean getPaidStatus(){
        return this.paid == 1;
    }
    public void setPaidStatus(int Paid){
        this.paid = Paid;
    }
    public boolean getMarkedStatus(){
        return this.bookmark == 1;
    }

    public String print() {
        return new Gson().toJson(this);
    }
    public static Transaction convertToTransaction(String json) {
        return new Gson().fromJson(json, Transaction.class);
    }
    public static String unixToString(long unixSeconds) {
        Date date = new Date(unixSeconds * 1000); // convert to milliseconds
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()); //MMM dd, yyyy – hh:mm a
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());
        return sdf.format(date);
    }
    public static String printDate(Long time){
        Date date = new Date(time * 1000); // convert to milliseconds
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()); //MMM dd, yyyy – hh:mm a
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());
        return sdf.format(date);
    }
    public static String printTime(Long time){
        Date date = new Date(time * 1000); // convert to milliseconds
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()); //MMM dd, yyyy – hh:mm a
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        //sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    public static UUID bytesToUUID(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long mostSigBits = bb.getLong();
        long leastSigBits = bb.getLong();
        return new UUID(mostSigBits, leastSigBits);
    }

    public static byte[] uuidToBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.allocate(16);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    public static String arrayProductToString(ArrayList<Product> array) {
        if (array == null || array.size() == 0) return "";

        StringBuilder text = new StringBuilder();

        for (int i = 0; i < array.size(); i++) {
            text.append(array.get(i).print());
            if (i < array.size() - 1) {
                text.append(","); // separator
            }
        }

        return text.toString();
    }
    public static ArrayList<Product> stringToProductArray(String text) {
        if (text == null || text.isEmpty()) return new ArrayList<>();

        String[] parts = text.split(",");
        ArrayList<Product> array = new ArrayList<>();

        for (int i = 0; i < parts.length; i++) {
            array.add(Product.jsonToProduct(parts[i]));
        }

        return array;
    }
}
