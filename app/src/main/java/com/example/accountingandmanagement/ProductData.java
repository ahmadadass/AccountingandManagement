package com.example.accountingandmanagement;

import com.google.gson.Gson;

public class ProductData {
    String id;
    String name;
    String type;
    public ProductData(String id, String name, String type){
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public String print() {
        return new Gson().toJson(this);
    }
    public static ProductData jsonToProductData(String json) {
        return new Gson().fromJson(json, ProductData.class);
    }
    /*public static Product jsonToProduct(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);

        Product product = new Product(jsonObject.getString("id"),jsonObject.getString("name"),jsonObject.getLong("time"),jsonObject.getString("type"));

        return product;
    }*/
}
