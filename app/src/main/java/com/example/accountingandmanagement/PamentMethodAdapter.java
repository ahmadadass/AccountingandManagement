package com.example.accountingandmanagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.List;

public class PamentMethodAdapter extends BaseAdapter {

    private Context context;
    private List<String> paymentmethodList;
    private LayoutInflater inflater;

    @Override public int getCount() {
        return paymentmethodList.size();
    }
    @Override public Object getItem(int position) {
        return paymentmethodList.get(position);
    }
    @Override public long getItemId(int position) {
        return position;
    }

    public PamentMethodAdapter(Context context, List<String> paymentmethodList){
        this.context = context;
        this.paymentmethodList = paymentmethodList;
        this.inflater = LayoutInflater.from(context);
    }

    static class ViewHolder {
        TextView paymentmethod;
        CardView cardView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.payment_method_item, parent, false);
            holder = new ViewHolder();
            holder.paymentmethod = convertView.findViewById(R.id.tv_pament_method_item);
            holder.cardView = convertView.findViewById(R.id.cv_payment_method_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.cardView.animate()
                .scaleX(position == selectedPosition ? 1.2f : 1f)
                .scaleY(position == selectedPosition ? 1.2f : 1f)
                .setDuration(120)
                .start();


        if (position == selectedPosition) {
            //holder.CardView.setSelected(true);
            @SuppressLint("ResourceAsColor") ColorStateList color = ColorStateList.valueOf(R.color.colorPrimary);
            holder.cardView.setCardBackgroundColor(color);
            holder.cardView.setCardElevation(12f);
        } else {
            //holder.CardView.setSelected(false);
            @SuppressLint("ResourceAsColor") ColorStateList color = ColorStateList.valueOf(R.color.colorPMItem);
            holder.cardView.setCardBackgroundColor(color);
            holder.cardView.setCardElevation(4f);
        }

        holder.paymentmethod.setText(paymentmethodList.get(position));

        return convertView;
    }
    private int selectedPosition = -1;

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

}