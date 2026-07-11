package com.example.campusplacementtracker;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;

public class ApplyActivity extends AppCompatActivity {

    TextView tvCompany, tvRole, tvPackage, tvEligibility, tvTechStack;
    Spinner spinnerSlots;
    Button btnSubmit, btnBack;

    String company, role, pkg, eligibility, username, minCgpa, techStack;
    ArrayList<String[]> availableSlots;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply);

        tvCompany = findViewById(R.id.tvApplyCompany);
        tvRole = findViewById(R.id.tvApplyRole);
        tvPackage = findViewById(R.id.tvApplyPackage);
        tvEligibility = findViewById(R.id.tvApplyEligibility);
        tvTechStack = findViewById(R.id.tvApplyTechStack); // NEED TO ADD TO XML
        spinnerSlots = findViewById(R.id.spinnerAvailableSlots);
        btnSubmit = findViewById(R.id.btnSubmitApplication);
        btnBack = findViewById(R.id.btnApplyBack);

        db = new Database(getApplicationContext());

        SharedPreferences sp = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        username = sp.getString("username", "");

        Intent it = getIntent();
        company = it.getStringExtra("company");
        role = it.getStringExtra("role");
        pkg = it.getStringExtra("pkg");
        eligibility = it.getStringExtra("eligibility");
        techStack = it.getStringExtra("tech_stack");
        minCgpa = it.getStringExtra("min_cgpa");

        tvCompany.setText(company);
        tvRole.setText("Role: " + role);
        tvPackage.setText("Package: " + pkg);
        tvEligibility.setText("Eligibility: " + eligibility);
        tvTechStack.setText("Required Tech: " + techStack);

        loadAvailableSlots();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validation: Check Student CGPA
                String[] studentProfile = db.getProfile(username);
                String studentCgpaStr = studentProfile[3];
                String studentBranch = studentProfile[2].toLowerCase();
                
                if (studentCgpaStr.isEmpty() || studentCgpaStr.equals("0")) {
                    Toast.makeText(getApplicationContext(), "Your CGPA is not updated. Please request update in Profile.", Toast.LENGTH_LONG).show();
                    return;
                }

                // Check Degree/Branch match (simple keyword match in eligibility)
                String eligLower = eligibility.toLowerCase();
                boolean branchEligible = false;
                if (eligLower.contains("any") || eligLower.contains("all branches") || eligLower.contains("any stream")) {
                    branchEligible = true;
                } else if (!studentBranch.isEmpty() && eligLower.contains(studentBranch)) {
                    branchEligible = true;
                }

                if (!branchEligible && !studentBranch.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Your branch (" + studentProfile[2] + ") is not eligible for this role. Role requires: " + eligibility, Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    double studentCgpa = Double.parseDouble(studentCgpaStr);
                    double requiredCgpa = Double.parseDouble(minCgpa);
                    if (studentCgpa < requiredCgpa) {
                        Toast.makeText(getApplicationContext(), "You do not meet the minimum CGPA eligibility (" + minCgpa + ")", Toast.LENGTH_LONG).show();
                        return;
                    }
                } catch (Exception e) {
                    // Ignore parsing errors if minCgpa is non-numeric, but log it
                }

                if (spinnerSlots.getSelectedItem() == null) {
                    Toast.makeText(getApplicationContext(), "No available slots for this company", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (db.alreadyApplied(username, company)) {
                    Toast.makeText(getApplicationContext(), "You have already applied to this company", Toast.LENGTH_LONG).show();
                    return;
                }

                int position = spinnerSlots.getSelectedItemPosition();
                String[] slot = availableSlots.get(position);
                String slotId = slot[0];
                String date = slot[1];
                String time = slot[2];

                Calendar now = Calendar.getInstance();
                String appliedDate = now.get(Calendar.DAY_OF_MONTH) + "/" + (now.get(Calendar.MONTH) + 1) + "/" + now.get(Calendar.YEAR);

                new AlertDialog.Builder(ApplyActivity.this)
                    .setTitle("Confirm Application")
                    .setMessage("Apply for " + role + " at " + company + " for the slot on " + date + " at " + time + "?")
                    .setPositiveButton("Yes, Apply", (dialog, which) -> {
                        db.addApplication(username, company, role, pkg, eligibility, appliedDate, date, time, "Applied");
                        db.bookSlot(slotId);

                        Toast.makeText(getApplicationContext(), "Application submitted successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ApplyActivity.this, MyApplicationsActivity.class));
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            }
        });

        btnBack.setOnClickListener(view -> finish());
    }

    private void loadAvailableSlots() {
        availableSlots = db.getAvailableSlots(company);
        ArrayList<String> slotStrings = new ArrayList<>();
        for (String[] slot : availableSlots) {
            slotStrings.add(slot[1] + " at " + slot[2]);
        }
        
        if (slotStrings.isEmpty()) {
            slotStrings.add("No slots available");
            spinnerSlots.setEnabled(false);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, slotStrings);
        spinnerSlots.setAdapter(adapter);
    }
}
