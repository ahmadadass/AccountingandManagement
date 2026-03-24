package com.example.accountingandmanagement;

import com.google.gson.Gson;

public class Product {
    String id;
    byte[] transactionId;
    int cunt;
    Long expiryDate;
    Double Price;

    public Product(String id, byte[] transactionId, int cunt, Long expiryDate, Double Price) {
        this.id = id;
        this.transactionId = transactionId;
        this.cunt = cunt;
        this.expiryDate = expiryDate;
        this.Price = Price;
    }

    public String getId() {
        return id;
    }
    public byte[] getTransactionId() {
        return transactionId;
    }
    public String getStringTransactionId() {
        return Transaction.bytesToUUID(this.transactionId).toString();
    }
    public int getCunt() {
        return cunt;
    }
    public Long getExpiryDate() {
        return expiryDate;
    }
    public Double getPrice() {
        return Price;
    }
    public String print() {
        return new Gson().toJson(this);
    }
    public static Product jsonToProduct(String json) {
        return new Gson().fromJson(json, Product.class);
    }
}
