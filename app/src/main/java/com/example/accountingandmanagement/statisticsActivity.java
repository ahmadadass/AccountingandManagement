package com.example.accountingandmanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class statisticsActivity extends AppCompatActivity {

    public static statisticsActivity instance;
    Double IncomeSum = 0.0;
    Double ExpenseSum = 0.0;
    Double TotalSum = 0.0;
    public TextView tv_filter;
    public Button btn_filter_left;
    public Button btn_filter_right;
    public static String filterType;
    ListView lv_transactions;
    TextView tv_income_amount;
    TextView tv_expense_amount;
    TextView tv_sum_amount;
    public TransactionAdapter adapter;
    public ArrayList<Transaction> transactions = new ArrayList<>();
    private NetworkListener networkListener;
    public DatabaseHelper db;

    @Override
    @SuppressLint({"MissingInflatedId", "LocalSuppress", "RestrictedApi"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        lv_transactions = findViewById(R.id.lv_transactions);
        RecyclerView rv_filter = findViewById(R.id.rv_filter);
        Button btn_add_income = findViewById(R.id.btn_add_income);
        Button btn_add_expense = findViewById(R.id.btn_add_expense);
        Button btn_search = findViewById(R.id.btn_search);
        Button btn_menu = findViewById(R.id.btn_menu);
        tv_income_amount = findViewById(R.id.tv_income_amount);
        tv_expense_amount = findViewById(R.id.tv_expense_amount);
        tv_sum_amount = findViewById(R.id.tv_sum_amount);
        tv_filter = findViewById(R.id.tv_filter);
        btn_filter_left = findViewById(R.id.btn_filter_left);
        btn_filter_right = findViewById(R.id.btn_filter_right);
        NavigationView nv_drawer_menu = findViewById(R.id.nv_drawer_menu);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ImageView im_account = findViewById(R.id.im_account);
        MaterialAutoCompleteTextView it_account_name = findViewById(R.id.it_account_name);
        LinearLayout ly_inventory = findViewById(R.id.ly_inventory);
        LinearLayout ly_edit_fields = findViewById(R.id.ly_edit_fields);
        LinearLayout ly_book_marks = findViewById(R.id.ly_book_marks);
        LinearLayout ly_backup = findViewById(R.id.ly_backup);
        LinearLayout ly_settings = findViewById(R.id.ly_settings);
        LinearLayout ly_customer_support = findViewById(R.id.ly_customer_support);
        LinearLayout ly_logout = findViewById(R.id.ly_logout);

        db = new DatabaseHelper(this);

        networkListener = new NetworkListener();
        networkListener.start(this);

        btn_add_income.setOnClickListener( e -> {

            PopupMenu menu = new PopupMenu(this, e);
            menu.inflate(R.menu.menu_plus);

            menu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.income) {
                    Intent intent = new Intent(statisticsActivity.this, AddEditIncomeExpenseActivity.class);
                    intent.putExtra("Transaction_Title","Income");
                    intent.putExtra("Selected_Transaction_Id","");
                    startActivity(intent);
                } else if (item.getItemId() == R.id.sell) {
                    //openSell();
                }
                return true;
            });

            try {
                Field[] fields = menu.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if ("mPopup".equals(field.getName())) {
                        field.setAccessible(true);
                        Object menuPopupHelper = field.get(menu);
                        Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                        Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                        setForceIcons.invoke(menuPopupHelper, true);
                        break;
                    }
                }
            } catch (Exception x) {
                x.printStackTrace();
            }

            menu.show();
            /*
            Intent intent = new Intent(statisticsActivity.this, AddEditIncomeExpenseActivity.class);
            intent.putExtra("Transaction_Title","Income");
            intent.putExtra("Selected_Transaction_Id","");
            startActivity(intent);*/
        });

        btn_add_expense.setOnClickListener( e -> {
            PopupMenu menu = new PopupMenu(this, e);
            menu.inflate(R.menu.menu_minus);

            menu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.expense) {
                    Intent intent = new Intent(statisticsActivity.this, AddEditIncomeExpenseActivity.class);
                    intent.putExtra("Transaction_Title","Expense");
                    intent.putExtra("Selected_Transaction_Id","");
                    startActivity(intent);
                } else if (item.getItemId() == R.id.buy) {
                    //openBuy();
                }
                return true;
            });

            try {
                Field[] fields = menu.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if ("mPopup".equals(field.getName())) {
                        field.setAccessible(true);
                        Object menuPopupHelper = field.get(menu);
                        Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                        Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                        setForceIcons.invoke(menuPopupHelper, true);
                        break;
                    }
                }
            } catch (Exception x) {
                x.printStackTrace();
            }

            menu.show();
            /*
            Intent intent = new Intent(statisticsActivity.this, AddEditIncomeExpenseActivity.class);
            intent.putExtra("Transaction_Title","Expense");
            intent.putExtra("Selected_Transaction_Id","");
            startActivity(intent);*/
        });

        btn_search.setOnClickListener( e -> {
            Intent intent = new Intent(statisticsActivity.this, SearchActivity.class);
            intent.putExtra("SEARCH_SETTINGS",1);
            startActivity(intent);
        });

        btn_menu.setOnClickListener( e-> {
            drawerLayout.openDrawer(findViewById(R.id.nv_drawer_menu));
        });

        ly_inventory.setOnClickListener( e-> {
            Intent intent = new Intent(statisticsActivity.this, InventoryActivity.class);
            startActivity(intent);
        });
        ly_edit_fields.setOnClickListener( e->{
            Intent intent = new Intent(statisticsActivity.this, EditFieldsActivity.class);
            startActivity(intent);
        });
        ly_book_marks.setOnClickListener( e-> {
            Intent intent = new Intent(statisticsActivity.this, SearchActivity.class);
            intent.putExtra("SEARCH_SETTINGS",11);
            startActivity(intent);
        });
        ly_backup.setOnClickListener( e-> {
            Toast.makeText(this, "ly_backup", Toast.LENGTH_SHORT).show();
        });
        ly_settings.setOnClickListener( e-> {
            Toast.makeText(this, "ly_settings", Toast.LENGTH_SHORT).show();
        });
        ly_customer_support.setOnClickListener( e-> {
            String url = "https://wa.me/qr/SSESYISDMTJBH1";

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
            //Toast.makeText(this, "ly_customer_support", Toast.LENGTH_SHORT).show();
        });
        ly_logout.setOnClickListener( e-> {
            //db.deleteUserPassword(user.getId());
            Intent intent = new Intent(statisticsActivity.this, MainActivity.class);
            startActivity(intent);
        });
        /*

        ArrayList<Transaction> transactions = db.getAllTransactions();

        for (Transaction transaction : transactions) {
            Log.i("SA",transaction.print());
            if (transaction.getPaidStatus()) {
                TotalSum += transaction.getAmount();
                if (transaction.getAmount() > 0) {
                    IncomeSum += transaction.getAmount();
                } else {
                    ExpenseSum += transaction.getAmount();
                }
            }
        }

        long unixTime = System.currentTimeMillis() / 1000;

        Log.i("sA","time:" + unixTime);
        //Toast.makeText(this, "Time: "+Transaction.unixToString(unixTime), Toast.LENGTH_SHORT).show();

        tv_income_amount.setText("₪ " + IncomeSum.toString());
        tv_expense_amount.setText("₪ " + ExpenseSum.toString());
        tv_sum_amount.setText("₪ " + TotalSum.toString());

        db.setSettings();

        TransactionAdapter adapter = new TransactionAdapter(statisticsActivity.this, transactions);
        lv_transactions.setAdapter(adapter);*/

        transactions.clear();
        transactions.addAll(db.getAllTransactions());

        TotalSum = 0.0;
        IncomeSum = 0.0;
        ExpenseSum = 0.0;
        for (Transaction transaction : transactions) {
            Log.i("SA",transaction.print());
            if (transaction.getPaidStatus()) {
                TotalSum += transaction.getAmount();
                if (transaction.getAmount() > 0) {
                    IncomeSum += transaction.getAmount();
                } else {
                    ExpenseSum += transaction.getAmount();
                }
            }
        }

        long unixTime = System.currentTimeMillis() / 1000;

        Log.i("sA","time:" + unixTime);
        //Toast.makeText(this, "Time: "+Transaction.unixToString(unixTime), Toast.LENGTH_SHORT).show();

        tv_income_amount.setText("₪ " + IncomeSum.toString());
        tv_expense_amount.setText("₪ " + ExpenseSum.toString());
        tv_sum_amount.setText("₪ " + TotalSum.toString());

        db.setSettings();

        adapter = new TransactionAdapter(statisticsActivity.this, transactions);
        lv_transactions.setAdapter(adapter);

        //setMainListView();
        lv_transactions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<Transaction>Transactions = db.getAllTransactions();
                Intent intent = new Intent(statisticsActivity.this, TransactionActivity.class);
                intent.putExtra("Selected_Transaction_Id",Transactions.get(position).getId());
                startActivity(intent);
            }
        });




