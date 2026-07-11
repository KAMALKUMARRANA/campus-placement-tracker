package com.example.campusplacementtracker;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;

public class AdminManageSlotsActivity extends AppCompatActivity {

    Spinner spinnerCompany;
    Button btnDate, btnTime, btnAdd, btnBack;
    Database db;
    String selectedDate = "", selectedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_slots);

        spinnerCompany = findViewById(R.id.spinnerSlotCompany);
        btnDate = findViewById(R.id.btnSlotDate);
        btnTime = findViewById(R.id.btnSlotTime);
        btnAdd = findViewById(R.id.btnAddSlot);
        btnBack = findViewById(R.id.btnSlotBack);

        db = new Database(getApplicationContext());

        String fixedCompany = getIntent().getStringExtra("fixedCompany");

        // Load companies into spinner
        ArrayList<String[]> companies = db.getCompanies();
        ArrayList<String> companyNames = new ArrayList<>();
        int selectionIndex = -1;

        for (int i = 0; i < companies.size(); i++) {
            String name = companies.get(i)[0];
            companyNames.add(name);
            if (fixedCompany != null && fixedCompany.equals(name)) {
                selectionIndex = i;
            }
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, companyNames);
        spinnerCompany.setAdapter(adapter);

        if (fixedCompany != null && selectionIndex != -1) {
            spinnerCompany.setSelection(selectionIndex);
            spinnerCompany.setEnabled(false); // HR can only add for their own company
        }

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(AdminManageSlotsActivity.this,
                        (view1, year, month, dayOfMonth) -> {
                            selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                            btnDate.setText(selectedDate);
                        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                TimePickerDialog dialog = new TimePickerDialog(AdminManageSlotsActivity.this,
                        (view1, hourOfDay, minute) -> {
                            selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                            btnTime.setText(selectedTime);
                        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
                dialog.show();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String company = spinnerCompany.getSelectedItem() != null ? spinnerCompany.getSelectedItem().toString() : "";
                if (company.isEmpty() || selectedDate.isEmpty() || selectedTime.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please select company, date and time", Toast.LENGTH_SHORT).show();
                    return;
                }

                new AlertDialog.Builder(AdminManageSlotsActivity.this)
                    .setTitle("Confirm Add Slot")
                    .setMessage("Add interview slot for " + company + " on " + selectedDate + " at " + selectedTime + "?")
                    .setPositiveButton("Add", (dialog, which) -> {
                        db.addSlot(company, selectedDate, selectedTime);
                        Toast.makeText(getApplicationContext(), "Slot added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            }
        });

        btnBack.setOnClickListener(view -> finish());
    }
}
