package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.journeyapps.barcodescanner.ScanOptions;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import androidx.activity.result.ActivityResultLauncher;

public class Activity3 extends AppCompatActivity {
    private EditText productCodeInput;
    private Button scanButton;
    private Button checkButton;
    private TextView productInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);

        productCodeInput = findViewById(R.id.productCodeInput);
        scanButton = findViewById(R.id.scanButton);
        checkButton = findViewById(R.id.checkButton);
        productInfoText = findViewById(R.id.productInfoText);

        // Initialize the InventoryManager
        InventoryManager.initialize(this);

        // Handle Scan Button Click
        scanButton.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setPrompt("Scan a QR code");
            options.setBeepEnabled(true);
            options.setOrientationLocked(true);
            options.setCaptureActivity(CaptureActivity.class);
            barcodeLauncher.launch(options);
        });

        // Handle Check Button Click
        checkButton.setOnClickListener(v -> {
            String productCode = productCodeInput.getText().toString().trim();
            if (productCode.isEmpty()) {
                Toast.makeText(this, "Zadajte alebo naskenujte kód produktu.", Toast.LENGTH_SHORT).show();
                return;
            }
            displayProductInfo(productCode);
        });
    }

    private void displayProductInfo(String productCode) {
        Item item = InventoryManager.getItemByCode(productCode);
        if (item == null) {
            productInfoText.setText("Produkt nebol nájdený.");
            return;
        }

        String productInfo = "Názov produktu: " + item.getName() + "\n" +
                "Seriové čislo: " + item.getCode() + "\n" +
                "Miestnosť: " + item.getRoom();
        productInfoText.setText(productInfo);
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Toast.makeText(Activity3.this, "Skenovanie zrušené", Toast.LENGTH_SHORT).show();
                } else {
                    String scannedCode = result.getContents();
                    if (scannedCode.startsWith("SN")) {
                        productCodeInput.setText(scannedCode);
                        displayProductInfo(scannedCode);
                    } else {
                        Toast.makeText(Activity3.this, "Nesprávny kód produktu - musí začínať na SN", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
}