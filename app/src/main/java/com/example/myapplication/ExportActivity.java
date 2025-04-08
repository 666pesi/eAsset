package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.List;

public class ExportActivity extends AppCompatActivity {
    private Button exportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        exportButton = findViewById(R.id.exportButton);
        exportButton.setOnClickListener(v -> showExportConfirmation());
    }

    private void showExportConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Potvrdiť export")
                .setMessage("Ste si istí, že chcete exportovať všetky údaje o zásobách?")
                .setPositiveButton("Export", (dialog, which) -> exportData())
                .setNegativeButton("Zrušiť", null)
                .show();
    }

    private void exportData() {
        List<Item> updatedData = InventoryManager.getAllItems();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-production-b7ac.up.railway.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<Void> call = apiService.exportData(updatedData);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ExportActivity.this, "Údaje boli úspešne exportované!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ExportActivity.this, "Nepodarilo sa exportovať údaje", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ExportActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}