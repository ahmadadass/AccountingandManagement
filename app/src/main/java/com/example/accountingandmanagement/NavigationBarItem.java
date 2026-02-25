package com.example.accountingandmanagement;

import java.util.ArrayList;

public class NavigationBarItem {
    public String titel;
    public String desc;
    public int icon;
    public boolean icon_fun;
    public ArrayList<String> arrayMoreActions;
    public boolean selcted;

    public NavigationBarItem(String titel, String desc, int icon, boolean icon_fun, ArrayList<String> arrayMoreActions, boolean selcted) {
        this.titel = titel;
        this.desc = desc;
        this.icon = icon;
        this.icon_fun = icon_fun;
        this.arrayMoreActions = arrayMoreActions;
        this.selcted = selcted;
    }
    public String getTitel() {
        return titel;
    }
    public void setTitel(String titel) {
        this.titel = titel;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public int getIcon() {
        return icon;
    }
    public void setIcon(int icon) {
        this.icon = icon;
    }
    public boolean isIcon_fun() {
        return icon_fun;
    }
    public void setIcon_fun(boolean icon_fun) {
        this.icon_fun = icon_fun;
    }
    public ArrayList<String> getArrayMoreActions() {
        return arrayMoreActions;
    }
    public void setArrayMoreActions(ArrayList<String> arrayMoreActions) {
        this.arrayMoreActions = arrayMoreActions;
    }
    public boolean isSelcted() {
        return selcted;
    }
    public void setSelcted(boolean selcted) {
        this.selcted = selcted;
    }
}
