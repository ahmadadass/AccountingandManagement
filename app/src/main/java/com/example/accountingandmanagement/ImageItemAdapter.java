package com.example.accountingandmanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ImageItemAdapter  extends BaseAdapter {
    private List<ImageItem> itemList;
    private LayoutInflater inflater;


    @Override public int getCount() {
        return itemList.size();
    }
    @Override public Object getItem(int position) {
        return itemList.get(position);
    }
    @Override public long getItemId(int position) {
        return position;
    }

    public ImageItemAdapter(Context context, List<ImageItem> itemList){
        this.itemList = itemList;
        this.inflater = LayoutInflater.from(context);
    }

    static class ViewHolder {
        ImageView imageView;
        TextView titelView;
        TextView detalseView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.navigation_bar_item, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.iv_navigation_item_icon);
            holder.titelView = convertView.findViewById(R.id.tv_navigation_item_title);
            holder.detalseView = convertView.findViewById(R.id.tv_navigation_item_detalse);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imageView.setImageBitmap(itemList.get(position).getImage());
        holder.titelView.setText(itemList.get(position).getTitel());

        if (itemList.get(position).getDetalse() == null || itemList.get(position).getDetalse().isEmpty()){
            holder.detalseView.setVisibility(View.GONE);
        } else {
            holder.detalseView.setVisibility(View.VISIBLE);
            holder.detalseView.setText(itemList.get(position).getDetalse());
        }

        return convertView;
    }
}