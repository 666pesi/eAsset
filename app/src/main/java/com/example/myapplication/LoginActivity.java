package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private List<Map<String, String>> users = new ArrayList<>();
    private ImageView fetchUsersButton;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText usernameInput = findViewById(R.id.usernameInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        TextView errorText = findViewById(R.id.errorText);
        fetchUsersButton = findViewById(R.id.fetchUsersButton);

        fetchUsersButton.setOnClickListener(v -> fetchUsersFromApi());

        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString();
            String inputPassword = passwordInput.getText().toString();

            if (username.isEmpty() || inputPassword.isEmpty()) {
                errorText.setText("Please enter username and password.");
                errorText.setVisibility(View.VISIBLE);
                return;
            }

            boolean isValidUser = false;
            for (Map<String, String> user : users) {
                if (user.get("username").equals(username) && user.get("password").equals(inputPassword)) {
                    isValidUser = true;
                    break;
                }
            }

            if (isValidUser) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            } else {
                errorText.setText("Invalid username or password.");
                errorText.setVisibility(View.VISIBLE);
            }
        });
    }

    private void fetchUsersFromApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-production-b7ac.up.railway.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<Map<String, String>>> call = apiService.getUsers();

        call.enqueue(new Callback<List<Map<String, String>>>() {
            @Override
            public void onResponse(Call<List<Map<String, String>>> call, Response<List<Map<String, String>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    users = response.body();
                    Toast.makeText(LoginActivity.this, "Users loaded successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, String>>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}