package com.example.campusplacementtracker;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

public class AdminDashboardActivity extends AppCompatActivity {

    TextView tvTotalApps, tvSelectedApps, tvTotalUsers, tvTotalCompanies;
    Button btnManageApplications, btnManageUsers, btnAddCompany, btnManageSlots, btnViewSlots, btnLogout, btnAdminCreateUser, btnAdminViewCompanies, btnCgpaRequests, btnChangePass;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        tvTotalApps = findViewById(R.id.tvTotalApps);
        tvSelectedApps = findViewById(R.id.tvSelectedApps);
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvTotalCompanies = findViewById(R.id.tvTotalCompanies);

        btnManageApplications = findViewById(R.id.btnManageApplications);
        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnAddCompany = findViewById(R.id.btnAddCompany);
        btnManageSlots = findViewById(R.id.btnManageSlots);
        btnViewSlots = findViewById(R.id.btnAdminViewSlots);
        btnCgpaRequests = findViewById(R.id.btnAdminCgpaRequests);
        btnAdminCreateUser = findViewById(R.id.btnAdminCreateUser);
        btnAdminViewCompanies = findViewById(R.id.btnAdminViewCompanies);
        btnChangePass = findViewById(R.id.btnAdminChangePass);
        btnLogout = findViewById(R.id.btnAdminLogout);

        db = new Database(getApplicationContext());

        loadStats();

        btnManageApplications.setOnClickListener(v -> startActivity(new Intent(AdminDashboardActivity.this, AdminManageApplicationsActivity.class)));
        btnManageUsers.setOnClickListener(v -> startActivity(new Intent(AdminDashboardActivity.this, AdminUserListActivity.class)));
        btnAdminCreateUser.setOnClickListener(v -> startActivity(new Intent(AdminDashboardActivity.this, AdminCreateUserActivity.class)));
        btnAdminViewCompanies.setOnClickListener(v -> startActivity(new Intent(AdminDashboardActivity.this, AdminCompanyListActivity.class)));
        btnAddCompany.setOnClickListener(v -> startActivity(new Intent(AdminDashboardActivity.this, AdminAddCompanyActivity.class)));
        btnManageSlots.setOnClickListener(v -> startActivity(new Intent(AdminDashboardActivity.this, AdminManageSlotsActivity.class)));
        btnViewSlots.setOnClickListener(v -> startActivity(new Intent(AdminDashboardActivity.this, ViewSlotsActivity.class)));
        btnCgpaRequests.setOnClickListener(v -> startActivity(new Intent(AdminDashboardActivity.this, AdminCgpaRequestsActivity.class)));
        btnChangePass.setOnClickListener(v -> startActivity(new Intent(AdminDashboardActivity.this, ChangePasswordActivity.class)));

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(AdminDashboardActivity.this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        SharedPreferences sp = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.clear();
                        editor.apply();
                        startActivity(new Intent(AdminDashboardActivity.this, MainActivity.class));
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
            }
        });
    }

    private void loadStats() {
        ArrayList<String> apps = db.getAllApplications();
        int totalApps = apps.size();
        int selectedCount = 0;
        for (String app : apps) {
            if (app.contains("|Selected|")) {
                selectedCount++;
            }
        }
        
        int totalStudents = db.getAllUsers().size();
        int totalComps = db.getCompanies().size();

        tvTotalApps.setText(String.valueOf(totalApps));
        tvSelectedApps.setText(String.valueOf(selectedCount));
        tvTotalUsers.setText(String.valueOf(totalStudents));
        tvTotalCompanies.setText(String.valueOf(totalComps));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStats();
    }
}
