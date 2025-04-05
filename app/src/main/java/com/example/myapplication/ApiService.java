package com.example.myapplication;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import java.util.List;
import java.util.Map;

public interface ApiService {
    @GET("/api/load")
    Call<List<Item>> loadData();

    @GET("/api/users")
    Call<List<Map<String, String>>> getUsers();

    @GET("/api/rooms")
    Call<List<Item>> getRooms();

    @GET("/api/inventory-check")
    Call<Map<String, List<String>>> getInventoryCheck();

    @POST("/api/export")
    Call<Void> exportData(@Body List<Item> items);
}