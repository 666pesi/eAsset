package com.example.myapplication;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryManager {
    private static List<Item> items;
    private static Map<String, List<String>> inventoryCheck = new HashMap<>();

    public static void initialize(Context context) {
        if (items == null) {
            items = JsonUtils.parseItems(JsonLoader.loadJsonFromRaw(context, R.raw.items));
        }
        if (inventoryCheck.isEmpty()) {
            inventoryCheck = JsonUtils.parseInventoryCheck(JsonLoader.loadJsonFromRaw(context, R.raw.inventory_check));
        }
    }

    public static void updateItems(List<Item> newItems) {
        items = newItems;
    }

    public static void updateInventoryCheck(Map<String, List<String>> newInventoryCheck) {
        inventoryCheck = newInventoryCheck;
    }

    public static List<String> getAssignedRooms(String username) {
        return inventoryCheck.getOrDefault(username, new ArrayList<>());
    }

    public static List<Item> getItemsForRoom(String roomCode) {
        List<Item> filteredItems = new ArrayList<>();
        for (Item item : items) {
            if (item.getRoom().equalsIgnoreCase(roomCode)) {
                filteredItems.add(item);
            }
        }
        return filteredItems;
    }

    public static List<Item> getAllItems() {
        return items;
    }

    public static Item getItemByCode(String code) {
        for (Item item : items) {
            if (item.getCode().equalsIgnoreCase(code)) {
                return item;
            }
        }
        return null;
    }

    public static void updateItemRoom(String code, String newRoom) {
        for (Item item : items) {
            if (item.getCode().equalsIgnoreCase(code)) {
                item.setRoom(newRoom);
                break;
            }
        }
    }

    public static boolean checkItem(String code) {
        for (Item item : items) {
            if (item.getCode().equalsIgnoreCase(code)) {
                item.setChecked(true);
                return true;
            }
        }
        return false;
    }

    public static void addItem(Item item) {
        items.add(item);
    }
}