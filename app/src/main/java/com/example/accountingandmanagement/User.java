package com.example.accountingandmanagement;

import com.google.gson.Gson;

public class User {
    int id;
    String username;
    String password;
    String subscription;
    public User(int id, String name, String password, String subscription){
        this.id = id;
        this.username = name;
        this.password = password;
        this.subscription = subscription;
    }
    public String print() {
        return new Gson().toJson(this);
    }
    public static User convertToUser(String json) {
        return new Gson().fromJson(json, User.class);
    }

    public int getId(){
        return this.id;
    }
    public String getName(){
        return this.username;
    }
    public String getPassword(){
        return this.password;
    }
    public String getSubscription(){
        return this.subscription;
    }
}
