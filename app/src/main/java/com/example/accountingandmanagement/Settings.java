package com.example.accountingandmanagement;

import com.google.gson.Gson;

public class Settings {
    public int id;
    public int user_id;
    public int name_visibility;
    public int type_visibility;
    public int notes_visibility;
    public int time_visibility;

    public Settings(int id, int user_id, boolean name_visibility, boolean type_visibility, boolean notes_visibility, boolean time_visibility){
        this.id = id;
        this.user_id = user_id;
        this.name_visibility = name_visibility ? 1 : 0;
        this.type_visibility = type_visibility ? 1 : 0;
        this.time_visibility = time_visibility ? 1 : 0;
        this.notes_visibility = notes_visibility ? 1 : 0;
    }

    public String print() {
        return new Gson().toJson(this);
    }

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getName_visibility() {
        return name_visibility;
    }

    public int getType_visibility() {
        return type_visibility;
    }

    public int getNotes_visibility() {
        return notes_visibility;
    }

    public int getTime_visibility() {
        return time_visibility;
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
}