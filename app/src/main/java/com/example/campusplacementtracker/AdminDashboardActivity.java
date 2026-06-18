package com.example.campusplacementtracker;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

public class AdminDashboardActivity extends AppCompatActivity {

    TextView tvTotalApps, tvSelectedApps;
    Button btnManageApplications, btnLogout;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        tvTotalApps = findViewById(R.id.tvTotalApps);
        tvSelectedApps = findViewById(R.id.tvSelectedApps);
        btnManageApplications = findViewById(R.id.btnManageApplications);
        btnLogout = findViewById(R.id.btnAdminLogout);

        db = new Database(getApplicationContext());

        loadStats();

        btnManageApplications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(AdminDashboardActivity.this, AdminManageApplicationsActivity.class);
                startActivity(it);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.apply();
                startActivity(new Intent(AdminDashboardActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void loadStats() {
        ArrayList<String> apps = db.getAllApplications();
        int total = apps.size();
        int selectedCount = 0;
        for (String app : apps) {
            if (app.contains("|Selected|")) {
                selectedCount++;
            }
        }
        tvTotalApps.setText(String.valueOf(total));
        tvSelectedApps.setText(String.valueOf(selectedCount));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStats();
    }
}
