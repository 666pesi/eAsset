package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.print.PrintManager;
import android.print.PrintDocumentAdapter;
import android.print.PrintAttributes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.util.List;

public class ExpandableItemAdapter extends BaseExpandableListAdapter {
    private final Context context;
    private final List<String> rooms;
    private final List<List<Item>> itemsByRoom;
    private ExpandableListView listView;

    public ExpandableItemAdapter(Context context, List<String> rooms, List<List<Item>> itemsByRoom, ExpandableListView listView) {
        this.context = context;
        this.rooms = rooms;
        this.itemsByRoom = itemsByRoom;
        this.listView = listView;
    }

    @Override
    public int getGroupCount() {
        return rooms.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return itemsByRoom.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return rooms.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return itemsByRoom.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item_room, parent, false);
        }

        String room = rooms.get(groupPosition);
        TextView roomTextView = convertView.findViewById(R.id.room_code);
        ImageView printButton = convertView.findViewById(R.id.printButton);
        ImageView expandIcon = convertView.findViewById(R.id.expand_icon);

        roomTextView.setText(room);
        expandIcon.setImageResource(isExpanded ? R.drawable.ic_expand_less : R.drawable.ic_expand_more);

        printButton.setOnClickListener(v -> {
            v.setEnabled(false);
            generateAndPrintQRCode(room, "", "");
            v.postDelayed(() -> v.setEnabled(true), 500);
        });

        convertView.setOnClickListener(v -> {
            if (isExpanded) {
                listView.collapseGroup(groupPosition);
            } else {
                listView.expandGroup(groupPosition);
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item_with_print, parent, false);
        }

        Item item = itemsByRoom.get(groupPosition).get(childPosition);
        TextView itemCodeTextView = convertView.findViewById(R.id.item_code);
        TextView itemNameTextView = convertView.findViewById(R.id.item_name);
        ImageView printButton = convertView.findViewById(R.id.printButton);

        itemCodeTextView.setText(item.getCode());
        itemNameTextView.setText(item.getName());

        printButton.setOnClickListener(v -> {
            v.setEnabled(false);
            generateAndPrintQRCode(item.getCode(), item.getName(), item.getRoom());
            v.postDelayed(() -> v.setEnabled(true), 500);
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private void generateAndPrintQRCode(String code, String name, String room) {
        String qrContent = code;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 300, 300);
            Bitmap qrCodeBitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.RGB_565);
            for (int x = 0; x < 300; x++) {
                for (int y = 0; y < 300; y++) {
                    qrCodeBitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            printQRCode(qrCodeBitmap, code);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to generate QR code", Toast.LENGTH_SHORT).show();
        }
    }

    private void printQRCode(Bitmap qrCodeBitmap, String codeText) {
        if (qrCodeBitmap == null) {
            Toast.makeText(context, "No QR code to print", Toast.LENGTH_SHORT).show();
            return;
        }

        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = new BitmapPrintDocumentAdapter(context, qrCodeBitmap, codeText);
        String jobName = "QR Code Print Job";
        printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
    }
}