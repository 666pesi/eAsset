package com.example.myapplication;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class JsonUtils {
    public static List<Item> parseItems(String json) {
        Gson gson = new Gson();
        Type itemListType = new TypeToken<List<Item>>() {}.getType();
        return gson.fromJson(json, itemListType);
    }

    public static Map<String, List<String>> parseInventoryCheck(String json) {
        Gson gson = new Gson();
        Type inventoryCheckType = new TypeToken<Map<String, List<String>>>() {}.getType();
        return gson.fromJson(json, inventoryCheckType);
    }

    public static Map<String, String> parseRooms(String json) {
        Gson gson = new Gson();
        Type roomMapType = new TypeToken<Map<String, String>>() {}.getType();
        return gson.fromJson(json, roomMapType);
    }
}