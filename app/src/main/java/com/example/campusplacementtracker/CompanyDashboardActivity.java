package com.example.campusplacementtracker;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

public class CompanyDashboardActivity extends AppCompatActivity {

    TextView tvWelcome, tvTotalApps;
    Button btnViewApps, btnManageSlots, btnViewSlots, btnLogout, btnChangePass;
    String companyName, displayName;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_dashboard);

        tvWelcome = findViewById(R.id.tvCompanyWelcome);
        tvTotalApps = findViewById(R.id.tvCompanyTotalApps);
        btnViewApps = findViewById(R.id.btnCompanyViewApps);
        btnManageSlots = findViewById(R.id.btnCompanyManageSlots);
        btnViewSlots = findViewById(R.id.btnCompanyViewSlots);
        btnChangePass = findViewById(R.id.btnCompanyChangePass);
        btnLogout = findViewById(R.id.btnCompanyLogout);

        db = new Database(getApplicationContext());
        SharedPreferences sp = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        companyName = sp.getString("username", ""); // HR username acts as company link
        
        // Use fullname as company display name if linked
        String[] profile = db.getProfile(companyName);
        displayName = profile[0].isEmpty() ? companyName : profile[0];
        tvWelcome.setText(displayName + " Dashboard");

        loadStats(displayName);

        btnViewApps.setOnClickListener(v -> {
            Intent it = new Intent(CompanyDashboardActivity.this, CompanyViewApplicationsActivity.class);
            it.putExtra("companyName", displayName);
            startActivity(it);
        });

        btnManageSlots.setOnClickListener(v -> {
            Intent it = new Intent(CompanyDashboardActivity.this, AdminManageSlotsActivity.class);
            it.putExtra("fixedCompany", displayName); // Pass company to fix the spinner
            startActivity(it);
        });

        btnViewSlots.setOnClickListener(v -> {
            Intent it = new Intent(CompanyDashboardActivity.this, ViewSlotsActivity.class);
            it.putExtra("companyName", displayName);
            startActivity(it);
        });

        btnChangePass.setOnClickListener(v -> startActivity(new Intent(CompanyDashboardActivity.this, ChangePasswordActivity.class)));

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(CompanyDashboardActivity.this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.clear();
                    editor.apply();
                    startActivity(new Intent(CompanyDashboardActivity.this, MainActivity.class));
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
        });
    }

    private void loadStats(String name) {
        ArrayList<String> apps = db.getCompanyApplications(name);
        int pending = 0;
        for (String app : apps) {
            if (app.contains("|Applied|")) pending++;
        }
        tvTotalApps.setText(String.valueOf(pending));
    }

    @Override
    protected void onResume() {
        super.onResume();
        String[] profile = db.getProfile(companyName);
        loadStats(profile[0].isEmpty() ? companyName : profile[0]);
    }
}
