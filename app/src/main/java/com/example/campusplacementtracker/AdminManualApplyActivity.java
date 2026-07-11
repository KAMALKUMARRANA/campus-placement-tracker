package com.example.campusplacementtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;

public class AdminManualApplyActivity extends AppCompatActivity {

    TextView tvTitle;
    Spinner spinnerComp, spinnerSlot;
    Button btnApply, btnBack;
    String username;
    Database db;
    ArrayList<String[]> companies;
    ArrayList<String[]> slots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manual_apply);

        tvTitle = findViewById(R.id.tvAdminManualApplyTitle);
        spinnerComp = findViewById(R.id.spinnerManualComp);
        spinnerSlot = findViewById(R.id.spinnerManualSlot);
        btnApply = findViewById(R.id.btnAdminDoManualApply);
        btnBack = findViewById(R.id.btnAdminManualBack);

        username = getIntent().getStringExtra("username");
        tvTitle.setText("Register: " + username);

        db = new Database(getApplicationContext());

        loadCompanies();

        spinnerComp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadSlots(companies.get(position)[0]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnApply.setOnClickListener(v -> {
            if (spinnerComp.getSelectedItem() == null || spinnerSlot.getSelectedItem() == null) {
                Toast.makeText(getApplicationContext(), "Please select company and available slot", Toast.LENGTH_SHORT).show();
                return;
            }
            
            String[] comp = companies.get(spinnerComp.getSelectedItemPosition());
            String[] slot = slots.get(spinnerSlot.getSelectedItemPosition());

            if (db.alreadyApplied(username, comp[0])) {
                Toast.makeText(getApplicationContext(), "Student already applied to this company", Toast.LENGTH_SHORT).show();
                return;
            }

            Calendar now = Calendar.getInstance();
            String appliedDate = now.get(Calendar.DAY_OF_MONTH) + "/" + (now.get(Calendar.MONTH) + 1) + "/" + now.get(Calendar.YEAR);

            new AlertDialog.Builder(AdminManualApplyActivity.this)
                .setTitle("Confirm Registration")
                .setMessage("Register " + username + " for " + comp[0] + " interview on " + slot[1] + "?")
                .setPositiveButton("Register", (dialog, which) -> {
                    db.addApplication(username, comp[0], comp[1], comp[2], comp[3], appliedDate, slot[1], slot[2], "Applied");
                    db.bookSlot(slot[0]);

                    Toast.makeText(getApplicationContext(), "Manual registration successful", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadCompanies() {
        companies = db.getCompanies();
        ArrayList<String> names = new ArrayList<>();
        for (String[] c : companies) names.add(c[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, names);
        spinnerComp.setAdapter(adapter);
    }

    private void loadSlots(String company) {
        slots = db.getAvailableSlots(company);
        ArrayList<String> slotStrings = new ArrayList<>();
        for (String[] s : slots) slotStrings.add(s[1] + " at " + s[2]);
        
        if (slotStrings.isEmpty()) {
            slotStrings.add("No slots available");
            spinnerSlot.setEnabled(false);
        } else {
            spinnerSlot.setEnabled(true);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, slotStrings);
        spinnerSlot.setAdapter(adapter);
    }
}
