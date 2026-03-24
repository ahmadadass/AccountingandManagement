package com.example.accountingandmanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.internal.Objects;

import java.util.ArrayList;

public class AddEditProductActivity extends AppCompatActivity {

    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product);

        Button btn_barcode_reader = findViewById(R.id.btn_barcode_reader);
        Button btn_add_edit_product_back = findViewById(R.id.btn_add_edit_product_back);
        EditText it_product_id = findViewById(R.id.it_product_id);
        EditText it_product_name = findViewById(R.id.it_product_name);
        EditText it_product_type = findViewById(R.id.it_product_type);
        Button btn_save_product = findViewById(R.id.btn_save_product);
        Button btn_save_and_add_more_products = findViewById(R.id.btn_save_and_add_more_products);
        
        DatabaseHelper db = new DatabaseHelper(this);

        getSupportFragmentManager().setFragmentResultListener("scanner_request", this,
                (requestKey, bundle) -> {
                    String scannedBarcode = bundle.getString("barcode_data");

                    it_product_id.setText(scannedBarcode);
                    Toast.makeText(this, "Scanned: " + scannedBarcode, Toast.LENGTH_LONG).show();
                });


        btn_barcode_reader.setOnClickListener( e -> {
            openScanner();
        });

        btn_add_edit_product_back.setOnClickListener( e -> {
            finish();
        });

        btn_save_product.setOnClickListener( e -> {
            String id = String.valueOf(it_product_id.getText());
            String name = String.valueOf(it_product_name.getText());
            String type = String.valueOf(it_product_type.getText());
            if (!id.equals("") && !name.equals("") && !type.equals("")){
                ArrayList<Product> products = db.getAllProdcutsWhereClause(DatabaseHelper.COL_PRODUCTS_ID + " = " + id); // column_name, clause (=, >, <, ...), data
                if (products == null || products.size() == 0 || !products.get(0).getId().equals(id)){
                    db.insertProductData(id,name,type);

                    finish();
                } else {
                    Toast.makeText(this, "this product id is taken", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        btn_save_and_add_more_products.setOnClickListener( e -> {
            String id = String.valueOf(it_product_id.getText());
            String name = String.valueOf(it_product_name.getText());
            String type = String.valueOf(it_product_type.getText());
            if (!id.equals("") && !name.equals("") && !type.equals("") ){
                if (!db.getAllProdcutsWhereClause(DatabaseHelper.COL_PRODUCTS_ID + " = " + id).get(0).getId().equals(id)){
                    db.insertProductData(id,name,type);
                    Toast.makeText(this, "Product inserted", Toast.LENGTH_SHORT).show();

                    it_product_id.setText("");
                    it_product_name.setText("");
                    it_product_type.setText("");
                } else {
                    Toast.makeText(this, "this product id is taken", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void openScanner(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted, show the dialog
            ScannerDialogFragment fragment = new ScannerDialogFragment();
            fragment.show(getSupportFragmentManager(), "scanner");
        } else {
            // Request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 100);
        }
    }
}
