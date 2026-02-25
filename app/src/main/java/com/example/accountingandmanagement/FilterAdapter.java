package com.example.accountingandmanagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String> data;
    public TextView tv_filter;
    public Button btn_filter_left;
    public Button btn_filter_right;

    public FilterAdapter(Context context, ArrayList<String>data, TextView tv_filter, Button btn_filter_left, Button btn_filter_right){
        this.context = context;
        this.data = data;
        this.tv_filter = tv_filter;
        this.btn_filter_left = btn_filter_left;
        this.btn_filter_right = btn_filter_right;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.payment_method_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        Log.i("FA","onBindViewHolder - pos: " + position);
        holder.paymentmethod.setText(data.get(position));


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
    }

    @Override
    public int getItemCount(){
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView paymentmethod;
        CardView cardView;

        public ViewHolder(View itemView){
            super(itemView);

            this.paymentmethod = itemView.findViewById(R.id.tv_payment_method_item);
            this.cardView = itemView.findViewById(R.id.cv_payment_method_item);


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i > data.size(); i++){
                        if (data.get(i).equals(paymentmethod.getText())){
                            selectedPosition = i;
                            statisticsActivity.setFilterText(i);
                        }
                    }
                }
            });


        }
    }

    private int selectedPosition = -1;

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }
}
