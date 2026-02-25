package com.example.accountingandmanagement;

import com.google.gson.Gson;

import java.util.ArrayList;

public class Settings {
    public int id;
    public int user_id;
    public int name_visibility;
    public int type_visibility;
    public int notes_visibility;
    public int time_visibility;
    public String payment_method_list;
    public String type_list;

    public Settings(int id, int user_id, boolean name_visibility, boolean type_visibility, boolean notes_visibility, boolean time_visibility, String payment_method_list, String type_list){
        this.id = id;
        this.user_id = user_id;
        this.name_visibility = name_visibility ? 1 : 0;
        this.type_visibility = type_visibility ? 1 : 0;
        this.time_visibility = time_visibility ? 1 : 0;
        this.notes_visibility = notes_visibility ? 1 : 0;
        this.payment_method_list = payment_method_list;
        this.type_list = type_list;
    }

    public String print() {
        return new Gson().toJson(this);
    }
    public int getId() {
        return id;
    }
    public static Settings convertToSettings(String json) {
        return new Gson().fromJson(json, Settings.class);
    }
    public int getUserId(){
        return this.user_id;
    }
    public boolean getNameV(){
        return this.name_visibility == 1;
    }
    public boolean getTypeV(){
        return this.type_visibility == 1;
    }
    public boolean getTimeV(){
        return this.time_visibility == 1;
    }
    public boolean getNotesV(){
        return this.notes_visibility == 1;
    }
    public String getPaymentMethodList(){
        return this.payment_method_list;
    }
    public String getTypeList(){
        return this.type_list;
    }
    public void setNameV(boolean visibility){
        this.name_visibility = visibility  ? 1 : 0;
    }
    public void setTypeV(boolean visibility){
        this.type_visibility = visibility ? 1 : 0;
    }
    public void setTimeV(boolean visibility){
        this.time_visibility = visibility ? 1 : 0;
    }
    public void setNotesV(boolean visibility){
        this.notes_visibility = visibility ? 1 : 0;
    }
    public void setPaymentMethodList(String payment_method_list){
        this.payment_method_list = payment_method_list;
    }
    public void setTypeList(String type_list){
        this.type_list = type_list;
    }
    public static String arrayToString(ArrayList<String> array) {
        if (array == null || array.size() == 0) return "";

        StringBuilder text = new StringBuilder();

        for (int i = 0; i < array.size(); i++) {
            text.append(array.get(i));
            if (i < array.size() - 1) {
                text.append(","); // separator
            }
        }

        return text.toString();
    }
    public static ArrayList<String> stringToArray(String text) {
        if (text == null || text.isEmpty()) return new ArrayList<>();

        String[] parts = text.split(",");
        ArrayList<String> array = new ArrayList<>();

        for (int i = 0; i < parts.length; i++) {
            array.add(parts[i]);
        }

        return array;
    }
}