package com.example.accountingandmanagement;

import com.google.gson.Gson;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
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

    public Transaction(){

    }

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

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public void setPaid(int paid) {
        this.paid = paid;
    }

    public void setMarkedStatus(int bookmark) {
        this.bookmark = bookmark;
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
}
