package com.example.accountingandmanagement;

import static com.example.accountingandmanagement.MainActivity.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class AddEditIncomeExpenseActivity extends AppCompatActivity {

    Transaction transaction = null;
    int position = -1;
    String paymentMethod = "";
    List<String> paymentmethodList = new ArrayList<>();

    MaterialAutoCompleteTextView tv_transaction_date_picker;
    MaterialAutoCompleteTextView tv_transaction_time_picker;
    TextInputLayout ly_transaction_date;
    TextInputLayout ly_transaction_time;
    Button btn_cancel_transaction;
    Button btn_save_transaction;
    Button btn_save_add_transaction;
    Button btn_transaction_bookmark;
    TextView tv_transaction_title;
    TextInputEditText it_transaction_amount;
    TextInputEditText it_transaction_name;
    MaterialAutoCompleteTextView it_transaction_type;
    TextInputEditText it_transaction_notes;
    GridView gv_transaction_payment_method;
    View ly_paid_status_add_edit;
    TextView tv_paid_status_add_edit;
    boolean Paid = true;
    boolean Marked = false;
    String transaction_title;
    String selected_transaction_id;
    long unixTime;

    int Year;
    int Month;
    int DayOfMonth;
    int Hours;
    int Minutes;
    int Seconds;
    DatabaseHelper db;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income_expense);

        db = new DatabaseHelper(this);

        btn_cancel_transaction = findViewById(R.id.btn_cancel_transaction);
        btn_save_transaction = findViewById(R.id.btn_save_transaction);
        btn_save_add_transaction = findViewById(R.id.btn_save_add_transaction);
        btn_transaction_bookmark = findViewById(R.id.btn_transaction_bookmark);
        tv_transaction_title = findViewById(R.id.tv_transaction_title);
        tv_transaction_date_picker = findViewById(R.id.tv_transaction_date_picker);
        tv_transaction_time_picker = findViewById(R.id.tv_transaction_time_picker);
        ly_transaction_date = findViewById(R.id.ly_transaction_date);
        ly_transaction_time = findViewById(R.id.ly_transaction_time);
        it_transaction_amount = findViewById(R.id.it_transaction_amount);
        it_transaction_name = findViewById(R.id.it_transaction_name);
        it_transaction_type = findViewById(R.id.it_transaction_type);
        it_transaction_notes = findViewById(R.id.it_transaction_notes);
        gv_transaction_payment_method = findViewById(R.id.gv_transaction_payment_method);
        ly_paid_status_add_edit = findViewById(R.id.ly_paid_status_add_edit);
        tv_paid_status_add_edit = findViewById(R.id.tv_paid_status_add_edit);

        unixTime = System.currentTimeMillis() / 1000;

        btn_cancel_transaction.setOnClickListener( e -> {
            Intent intent = new Intent(AddEditIncomeExpenseActivity.this, statisticsActivity.class);
            startActivity(intent);
        });

        btn_transaction_bookmark.setOnClickListener( e-> {
            if (Marked){
                Marked = false;
                btn_transaction_bookmark.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_action_bookmark_not_added));
            } else {
                Marked = true;
                btn_transaction_bookmark.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_action_bookmark));
            }
        });

        Intent intent = getIntent();
        transaction_title = intent.getStringExtra("Transaction_Title");
        selected_transaction_id = intent.getStringExtra("Selected_Transaction_Id");

        paymentmethodList.clear();

        paymentmethodList.add("Bank");
        paymentmethodList.add("Cash");
        paymentmethodList.add("Other");
        paymentmethodList.add("Show More"); // this is to add more payment method's

        if (transaction_title.equals("Expense")){
            it_transaction_amount.setText("-");
        }

        String Mode;
        if (selected_transaction_id.equals("")){

            Mode = "Add";
            tv_transaction_date_picker.setText(Transaction.printDate(unixTime));
            tv_transaction_time_picker.setText(Transaction.printTime(unixTime));
            setTimeValues(unixTime);
        } else {
            // if Edit
            Mode = "Edit";
            transaction = db.getTransactionById(selected_transaction_id);

            unixTime = transaction.getTime();
            tv_transaction_date_picker.setText(Transaction.printDate(unixTime));
            tv_transaction_time_picker.setText(Transaction.printTime(unixTime));
            it_transaction_name.setText(transaction.getName());
            it_transaction_amount.setText(transaction.getAmount().toString());
            it_transaction_type.setText(transaction.getType());
            it_transaction_notes.setText(transaction.getNotes());
            Paid = transaction.getPaidStatus();
            if (Paid){
                ly_paid_status_add_edit.setBackgroundResource(R.drawable.paid_back);
                tv_paid_status_add_edit.setText("Paid");
            } else {
                ly_paid_status_add_edit.setBackgroundResource(R.drawable.not_paid_back);
                tv_paid_status_add_edit.setText("Unpaid");
            }
            if (transaction.getMarkedStatus()){
                Marked = false;
                btn_transaction_bookmark.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_action_bookmark_not_added));
            } else {
                Marked = true;
                btn_transaction_bookmark.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_action_bookmark));
            }


            Log.i("AEIEA","paymentmethodList.size: " + paymentmethodList.size());
            Log.i("AEIEA","transaction: " + transaction.print());
            for (int i = 0; i < paymentmethodList.size(); i++){
                Log.i("for_AEIEA","for loop pos: " + i);
                if (transaction.getPayment_method().equals(paymentmethodList.get(i))){
                    position = i;
                }
            }

            setTimeValues(unixTime);
            //et_transaction_pa.setText(transaction.getPaymentMethod());
        }

        tv_transaction_title.setText(Mode + " " + transaction_title);

        ArrayList<String> array = new ArrayList<>();

        array.add("allowance");
        array.add("salary");
        array.add("mortgage");
        array.add("transport");
        array.add("shopping");
        array.add("subscription");

        ArrayAdapter<String> adapterType = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, array);

        it_transaction_type.setAdapter(adapterType);

        PamentMethodAdapter adapter = new PamentMethodAdapter(this,paymentmethodList);
        adapter.setSelectedPosition(position);
        gv_transaction_payment_method.setAdapter(adapter);

        gv_transaction_payment_method.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            View previousSelectedView = null;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Reset old selected item color
                if (previousSelectedView != null) {
                    previousSelectedView.setBackgroundColor(Color.TRANSPARENT);
                }

                adapter.setSelectedPosition(position);

                previousSelectedView = view;

                paymentMethod = paymentmethodList.get(position);



            }
        });

        //ly_transaction_date.setOnClickListener( e->{openDateDialog();});
        tv_transaction_date_picker.setOnClickListener( e-> {
            openDateDialog();
        });

        //ly_transaction_time.setOnClickListener( e->{openTimeDialog();});
        tv_transaction_time_picker.setOnClickListener( e-> {
            openTimeDialog();
        });

        btn_save_transaction.setOnClickListener( e-> {
            try {
                if (saveTransaction()){
                    Intent intent3 = new Intent(AddEditIncomeExpenseActivity.this, statisticsActivity.class);
                    startActivity(intent3);
                }
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });

        btn_save_add_transaction.setOnClickListener( e-> {
            try {
                if (saveTransaction()){
                    //restarts the activity
                    Intent intent2 = new Intent(AddEditIncomeExpenseActivity.this, AddEditIncomeExpenseActivity.class);
                    intent2.putExtra("Transaction_Title",tv_transaction_title.getText());
                    intent2.putExtra("Selected_Transaction_Id",0);
                    startActivity(intent2);
                }
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });

        ly_paid_status_add_edit.setOnClickListener( e ->{
            if (!Paid){
                Paid = true;
                ly_paid_status_add_edit.setBackgroundResource(R.drawable.paid_back);
                tv_paid_status_add_edit.setText("Paid");
            } else {
                Paid = false;
                ly_paid_status_add_edit.setBackgroundResource(R.drawable.not_paid_back);
                tv_paid_status_add_edit.setText("Unpaid");
            }
        });
    }

    private void setTimeValues(Long unixTime){
        Date date = new Date(unixTime * 1000); // convert to milliseconds
        SimpleDateFormat year = new SimpleDateFormat("yyyy", Locale.getDefault());
        SimpleDateFormat month = new SimpleDateFormat("MM", Locale.getDefault());
        SimpleDateFormat day = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat hours = new SimpleDateFormat("HH", Locale.getDefault());
        SimpleDateFormat minutes = new SimpleDateFormat("mm", Locale.getDefault());

        Year = Integer.parseInt(year.format(date));
        Month = Integer.parseInt(month.format(date));
        DayOfMonth = Integer.parseInt(day.format(date));
        Hours = Integer.parseInt(hours.format(date));
        Minutes = Integer.parseInt(minutes.format(date));
    }
    private boolean saveTransaction() throws InterruptedException {
        if (paymentMethod.isEmpty() && Objects.requireNonNull(it_transaction_name.getText()).toString().isEmpty()){
            Toast.makeText(this, "please select a paymentMethod or type", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (transaction == null){
                String name = it_transaction_name.getText().toString();
                Double amount = Double.valueOf(it_transaction_amount.getText().toString());
                String type = it_transaction_type.getText().toString();
                String notes = it_transaction_notes.getText().toString();

                Intent intent1 = new Intent(this, statisticsActivity.class);
                startActivity(intent1);

                db.insertTransaction(name,unixTime,amount,type,notes,paymentMethod,Paid, Marked);

                //db.insertTransactionLocal(99,user.getId(),name,unixTime,amount,type,notes,paymentMethod,Paid, Marked);

                //Intent intent1 = new Intent(AddEditIncomeExpenseActivity.this, statisticsActivity.class);
                //startActivity(intent1);
            } else {
                String name = it_transaction_name.getText().toString();
                Double amount = Double.valueOf(it_transaction_amount.getText().toString());
                String type = it_transaction_type.getText().toString();
                String notes = it_transaction_notes.getText().toString();

                Intent intent1 = new Intent(this, statisticsActivity.class);
                startActivity(intent1);

                db.editTransaction(selected_transaction_id,name,unixTime,amount,type,notes,paymentMethod,Paid,Marked);
            }
            return true;
        }
    }
    private void openDateDialog(){
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                Year = year;
                Month = month + 1;
                DayOfMonth = dayOfMonth;

                long unix_hours = (Hours) * (60 * 60);
                long unix_minutes = (Minutes) * (60);


                long unix_year_without_leap = (Year - 1970) * (60 * 60 * 24 * 365);
                int unix_leap_years = (int)Math.round((Year - 1970) / 4d);
                int days = DayOfMonth + unix_leap_years - 1;
                switch(Month - 1) {
                    case 11:
                        days += 30;
                    case 10:
                        days += 31;
                    case 9:
                        days += 30;
                    case 8:
                        days += 31;
                    case 7:
                        days += 31;
                    case 6:
                        days += 30;
                    case 5:
                        days += 31;
                    case 4:
                        days += 30;
                    case 3:
                        days += 31;
                    case 2:
                        days += 28;
                    case 1:
                        days += 31;
                }

                long unix_days = days * (60 * 60 * 24);

                unixTime = unix_year_without_leap + unix_days + unix_hours + unix_minutes + Seconds - (TimeZone.getDefault().getOffset(unixTime) / 1000);

                Log.i("unixTimeC",Long.toString(unixTime)  + " Default Time Zone " + TimeZone.getDefault().getOffset(unixTime) / 1000);

                tv_transaction_date_picker.setText(Transaction.printDate(unixTime));
                tv_transaction_time_picker.setText(Transaction.printTime(unixTime));

            }
        },
                Year,
                Month-1, // NOTE: from 0 - 11 (he applied Month mod 12)
                DayOfMonth);

        //Log.i("AEIEA","Year: " + Year + " Month: " + Month);

        dialog.show();
    }
    private void openTimeDialog(){
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                Hours = hourOfDay;
                Minutes = minute;
                Seconds = 0;

                Log.i("AEIEA","Hours: " + Hours + " Minutes: " + Minutes);

                long unix_hours = (Hours) * (60 * 60);
                long unix_minutes = (Minutes) * (60);


                long unix_year_without_leap = (Year - 1970) * (60 * 60 * 24 * 365);
                int unix_leap_years = (int)Math.round((Year - 1970) / 4d);
                int days = DayOfMonth + unix_leap_years - 1;
                switch(Month - 1) {
                    case 11:
                        days += 30;
                    case 10:
                        days += 31;
                    case 9:
                        days += 30;
                    case 8:
                        days += 31;
                    case 7:
                        days += 31;
                    case 6:
                        days += 30;
                    case 5:
                        days += 31;
                    case 4:
                        days += 30;
                    case 3:
                        days += 31;
                    case 2:
                        days += 28;
                    case 1:
                        days += 31;
                }

                long unix_days = days * (60 * 60 * 24);

                unixTime = unix_year_without_leap + unix_days + unix_hours + unix_minutes + Seconds - (TimeZone.getDefault().getOffset(unixTime) / 1000);


                Log.i("unixTimeC",Long.toString(unixTime)  + " Default Time Zone " + TimeZone.getDefault().getOffset(unixTime) / 1000);

                tv_transaction_date_picker.setText(Transaction.printDate(unixTime));
                tv_transaction_time_picker.setText(Transaction.printTime(unixTime));

            }
        },Hours,Minutes,false);

        //Log.i("AEIEA","Hours: " + Hours + " Minutes: " + Minutes);

        timePickerDialog.show();
    }
}