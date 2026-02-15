package com.example.accountingandmanagement;

import org.jetbrains.annotations.Nullable;

public class Action {
    int id;
    int local_id;
    int server_id;
    String paload;
    String table_name;
    String action_type;
    long time;
    int synced;
    public Action(int id , int local_id ,@Nullable int server_id, String table_name, String action_type, long time, int synced, String paload){
        this.id = id;
        this.local_id = local_id;
        this.server_id = server_id;
        this.paload = paload;
        this.table_name = table_name;
        this.action_type = action_type;
        this.time = time;
        this.synced = synced;
    }

    public int getId() {
        return id;
    }

    public int getLocal_id() {
        return local_id;
    }

    public int getServer_id() {
        return server_id;
    }

    public String getTable_name() {
        return table_name;
    }

    public String getAction_type() {
        return action_type;
    }

    public long getTime() {
        return time;
    }

    public int getSynced() {
        return synced;
    }
    public String getPaload() {
        return paload;
    }
}
