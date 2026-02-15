package com.example.accountingandmanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.MissingResourceException;

public class SearchActivity extends AppCompatActivity {
    Double IncomeSum = 0.0;
    Double ExpenseSum = 0.0;
    Double TotalSum = 0.0;
    TextView tv_search_income_amount;
    TextView tv_search_expenses_amount;
    TextView tv_search_sum_amount;
    ConstraintLayout cl_search;
    List<Transaction> transactions;
    String searchValue = "";
    boolean Marked = false;
    boolean Paid = true;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        DatabaseHelper db = new DatabaseHelper(this);

        ListView lv_transactions_search = findViewById(R.id.lv_transactions_search);
        tv_search_income_amount = findViewById(R.id.tv_search_income_amount);
        tv_search_expenses_amount = findViewById(R.id.tv_search_expenses_amount);
        tv_search_sum_amount = findViewById(R.id.tv_search_sum_amount);
        cl_search = findViewById(R.id.cl_search);
        EditText et_seartch = findViewById(R.id.et_seartch);
        Button btn_cansel_search = findViewById(R.id.btn_cansel_search);
        Button btn_share_transactions = findViewById(R.id.btn_share_transactions);
        Button btn_search_bookmark = findViewById(R.id.btn_search_bookmark);
        View btn_paid_status = findViewById(R.id.btn_paid_status);
        TextView tv_paid_status = findViewById(R.id.tv_paid_status);

        btn_cansel_search.setOnClickListener( e-> {
            Intent intent1 = new Intent(SearchActivity.this, statisticsActivity.class);
            startActivity(intent1);
        });

        btn_share_transactions.setOnClickListener( e-> {
            shareView(cl_search);
        });

        btn_search_bookmark.setOnClickListener( e-> {
            if (Marked){
                Marked = false;
                btn_search_bookmark.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_action_bookmark_not_added));
            } else {
                Marked = true;
                btn_search_bookmark.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_action_bookmark));
            }
            List<Transaction> transactions = db.searchTransactionByEverything(searchValue,Marked ? 1:0,Paid ? 1:0);
            TransactionAdapter adapter = new TransactionAdapter(SearchActivity.this, transactions);
            lv_transactions_search.setAdapter(adapter);
        });

        btn_paid_status.setOnClickListener( e-> {
            if (Paid){
                Paid = false;
                tv_paid_status.setText("Unpaid");
                btn_paid_status.setBackground(ContextCompat.getDrawable(this, R.drawable.not_paid_back));
            } else {
                Paid = true;
                tv_paid_status.setText("Paid");
                btn_paid_status.setBackground(ContextCompat.getDrawable(this, R.drawable.paid_back));
            }
            List<Transaction> transactions = db.searchTransactionByEverything(searchValue,Marked ? 1:0,Paid ? 1:0);
            TransactionAdapter adapter = new TransactionAdapter(SearchActivity.this, transactions);
            lv_transactions_search.setAdapter(adapter);
        });


        transactions = db.getAllTransactionsWhereClause(DatabaseHelper.COL_TRANSACTION_BOOK_MARK + "=" +(Marked ? 1 : 0) + " AND " + DatabaseHelper.COL_TRANSACTION_PAID + "=" + (Paid ? 1 : 0));
        db.setSettings();

        setStatisticsNumbers(transactions);

        TransactionAdapter adapter = new TransactionAdapter(SearchActivity.this, transactions);
        lv_transactions_search.setAdapter(adapter);

        lv_transactions_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<Transaction> Transactions = db.getAllTransactions();
                Intent intent = new Intent(SearchActivity.this, TransactionActivity.class);
                intent.putExtra("Selected_Transaction_Id",Transactions.get(position).getId());
                startActivity(intent);
            }
        });

        et_seartch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Log.i("SACL","beforeTextChanged");
                //Log.i("SACL","CharSequence: " + s + ",start: " + start + ",count: " + count + ",after: " + after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.i("SACL","onTextChanged");
                //Log.i("SACL","CharSequence: " + s + ",start: " + start + ",before: " + before + ",count: " + count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Log.i("SACL","afterTextChanged");
                //Log.i("SACL","Editable: " + s);

                searchValue = s.toString();

                List<Transaction> transactions = db.searchTransactionByEverything(searchValue,Marked ? 1:0,Paid ? 1:0);
                TransactionAdapter adapter = new TransactionAdapter(SearchActivity.this, transactions);
                lv_transactions_search.setAdapter(adapter);

            }
        });

    }

    public void setStatisticsNumbers(List<Transaction> transactions){
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

        tv_search_income_amount.setText("₪ " + IncomeSum.toString());
        tv_search_expenses_amount.setText("₪ " + ExpenseSum.toString());
        tv_search_sum_amount.setText("₪ " + TotalSum.toString());
    }

    private Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(
                view.getWidth(),
                view.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
    private Uri saveBitmapToCache(Context context, Bitmap bitmap) {
        File cachePath = new File(context.getCacheDir(), "images");
        cachePath.mkdirs();

        File file = new File(cachePath, searchValue + ".png");

        try (FileOutputStream stream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".provider",
                file
        );
    }
    private void shareView(View view) {
        Bitmap bitmap = viewToBitmap(view);
        Uri imageUri = saveBitmapToCache(this, bitmap);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, "Share transactions"));
    }

}