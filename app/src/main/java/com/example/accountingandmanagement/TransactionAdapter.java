package com.example.accountingandmanagement;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;

public class TransactionAdapter extends BaseAdapter {

    private Context context;
    private List<Transaction> transactionList;
    private LayoutInflater inflater;
    public static Settings settings = new Settings(1,1,true,false,false,true,"Bank,Cash,Other","allowance,salary,mortgage,transport,shopping,subscription"); // temp value have to change in the start. ,Show More
    @Override public int getCount() {
        return transactionList.size();
    }
    @Override public Object getItem(int position) {
        return transactionList.get(position);
    }
    @Override public long getItemId(int position) {
        return position;
    }

    public TransactionAdapter(Context context, List<Transaction> transactionList){
        this.context = context;
        this.transactionList = transactionList;
        this.inflater = LayoutInflater.from(context);
    }

    static class ViewHolder {
        TextView nameView;
        TextView typeView;
        TextView notesView;
        TextView timeView;
        TextView paymentMethodView;
        TextView incomeView;
        TextView expenseView;
        ConstraintLayout constraintLayout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.transaction_item, parent, false);
            holder = new ViewHolder();
            holder.nameView = convertView.findViewById(R.id.tv_transaction_name);
            holder.typeView = convertView.findViewById(R.id.tv_transaction_type);
            holder.notesView = convertView.findViewById(R.id.tv_transaction_notes);
            holder.timeView = convertView.findViewById(R.id.tv_transaction_time);
            holder.paymentMethodView = convertView.findViewById(R.id.tv_transaction_payment_method);
            holder.incomeView = convertView.findViewById(R.id.tv_transaction_income_amount);
            holder.expenseView = convertView.findViewById(R.id.tv_transaction_expense_amount);
            holder.constraintLayout = convertView.findViewById(R.id.constraint_layout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.nameView.setText(TransactionActivity.ifEmpty(transactionList.get(position).getName()));
        holder.typeView.setText(TransactionActivity.ifEmpty(transactionList.get(position).getType()));
        holder.notesView.setText(TransactionActivity.ifEmpty(transactionList.get(position).getNotes()));
        holder.timeView.setText(TransactionActivity.ifEmpty(Transaction.unixToString(transactionList.get(position).getTime())));
        holder.paymentMethodView.setText(TransactionActivity.ifEmpty(transactionList.get(position).getPayment_method()));
        Double Amount = transactionList.get(position).getAmount();
        if (Amount >= 0){
            holder.incomeView.setText("₪" + Amount);
            holder.expenseView.setText("-");
        } else {
            holder.incomeView.setText("-");
            holder.expenseView.setText("₪" + Amount);
        }

        if (!transactionList.get(position).getPaidStatus()) {
            //holder.constraintLayout.setBackgroundColor(Color.parseColor("#E6C7C4"));
            //holder.constraintLayout.setBackgroundResource(R.drawable.unpaid_back);
            // this is for the unpaid item.
        }

        if (!settings.getNameV())
            holder.nameView.setVisibility(View.GONE);
        else
            holder.nameView.setVisibility(View.VISIBLE);

        if(!settings.getTypeV())
            holder.typeView.setVisibility(View.GONE);
        else
            holder.typeView.setVisibility(View.VISIBLE);

        if(!settings.getNotesV())
            holder.notesView.setVisibility(View.GONE);
        else
            holder.notesView.setVisibility(View.VISIBLE);

        if(!settings.getTimeV())
            holder.timeView.setVisibility(View.GONE);
        else
            holder.timeView.setVisibility(View.VISIBLE);


        return convertView;
    }
}
/*

package com.example.lanmessenger;

import static com.example.lanmessenger.LoginActivity.onlineUsers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class UserAdapter extends BaseAdapter {

    private Context context;
    private List<User> userList;
    private LayoutInflater inflater;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int i) {
        return userList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    static class ViewHolder {
        TextView nameView;
        TextView statusView;
        TextView ipView;
        TextView newMsgCountView;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_user, parent, false);
            holder = new ViewHolder();
            holder.nameView = convertView.findViewById(R.id.tv_name);
            holder.statusView = convertView.findViewById(R.id.tv_status);
            holder.ipView = convertView.findViewById(R.id.tv_ip);
            holder.newMsgCountView = convertView.findViewById(R.id.tv_new_messages);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        User user = userList.get(i);
        boolean isOnline = false;
        for (String userIp : onlineUsers){
            int indexOf = userIp.indexOf(":");

            if (userIp.substring(0,indexOf).equals(user.getIp())){
                isOnline = true;
            }
        }

        if (isOnline) {
            holder.statusView.setText("● ");
            holder.statusView.setTextColor(-11751600);
            //Log.i("Color",String.valueOf(holder.statusView.getCurrentTextColor()));
        } else {
            holder.statusView.setText("○ ");
            holder.statusView.setTextColor(-975538);
        }

        holder.nameView.setText(user.getName());

        holder.ipView.setText(user.getIp());
        int newMessages = user.getNewMessageNumber();
        if (newMessages > 0) {
            holder.newMsgCountView.setText("New: " + newMessages);
            holder.newMsgCountView.setVisibility(View.VISIBLE);
        } else {
            holder.newMsgCountView.setVisibility(View.GONE);
        }

        return convertView;
    }
}

 */