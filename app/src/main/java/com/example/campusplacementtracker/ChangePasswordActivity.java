package com.example.campusplacementtracker;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText edNew, edConfirm;
    Button btnUpdate, btnBack;
    Database db;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        edNew = findViewById(R.id.edNewPassword);
        edConfirm = findViewById(R.id.edConfirmNewPassword);
        btnUpdate = findViewById(R.id.btnUpdatePassword);
        btnBack = findViewById(R.id.btnPassBack);

        db = new Database(getApplicationContext());
        SharedPreferences sp = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        username = sp.getString("username", "");

        btnUpdate.setOnClickListener(v -> {
            String pass = edNew.getText().toString();
            String conf = edConfirm.getText().toString();

            if (pass.isEmpty() || conf.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(conf)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!ValidationHelper.isValidPassword(pass)) {
                Toast.makeText(this, "Password must be 6+ chars with 1 digit and 1 special char", Toast.LENGTH_LONG).show();
                return;
            }

            db.updatePassword(username, pass);
            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnBack.setOnClickListener(v -> finish());
    }
}
