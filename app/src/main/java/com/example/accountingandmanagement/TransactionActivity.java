package com.example.accountingandmanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TransactionActivity extends AppCompatActivity {

    CardView cardView;
    Transaction selected_transaction;

    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        DatabaseHelper db = new DatabaseHelper(this);

        Intent intent = getIntent();

        String selected_transaction_id = intent.getStringExtra("Selected_Transaction_Id");

        if(selected_transaction_id.equals("")){
            Intent Intent = new Intent(TransactionActivity.this, statisticsActivity.class);
            startActivity(Intent);
        }

        selected_transaction = db.getTransactionById(selected_transaction_id);

        cardView = findViewById(R.id.cardView);
        TextView tv_transaction_view_payment_method = findViewById(R.id.tv_transaction_view_payment_method);
        TextView tv_date_time = findViewById(R.id.tv_date_time);
        TextView tv_transaction_view_amount = findViewById(R.id.tv_transaction_view_amount);
        TextView tv_transaction_view_name = findViewById(R.id.tv_transaction_view_name);
        TextView tv_transaction_view_notes = findViewById(R.id.tv_transaction_view_notes);
        TextView tv_transaction_view_type = findViewById(R.id.tv_transaction_view_type);
        Button btn_cansel_transaction_view = findViewById(R.id.btn_cansel_transaction_view);
        Button btn_delete_transaction = findViewById(R.id.btn_delete_transaction);
        Button btn_edit_transaction = findViewById(R.id.btn_edit_transaction);
        Button btn_share_transaction_view = findViewById(R.id.btn_share_transaction_view);
        View ly_paid_status = findViewById(R.id.ly_paid_status_transaction_view);
        TextView tv_paid_status = findViewById(R.id.tv_paid_status_transaction_view);

        tv_transaction_view_payment_method.setText(ifEmpty(selected_transaction.getPayment_method()));
        tv_date_time.setText(ifEmpty(Transaction.printDate(selected_transaction.getTime()) + ",\n" + Transaction.printTime(selected_transaction.getTime())));
        tv_transaction_view_amount.setText(ifEmpty(selected_transaction.getAmount().toString()));
        tv_transaction_view_name.setText(ifEmpty(selected_transaction.getName()));
        tv_transaction_view_notes.setText(ifEmpty(selected_transaction.getNotes()));
        tv_transaction_view_type.setText(ifEmpty(selected_transaction.getType()));

        if (selected_transaction.getPaidStatus()) {
            tv_paid_status.setText("Paid");
            ly_paid_status.setBackgroundResource(R.drawable.paid_back);
        } else {
            tv_paid_status.setText("Unpaid");
            ly_paid_status.setBackgroundResource(R.drawable.not_paid_back);
        }

        btn_cansel_transaction_view.setOnClickListener( e-> {
            Intent intent2 = new Intent(TransactionActivity.this, statisticsActivity.class);
            startActivity(intent2);
        });

        btn_delete_transaction.setOnClickListener( e-> {

            Intent intent1 = new Intent(this, statisticsActivity.class);
            startActivity(intent1);

            db.deleteTransaction(selected_transaction_id);

            /*
            if (db.deleteTransaction(selected_transaction_id)) {
                Toast.makeText(this, "Transaction deleted", Toast.LENGTH_SHORT).show();

                Intent intent1 = new Intent(TransactionActivity.this, statisticsActivity.class);
                startActivity(intent1);
            } else {
                Toast.makeText(this, "Error deleting transaction", Toast.LENGTH_SHORT).show();
            }*/
        });

        btn_edit_transaction.setOnClickListener( e-> {
            Intent intent3 = new Intent(TransactionActivity.this, AddEditIncomeExpenseActivity.class);
            if (selected_transaction.getAmount() <=0 ) {
                intent3.putExtra("Transaction_Title", "Expense");
            } else {
                intent3.putExtra("Transaction_Title", "Income");
            }
            intent3.putExtra("Selected_Transaction_Id",selected_transaction.getId());
            startActivity(intent3);
        });


        btn_share_transaction_view.setOnClickListener( e-> {
            shareView(cardView);
        });

    }

    public static String ifEmpty(String text){
        if (text == null || text.isEmpty())
            return "-";
        else
            return text;
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

        File file = new File(cachePath, selected_transaction.getName() + "_" + selected_transaction.getId() + ".png");

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

        startActivity(Intent.createChooser(shareIntent, "Share transaction"));
    }

    /*
    private void saveButton() {
        cardView.setDrawingCacheEnabled(true);
        cardView.buildDrawingCache();
        cardView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = cardView.getDrawingCache();
        save(bitmap);
    } // this fun is for converting a view to bitmap

    private void save(Bitmap bitmap) {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file= new File(root+"/Download");
        String fileName = "Simple Image.jpg";
        File myfile = new File(file, fileName);
        if (myfile.exists()){
            myfile.delete();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(myfile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            FileOutputStream.close();
            Toast.makeText(this,"Image Saved", Toast.LENGTH_SHORT).show();
            cardView.setDrawingCacheEnabled(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    } // this one is for saving a bitmap to a file Note: add permissions ->
        //<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="28" />
    */
}