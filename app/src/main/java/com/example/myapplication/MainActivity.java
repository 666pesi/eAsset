package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize InventoryManager
        InventoryManager.initialize(this);

        findViewById(R.id.logoutButton).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public void goToActivity1(View view) {
        String username = getIntent().getStringExtra("username");
        if (username == null) {
            username = "defaultUser";
        }

        List<String> assignedRooms = InventoryManager.getAssignedRooms(username);
        Intent intent = new Intent(this, Activity1.class);
        intent.putStringArrayListExtra("assignedRooms", new ArrayList<>(assignedRooms));
        startActivity(intent);
    }

    public void goToActivity2(View view) {
        startActivity(new Intent(this, Activity2.class));
    }

    public void goToActivity3(View view) {
        startActivity(new Intent(this, Activity3.class));
    }

    public void goToImportActivity(View view) {
        startActivity(new Intent(this, ImportActivity.class));
    }

    public void goToExportActivity(View view) {
        startActivity(new Intent(this, ExportActivity.class));
    }
}