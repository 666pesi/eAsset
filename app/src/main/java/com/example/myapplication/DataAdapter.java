package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import java.util.List;

public class DataAdapter extends BaseAdapter {
    private final Context context;
    private final List<Item> items;

    public DataAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        Item currentItem = items.get(position);

        TextView itemCodeTextView = convertView.findViewById(R.id.item_code);
        TextView itemNameTextView = convertView.findViewById(R.id.item_name);
        CheckBox itemCheckedCheckBox = convertView.findViewById(R.id.item_checked);

        itemCodeTextView.setText(currentItem.getCode());
        itemNameTextView.setText(currentItem.getName());
        itemCheckedCheckBox.setChecked(currentItem.isChecked());

        return convertView;
    }
}