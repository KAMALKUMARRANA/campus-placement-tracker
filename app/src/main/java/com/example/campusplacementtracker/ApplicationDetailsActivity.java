package com.example.campusplacementtracker;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ApplicationDetailsActivity extends AppCompatActivity {

    TextView tvCompany, tvRole, tvPackage, tvEligibility, tvApplied, tvInterview;
    Spinner spinnerStatus;
    Button btnUpdate, btnDelete, btnBack;

    String rowId, currentStatus;
    Database db;

    String[] statusOptions = {"Applied", "Interview Scheduled", "Selected", "Rejected"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_details);

        tvCompany = findViewById(R.id.tvDetailCompany);
        tvRole = findViewById(R.id.tvDetailRole);
        tvPackage = findViewById(R.id.tvDetailPackage);
        tvEligibility = findViewById(R.id.tvDetailEligibility);
        tvApplied = findViewById(R.id.tvDetailApplied);
        tvInterview = findViewById(R.id.tvDetailInterview);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnUpdate = findViewById(R.id.btnUpdateStatus);
        btnDelete = findViewById(R.id.btnDeleteApplication);
        btnBack = findViewById(R.id.btnDetailsBack);

        db = new Database(getApplicationContext());

        Intent it = getIntent();
        String company = it.getStringExtra("company");
        String role = it.getStringExtra("role");
        String pkg = it.getStringExtra("pkg");
        String eligibility = it.getStringExtra("eligibility");
        String appliedDate = it.getStringExtra("applieddate");
        String interviewDate = it.getStringExtra("interviewdate");
        String interviewTime = it.getStringExtra("interviewtime");
        currentStatus = it.getStringExtra("status");
        rowId = it.getStringExtra("rowid");

        tvCompany.setText(company);
        tvRole.setText("Role: " + role);
        tvPackage.setText("Package: " + pkg);
        tvEligibility.setText("Eligibility: " + eligibility);
        tvApplied.setText("Applied on: " + appliedDate);
        tvInterview.setText("Interview: " + interviewDate + " at " + interviewTime);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statusOptions);
        spinnerStatus.setAdapter(adapter);

        for (int i = 0; i < statusOptions.length; i++) {
            if (statusOptions[i].equals(currentStatus)) {
                spinnerStatus.setSelection(i);
                break;
            }
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newStatus = spinnerStatus.getSelectedItem().toString();
                db.updateStatus(rowId, newStatus);
                Toast.makeText(getApplicationContext(), "Status updated to: " + newStatus, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ApplicationDetailsActivity.this)
                        .setTitle("Delete Application")
                        .setMessage("Are you sure you want to delete this application to " + company + "?")
                        .setPositiveButton("Delete", new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                db.deleteApplication(rowId);
                                Toast.makeText(getApplicationContext(), "Application deleted", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}