package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.Gson;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ImportActivity extends AppCompatActivity {
    private ListView listView;
    private Button fetchButton;
    private Button saveButton;
    private List<Item> fetchedItems;
    private static final String TAG = "ImportActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        listView = findViewById(R.id.listView);
        fetchButton = findViewById(R.id.fetchButton);
        saveButton = findViewById(R.id.saveButton);

        fetchButton.setOnClickListener(v -> fetchDataFromApi());
        saveButton.setOnClickListener(v -> {
            if (fetchedItems != null) {
                saveDataToFile(fetchedItems, "items.json");
                InventoryManager.updateItems(fetchedItems);
                Toast.makeText(this, "Údaje boli úspešne uložené", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Žiadne údaje na ukladanie", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDataFromApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-production-b7ac.up.railway.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // Fetch items
        apiService.loadData().enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fetchedItems = response.body();
                    displayData(fetchedItems);
                    fetchRooms();
                    fetchInventoryCheck();
                    Toast.makeText(ImportActivity.this, "Údaje boli úspešne načítané", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ImportActivity.this, "Nepodarilo sa načítať položky", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Toast.makeText(ImportActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchRooms() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-production-b7ac.up.railway.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getRooms().enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    saveDataToFile(response.body(), "rooms.json");
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Log.e(TAG, "Nepodarilo sa načítať izby", t);
            }
        });
    }

    private void fetchInventoryCheck() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-production-b7ac.up.railway.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getInventoryCheck().enqueue(new Callback<Map<String, List<String>>>() {
            @Override
            public void onResponse(Call<Map<String, List<String>>> call, Response<Map<String, List<String>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    saveDataToFile(response.body(), "inventory_check.json");
                    InventoryManager.updateInventoryCheck(response.body());
                }
            }

            @Override
            public void onFailure(Call<Map<String, List<String>>> call, Throwable t) {
                Log.e(TAG, "Nepodarilo sa načítať zásoby", t);
            }
        });
    }

    private void saveDataToFile(Object data, String fileName) {
        try (FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE)) {
            fos.write(new Gson().toJson(data).getBytes());
        } catch (IOException e) {
            Log.e(TAG, "Chybné uloženie " + fileName, e);
        }
    }

    private void displayData(List<Item> items) {
        String[] itemStrings = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            itemStrings[i] = item.getCode() + " - " + item.getName() + " (" + item.getRoom() + ")";
        }
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemStrings));
    }
}