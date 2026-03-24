package com.example.accountingandmanagement;

import static com.example.accountingandmanagement.MainActivity.user;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class EditFieldsActivity extends AppCompatActivity {

    @Override
    @SuppressLint({"MissingInflatedId", "LocalSuppress", "UseSwitchCompatOrMaterialCode"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_fields);

        Switch s_name = findViewById(R.id.s_name);
        Switch s_type = findViewById(R.id.s_type);
        Switch s_notes = findViewById(R.id.s_notes);
        Switch s_time = findViewById(R.id.s_time);
        ListView lv_item_preview = findViewById(R.id.lv_item_preview);
        Button btn_save_fields = findViewById(R.id.btn_save_fields);
        Button btn_back_edit_fields = findViewById(R.id.btn_back_edit_fields);

        DatabaseHelper db = new DatabaseHelper(this);

        Settings settings = db.getSettings();

        s_name.setChecked(settings.getNameV());
        s_type.setChecked(settings.getTypeV());
        s_notes.setChecked(settings.getNotesV());
        s_time.setChecked(settings.getTimeV());

        ArrayList<Transaction> transactions = new ArrayList<>();

        UUID id = UUID.randomUUID();

        transactions.add(new Transaction(id.toString(),0,"Name",1767200811L,15.9,"Type","Notes","PaymentMethod",1,0));

        db.setSettings();
        TransactionAdapter adapter = new TransactionAdapter(EditFieldsActivity.this, transactions);
        lv_item_preview.setAdapter(adapter);

        s_name.setOnClickListener( e-> {
            settings.setNameV(s_name.isChecked());
            adapter.notifyDataSetChanged();
        });
        s_type.setOnClickListener( e-> {
            settings.setTypeV(s_type.isChecked());
            adapter.notifyDataSetChanged();
        });
        s_notes.setOnClickListener( e-> {
            settings.setNotesV(s_notes.isChecked());
            adapter.notifyDataSetChanged();
        });
        s_time.setOnClickListener( e-> {
            settings.setTimeV(s_time.isChecked());
            adapter.notifyDataSetChanged();
        });

        btn_save_fields.setOnClickListener( e-> {
            //db.insertSettings(s_name.isChecked(),s_type.isChecked(),s_notes.isChecked(),s_time.isChecked(),user.getId());

            finish();

            db.updateSettings(s_name.isChecked(),s_type.isChecked(),s_notes.isChecked(),s_time.isChecked(),user.getId(),settings.getPaymentMethodList(),settings.getTypeList());
        });

        btn_back_edit_fields.setOnClickListener( e-> {
            finish();
        });
    }
}