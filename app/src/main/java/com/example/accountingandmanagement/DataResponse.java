package com.example.accountingandmanagement;

import java.util.ArrayList;

public class DataResponse {

    public String token;
    public User user;
    public ArrayList<Transaction> transactions;
    public Settings settings;
    public String error; // To catch login errors
    private ResultSetHeader ResultSetHeader;

    public ResultSetHeader getResultSetHeader() {
        return ResultSetHeader;
    }

    public static class ResultSetHeader {
        private String insertId;

        public String getInsertId() {
            return insertId;
        }
    }

    private Body body;

    public Body getBody() {
        return body;
    }

    public static class Body {
        private String key;

        public String getKey() {
            return key;
        }
    }

    public String print(){
        String text = "";
        if (this.transactions != null){
            for (Transaction transaction:transactions)
                text += "Transaction:" + transaction.print() + "\n";
        }
        if (this.user != null){
            text += "User" + user.print() + "\n";
        }
        if (this.settings != null){
            text += "Settings:" + settings.print() + "\n";
        }
        if (!this.error.isEmpty()){
            text += "Error:" + error + "\n";
        }
        return text;
    }

}

class insertTransactionQ{
    public int insertId;
    public insertTransactionQ(int insertId){
        this.insertId = insertId;
    }
    //public int getInsertId(){return this.insertId;}

}