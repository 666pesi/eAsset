package com.example.myapplication;

import android.os.Bundle;
import android.widget.ExpandableListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity2 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        InventoryManager.initialize(this);
        ExpandableListView expandableListView = findViewById(R.id.expandableListView);

        Map<String, List<Item>> itemsByRoom = new HashMap<>();
        for (Item item : InventoryManager.getAllItems()) {
            String room = item.getRoom();
            itemsByRoom.computeIfAbsent(room, k -> new ArrayList<>()).add(item);
        }

        ExpandableItemAdapter adapter = new ExpandableItemAdapter(
                this,
                new ArrayList<>(itemsByRoom.keySet()),
                new ArrayList<>(itemsByRoom.values()),
                expandableListView
        );

        expandableListView.setAdapter(adapter);
    }
}