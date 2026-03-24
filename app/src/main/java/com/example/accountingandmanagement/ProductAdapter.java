package com.example.accountingandmanagement;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends BaseAdapter {
    public Context context;
    public ArrayList<ProductData> productDataList;
    public LayoutInflater inflater;
    public DatabaseHelper db;
    @Override
    public int getCount() {
        return productDataList.size();
    }
    @Override
    public Object getItem(int position) {
        return productDataList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    public ProductAdapter(Context context, ArrayList<ProductData> productDataList) {
        this.context = context;
        this.productDataList = productDataList;
        this.inflater = LayoutInflater.from(context);
        db = new DatabaseHelper(context);
    }
    static class ViewHolder {
        ImageView barcodeView;
        TextView barcodeTextView;
        TextView nameView;
        TextView typeView;
        TextView totalView;
        ImageView moreView;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.prodcut_item, parent, false);
            holder = new ViewHolder();
            holder.barcodeView = convertView.findViewById(R.id.iv_product_barcode);
            holder.barcodeTextView = convertView.findViewById(R.id.tv_product_barcode);
            holder.nameView = convertView.findViewById(R.id.tv_product_name);
            holder.typeView = convertView.findViewById(R.id.tv_product_type);
            holder.totalView = convertView.findViewById(R.id.tv_product_total);
            holder.moreView = convertView.findViewById(R.id.tv_product_more);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            // Generate BitMatrix and convert to Bitmap
            BitMatrix bitMatrix = multiFormatWriter.encode(productDataList.get(position).getId(), BarcodeFormat.CODE_128, 800, 300);
            Bitmap bitmap = new BarcodeEncoder().createBitmap(bitMatrix);
            holder.barcodeView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        holder.barcodeTextView.setText(productDataList.get(position).getId());
        holder.nameView.setText(productDataList.get(position).getName());
        holder.typeView.setText(productDataList.get(position).getType());

        Double productTotalSum = 0.0;
        ArrayList<Product> productArrayList = db.getAllProdcutsWhereClause(DatabaseHelper.COL_PRODUCTS_ID + " = " + productDataList.get(position).getId());
        for (Product product:productArrayList){
            Transaction transaction = db.getTransactionById(product.getStringTransactionId());
            if (transaction.getAmount() > 0){
                productTotalSum += product.getPrice();
            } else {
                productTotalSum -= product.getPrice();
            }
        }
        holder.totalView.setText("₪ " + productTotalSum);
        /*
        ArrayList<Transaction> Transactions = db.searchTransactionByEverything(productDataList.get(position).getId(),0,1,0);
        if (Transactions != null && Transactions.size() != 0){
            for (Transaction transaction:Transactions) {
                for (Product product:transaction.getProducts()){
                    if(productDataList.get(position).getId().equals(product.getId())){
                        if (transaction.getAmount() > 0) {
                            productTotalSum += product.getPrice();
                        } else
                            productTotalSum -= product.getPrice();
                    }
                }
            }
            holder.totalView.setText("₪ " + productTotalSum);
        }*/

        holder.moreView.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.moreView);

            popupMenu.getMenu().add("Edit");
            popupMenu.getMenu().add("Delete");


            popupMenu.setOnMenuItemClickListener(item -> {
                String title = item.getTitle().toString();

                if (title.equals("Edit")) {

                    showCustomDialog(productDataList.get(position).getId(),"Edit payment method",productDataList.get(position).getName(),productDataList.get(position).getType());

                } else if (title.equals("Delete")) {

                    db.deleteProductData(productDataList.get(position).getId());

                    notifyDataSetChanged();
                }

                return true;
            });
            popupMenu.show();
        });

        return convertView;
    }

    public void showCustomDialog(String id,String title, String editTextText, String editTextText2) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.edit_text_save_cancel_dialog);

        // make background transparent
        dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));

        // Access views within the dialog
        TextView tv_title_dialog = dialog.findViewById(R.id.tv_title_dialog);
        EditText et_text_dialog = dialog.findViewById(R.id.et_text_dialog);
        EditText et_text2_dialog = dialog.findViewById(R.id.et_text2_dialog);
        Button btn_cancel_dialog = dialog.findViewById(R.id.btn_cancel_dialog);
        Button btn_save_dialog = dialog.findViewById(R.id.btn_save_dialog);

        tv_title_dialog.setText(title);
        if (editTextText != null || !editTextText.equals("")) {
            et_text_dialog.setText(editTextText);
        }
        if (editTextText2 != null || !editTextText2.equals("")) {
            et_text2_dialog.setVisibility(View.VISIBLE);
            et_text2_dialog.setText(editTextText2);
        }

        btn_save_dialog.setOnClickListener( e -> {

            if ((et_text_dialog != null || !et_text_dialog.equals("")) && (editTextText2 != null || !editTextText2.equals(""))) {

                db.updateProductData(id,editTextText,editTextText2);

                notifyDataSetChanged();

                dialog.dismiss();
            }

        });

        btn_cancel_dialog.setOnClickListener( e -> {dialog.dismiss();});

        dialog.show();
    }
}
