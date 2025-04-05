package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.ScanOptions;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.List;

public class Activity1 extends AppCompatActivity {
    private DataAdapter adapter;
    private List<Item> items;
    private List<String> assignedRooms;
    private String currentRoomCode;
    private Button checkButton;
    private Button confirmRoomButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);

        EditText roomCodeInput = findViewById(R.id.roomCodeInput);
        confirmRoomButton = findViewById(R.id.confirmRoomButton);
        Button scanButton = findViewById(R.id.scanButton);
        EditText inventoryInput = findViewById(R.id.inventoryInput);
        checkButton = findViewById(R.id.checkButton);
        ListView inventoryListView = findViewById(R.id.inventoryListView);
        TextView errorText = findViewById(R.id.errorText);

        InventoryManager.initialize(this);
        assignedRooms = getIntent().getStringArrayListExtra("assignedRooms");

        items = new ArrayList<>();
        adapter = new DataAdapter(this, items);
        inventoryListView.setAdapter(adapter);

        confirmRoomButton.setOnClickListener(v -> {
            String roomCode = roomCodeInput.getText().toString().trim();
            if (roomCode.isEmpty()) {
                errorText.setText("Prosím vložte číslo miestnosti.");
                errorText.setVisibility(View.VISIBLE);
                return;
            }

            if (!assignedRooms.contains(roomCode)) {
                errorText.setText("Táto izba vám nie je pridelená.");
                errorText.setVisibility(View.VISIBLE);
                items.clear();
                adapter.notifyDataSetChanged();
                return;
            }

            currentRoomCode = roomCode;
            items.clear();
            items.addAll(InventoryManager.getItemsForRoom(roomCode));
            adapter.notifyDataSetChanged();
            errorText.setVisibility(View.GONE);
        });

        scanButton.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setPrompt("Naskenujte QR kód");
            options.setBeepEnabled(true);
            options.setOrientationLocked(true);
            options.setCaptureActivity(CaptureActivity.class);
            barcodeLauncher.launch(options);
        });

        checkButton.setOnClickListener(v -> {
            if (currentRoomCode == null || currentRoomCode.isEmpty()) {
                Toast.makeText(Activity1.this, "Najskôr potvrďte izbu!", Toast.LENGTH_SHORT).show();
                errorText.setText("Najskôr potvrďte izbu!");
                errorText.setVisibility(View.VISIBLE);
                return;
            }

            String inputCode = inventoryInput.getText().toString().trim();
            if (inputCode.isEmpty()) {
                errorText.setText("Zadajte kód položky.");
                errorText.setVisibility(View.VISIBLE);
                return;
            }

            Item item = InventoryManager.getItemByCode(inputCode);
            if (item == null) {
                Toast.makeText(Activity1.this, "Položka sa nenašla!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!item.getRoom().equals(currentRoomCode)) {
                new AlertDialog.Builder(Activity1.this)
                        .setTitle("Položka v nesprávnej miestnosti")
                        .setMessage("Tento predmet sa v tejto miestnosti nemá nachádzať. Chcete ju presunúť do " + currentRoomCode + "?")
                        .setPositiveButton("Áno", (dialog, which) -> {
                            InventoryManager.updateItemRoom(item.getCode(), currentRoomCode);
                            items.clear();
                            items.addAll(InventoryManager.getItemsForRoom(currentRoomCode));
                            adapter.notifyDataSetChanged();
                            Toast.makeText(Activity1.this, "Položka presunutá do " + currentRoomCode, Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Nie", null)
                        .show();
            } else {
                InventoryManager.checkItem(inputCode);
                adapter.notifyDataSetChanged();
                Toast.makeText(Activity1.this, "Položka skontrolovaná!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Toast.makeText(Activity1.this, "Skenovanie zrušené", Toast.LENGTH_SHORT).show();
                } else {
                    String scannedCode = result.getContents();
                    if (scannedCode.startsWith("R")) {
                        EditText roomCodeInput = findViewById(R.id.roomCodeInput);
                        roomCodeInput.setText(scannedCode);
                        confirmRoomButton.performClick();
                    } else if (scannedCode.startsWith("SN")) {
                        if (currentRoomCode == null || currentRoomCode.isEmpty()) {
                            Toast.makeText(Activity1.this, "Najskôr potvrďte izbu!", Toast.LENGTH_SHORT).show();
                            TextView errorText = findViewById(R.id.errorText);
                            errorText.setText("Najskôr potvrďte izbu!");
                            errorText.setVisibility(View.VISIBLE);
                        } else {
                            EditText inventoryInput = findViewById(R.id.inventoryInput);
                            inventoryInput.setText(scannedCode);
                            checkButton.performClick();
                        }
                    } else {
                        Toast.makeText(Activity1.this, "Neplatný kód QR - musí začínať na R (miestnosť) alebo SN (produkt)", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
}