/*      TODO do wen ready :) don't force your self
        filterType = "time";
        FilterAdapter filterAdapter = new FilterAdapter(this,setFilterData(filterType),tv_filter,btn_filter_left,btn_filter_right);
        filterAdapter.setSelectedPosition(1);
        rv_filter.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        rv_filter.setItemAnimator(new DefaultItemAnimator());
        rv_filter.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.HORIZONTAL));
        rv_filter.setAdapter(filterAdapter);*/

    }
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Broadcast", "UPDATE_LIST received");
            runOnUiThread(() -> setMainListView());
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

    public void setMainListView(){
        transactions.clear();
        transactions.addAll(db.getAllTransactions());

        TotalSum = 0.0;
        IncomeSum = 0.0;
        ExpenseSum = 0.0;
        for (Transaction transaction : transactions) {
            Log.i("SA",transaction.print());
            if (transaction.getPaidStatus()) {
                TotalSum += transaction.getAmount();
                if (transaction.getAmount() > 0) {
                    IncomeSum += transaction.getAmount();
                } else {
                    ExpenseSum += transaction.getAmount();
                }
            }
        }

        long unixTime = System.currentTimeMillis() / 1000;

        Log.i("sA","time:" + unixTime);
        //Toast.makeText(this, "Time: "+Transaction.unixToString(unixTime), Toast.LENGTH_SHORT).show();

        tv_income_amount.setText("₪ " + IncomeSum.toString());
        tv_expense_amount.setText("₪ " + ExpenseSum.toString());
        tv_sum_amount.setText("₪ " + TotalSum.toString());

        db.setSettings();

        adapter.notifyDataSetChanged();

    }
    public static ArrayList<String> setFilterData(String type){
        ArrayList<String> filterData = new ArrayList<>();

        if (type.equals("time")){
            filterData.add("All");
            filterData.add("Daily");
            filterData.add("Monthly");
            filterData.add("Weekly");
            filterData.add("Yearly");
        }


        return filterData;
    }

    public static void setFilterText(int position){
        if(filterType.equals("time")){
            switch (position){
                case 1:{

                }
            }
        }
    }

/*
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sort_by_item) {
            Toast.makeText(this, "You have clicked on sort_by_item", Toast.LENGTH_SHORT).show();
            return true;

        } else if (id == R.id.transaction_values_item) {
            Toast.makeText(this, "You have clicked on transaction_values_item", Toast.LENGTH_SHORT).show();
            return true;

        } else if (id == R.id.save_reports_item) {
            Toast.makeText(this, "You have clicked on save_reports_item", Toast.LENGTH_SHORT).show();
            return true;

        } else if (id == R.id.backup_and_restore_item) {
            Toast.makeText(this, "You have clicked on backup_and_restore_item", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}

/*

lv_users.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isAvalable[0]) {
                    ArrayList<User> users = dbHelper.getAllUsersList();

                    users.get(position).newMessageNumber = 0;

                    currentUser = users.get(position);
                    dbHelper.updateMessageCountById(currentUser.getId(), 0);
                    Toast.makeText(getApplicationContext(), "Clicked: " + currentUser.getName() + " IP: " + currentUser.getIp(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                    intent.putExtra("user_id", currentUser.getId());
                    startActivity(intent);
                }
            }
        });
 */