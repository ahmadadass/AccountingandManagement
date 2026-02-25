package com.example.accountingandmanagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.ViewHolder>  {

    private Context context;
    private List<String> paymentmethodList;
    private LayoutInflater inflater;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onShowMoreClick(int currentSelectedPosition);
    }

    private OnItemClickListener listener;

    public PaymentMethodAdapter(Context context, List<String> list, OnItemClickListener listener) {
        this.context = context;
        this.paymentmethodList = list;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView paymentmethod;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            paymentmethod = itemView.findViewById(R.id.tv_payment_method_item);
            cardView = itemView.findViewById(R.id.cv_payment_method_item);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.payment_method_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.paymentmethod.setText(paymentmethodList.get(position));

        // 🔥 Random height for uneven look
        ViewGroup.LayoutParams params = holder.cardView.getLayoutParams();
        //params.height = (position % 2 == 0) ? 250 : 350;
        holder.cardView.setLayoutParams(params);

        // Selection animation
        holder.cardView.animate()
                .scaleX(position == selectedPosition ? 1.1f : 1f)
                .scaleY(position == selectedPosition ? 1.1f : 1f)
                .setDuration(120)
                .start();

        if (position == selectedPosition) {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            holder.cardView.setCardElevation(12f);
        } else {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorPMItem));
            holder.cardView.setCardElevation(4f);
        }

        holder.itemView.setOnClickListener(v -> {

            String item = paymentmethodList.get(position);

            // 👉 If "Show More" clicked
            if (item.equals("Show More")) {/*
                Intent intent = new Intent(context, AddPamentMethodActivity.class);
                context.startActivity(intent);*/
                if (listener != null) {
                    listener.onShowMoreClick(selectedPosition);
                }
                return;
            }

            int oldPosition = selectedPosition;
            selectedPosition = position;

            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onItemClick(position);
            }
        });

        /*holder.itemView.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = position;

            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onItemClick(position);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return paymentmethodList.size();
    }
    private int selectedPosition = -1;

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }


}