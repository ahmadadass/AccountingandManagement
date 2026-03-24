package com.example.accountingandmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.UUID;

public class InventoryActivity extends AppCompatActivity {
    Button btn_inventory_back;
    Button btn_add_product;
    TextView tv_inventory_value;
    ListView lv_inventory;
    DatabaseHelper db;
    ArrayList<ProductData> productsData;
    ProductAdapter productAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        btn_inventory_back = findViewById(R.id.btn_inventory_back);
        btn_add_product = findViewById(R.id.btn_add_product);
        tv_inventory_value = findViewById(R.id.tv_inventory_value);
        lv_inventory = findViewById(R.id.lv_inventory);
        db = new DatabaseHelper(this);

        btn_add_product.setOnClickListener( e -> {
            Intent intent = new Intent(InventoryActivity.this, AddEditProductActivity.class);
            startActivity(intent);
        });

        //db.insertProductData("6971023771120","tow way pin","school supplies");
        //db.insertProduct("6971023771120",Transaction.uuidToBytes(UUID.fromString("1f2d1138-c8f3-417a-99fd-b2c298970e63")),5,1835944850,25.0);
        productsData = db.getAllProdcutsData();
        productAdapter = new ProductAdapter(this,productsData);

        lv_inventory.setAdapter(productAdapter);

        btn_inventory_back.setOnClickListener( e -> {
            finish();
        });

    }
    public void setMainView(){
        productsData = db.getAllProdcutsData();

        double sum = 0;
        for (ProductData productData:productsData) {
            ArrayList<Product> productArrayList = db.getAllProdcutsWhereClause(DatabaseHelper.COL_PRODUCTS_ID + " = " + productData.getId());
            for (Product product : productArrayList) {
                Transaction transaction = db.getTransactionById(product.getStringTransactionId());
                if (transaction.getAmount() > 0) {
                    sum += product.getPrice();
                } else {
                    sum -= product.getPrice();
                }
            }
        }

        tv_inventory_value.setText("₪ " + sum);

        productAdapter.notifyDataSetChanged();
    }
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Broadcast", "UPDATE_LIST received");
            runOnUiThread(() -> setMainView());
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter("UPDATE_LIST"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
}