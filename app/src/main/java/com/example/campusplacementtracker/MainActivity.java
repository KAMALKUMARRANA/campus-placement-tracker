package com.example.campusplacementtracker;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText edUsername, edPassword;
    Button btnLogin, btnThemeToggle;
    TextView tvGoRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edUsername = findViewById(R.id.edUsername);
        edPassword = findViewById(R.id.edPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoRegister = findViewById(R.id.tvGoRegister);
        btnThemeToggle = findViewById(R.id.btnThemeToggle);

        Database db = new Database(getApplicationContext());

        updateToggleButtonText();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edUsername.getText().toString();
                String password = edPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    String role = db.login(username, password);
                    if (!role.isEmpty()) {
                        SharedPreferences sp = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("username", username);
                        editor.putString("role", role);
                        editor.apply();

                        Toast.makeText(getApplicationContext(), "Login successful as " + role, Toast.LENGTH_SHORT).show();
                        
                        if (role.equals("admin")) {
                            startActivity(new Intent(MainActivity.this, AdminDashboardActivity.class));
                        } else {
                            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        }
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        tvGoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        btnThemeToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThemeHelper.toggleTheme(MainActivity.this);
                recreate();
            }
        });
    }

    private void updateToggleButtonText() {
        if (ThemeHelper.isDarkMode(this)) {
            btnThemeToggle.setText("☀ Switch to Light Mode");
        } else {
            btnThemeToggle.setText("🌙 Switch to Dark Mode");
        }
    }
}