package com.example.accountingandmanagement;

import static com.example.accountingandmanagement.TransactionAdapter.settings;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NavigationBarAdapter extends BaseAdapter {

    public Context context;
    public List<NavigationBarItem> navigationBarItemList;
    public LayoutInflater inflater;
    public DatabaseHelper db;
    @Override public int getCount() {
        return navigationBarItemList.size();
    }
    @Override public Object getItem(int position) {
        return navigationBarItemList.get(position);
    }
    @Override public long getItemId(int position) {
        return position;
    }

    public NavigationBarAdapter(Context context, List<NavigationBarItem> navigationBarItemList){
        this.context = context;
        this.navigationBarItemList = navigationBarItemList;
        this.inflater = LayoutInflater.from(context);
        db = new DatabaseHelper(context); // TODO make fun's for database 
    }

    static class ViewHolder {
        ImageView iconView;
        TextView titelView;
        TextView descView;
        ImageView moreView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.navigation_bar_item, parent, false);
            holder = new ViewHolder();
            holder.iconView = convertView.findViewById(R.id.iv_navigation_item_icon);
            holder.titelView = convertView.findViewById(R.id.tv_navigation_item_title);
            holder.descView = convertView.findViewById(R.id.tv_navigation_item_detalse);
            holder.moreView = convertView.findViewById(R.id.iv_more_navigation_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (navigationBarItemList.get(position).icon_fun){
            if (navigationBarItemList.get(position).isSelcted()){
                holder.iconView.setImageResource(R.drawable.ic_action_radio_button_checked);
            } else {
                holder.iconView.setImageResource(R.drawable.ic_action_radio_button_unchecked);
            }
            /*holder.iconView.setOnClickListener( e -> {
                for (int i = 0; i < navigationBarItemList.size(); i++){
                    if (navigationBarItemList.get(i).isSelcted()){
                        navigationBarItemList.get(i).setSelcted(false);
                    }
                }

                navigationBarItemList.get(position).setSelcted(true);
                notifyDataSetChanged();
            });*/
        } else {
            holder.iconView.setImageResource(navigationBarItemList.get(position).getIcon());
        }

        holder.titelView.setText(navigationBarItemList.get(position).getTitel());
        holder.descView.setText(navigationBarItemList.get(position).getDesc());


        if (navigationBarItemList.get(position).getArrayMoreActions().size() != 0) {

            holder.moreView.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context, holder.moreView);


                for(String action:navigationBarItemList.get(position).getArrayMoreActions()){
                    popupMenu.getMenu().add(action);
                }

                popupMenu.setOnMenuItemClickListener(item -> {
                    String title = item.getTitle().toString();

                    if (title.equals("Edit")) {

                        showCustomDialog("Edit payment method",navigationBarItemList.get(position).getTitel());

                    } else if (title.equals("Delete")) {
                        ArrayList<String> arrayList = Settings.stringToArray(settings.getPaymentMethodList());
                        arrayList.remove(position);
                        String  paymentMethodList = Settings.arrayToString(arrayList);
                        settings.setPaymentMethodList(paymentMethodList);

                        ArrayList<String> more_actions = new ArrayList<>();
                        more_actions.add("Edit");
                        more_actions.add("Delete");

                        navigationBarItemList.clear();
                        for (String paymentMethod:arrayList){
                            if (!paymentMethod.equals("Show More")) {
                                navigationBarItemList.add(new NavigationBarItem(paymentMethod, "", R.drawable.ic_action_radio_button_unchecked, true, more_actions, false));
                            }
                        }

                        notifyDataSetChanged();

                        db.updateSettings(settings.getNameV(),settings.getTypeV(),settings.getNotesV(),settings.getTimeV(),settings.getUserId(),settings.getPaymentMethodList(),settings.getTypeList());
                    }

                    return true;
                });
                popupMenu.show();
            });
        } else {
            holder.moreView.setVisibility(View.GONE);
        }

        return convertView;
    }
    public void showCustomDialog(String title, String editTextText) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.add_payment_method_dialog);

        // make background transparent
        dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));

        // Access views within the dialog
        TextView tv_add_payment_method_title = dialog.findViewById(R.id.tv_add_payment_method_title);
        EditText et_add_payment_method_text = dialog.findViewById(R.id.et_add_payment_method_text);
        Button btn_cancel_add_payment_method = dialog.findViewById(R.id.btn_cancel_payment_method);
        Button btn_save_add_payment_method = dialog.findViewById(R.id.btn_save_payment_method);

        tv_add_payment_method_title.setText(title);
        if (editTextText != null || !editTextText.equals("")) {
            et_add_payment_method_text.setText(editTextText);
        }

        btn_save_add_payment_method.setOnClickListener( e -> {

            ArrayList<String> arrayList = Settings.stringToArray(settings.getPaymentMethodList());
            if (et_add_payment_method_text != null || !et_add_payment_method_text.equals("")) {
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).equals(editTextText) && !et_add_payment_method_text.getText().equals("")) {
                        arrayList.set(i, String.valueOf(et_add_payment_method_text.getText()));

                    }
                }

                ArrayList<String> more_actions = new ArrayList<>();
                more_actions.add("Edit");
                more_actions.add("Delete");

                navigationBarItemList.clear();
                for (String paymentMethod:arrayList){
                    if (!paymentMethod.equals("Show More")) {
                        navigationBarItemList.add(new NavigationBarItem(paymentMethod, "", R.drawable.ic_action_radio_button_unchecked, true, more_actions, false));
                    }
                }
                String  paymentMethodList = Settings.arrayToString(arrayList);
                settings.setPaymentMethodList(paymentMethodList);

                notifyDataSetChanged();

                db.updateSettings(settings.getNameV(),settings.getTypeV(),settings.getNotesV(),settings.getTimeV(),settings.getUserId(),settings.getPaymentMethodList(),settings.getTypeList());

                dialog.dismiss();
            }

        });

        btn_cancel_add_payment_method.setOnClickListener( e -> {dialog.dismiss();});

        dialog.show();
    }
}