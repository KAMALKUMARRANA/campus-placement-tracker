package com.example.campusplacementtracker;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;

public class ApplyActivity extends AppCompatActivity {

    TextView tvCompany, tvRole, tvPackage, tvEligibility;
    Button btnPickDate, btnPickTime, btnSubmit, btnBack;

    String company, role, pkg, eligibility, username;
    String selectedDate = "", selectedTime = "";

    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply);

        tvCompany = findViewById(R.id.tvApplyCompany);
        tvRole = findViewById(R.id.tvApplyRole);
        tvPackage = findViewById(R.id.tvApplyPackage);
        tvEligibility = findViewById(R.id.tvApplyEligibility);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnPickTime = findViewById(R.id.btnPickTime);
        btnSubmit = findViewById(R.id.btnSubmitApplication);
        btnBack = findViewById(R.id.btnApplyBack);

        db = new Database(getApplicationContext());

        SharedPreferences sp = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        username = sp.getString("username", "");

        // Get company details passed from CompanyListActivity
        Intent it = getIntent();
        company = it.getStringExtra("company");
        role = it.getStringExtra("role");
        pkg = it.getStringExtra("pkg");
        eligibility = it.getStringExtra("eligibility");

        tvCompany.setText(company);
        tvRole.setText("Role: " + role);
        tvPackage.setText("Package: " + pkg);
        tvEligibility.setText("Eligibility: " + eligibility);

        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(ApplyActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                month = month + 1; // Calendar months are 0-indexed
                                selectedDate = dayOfMonth + "/" + month + "/" + year;
                                btnPickDate.setText(selectedDate);
                            }
                        },
                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                dialog.show();
            }
        });

        btnPickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                TimePickerDialog dialog = new TimePickerDialog(ApplyActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                                btnPickTime.setText(selectedTime);
                            }
                        },
                        cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
                dialog.show();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedDate.length() == 0 || selectedTime.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please select interview date and time", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (db.alreadyApplied(username, company)) {
                    Toast.makeText(getApplicationContext(), "You have already applied to this company", Toast.LENGTH_LONG).show();
                    return;
                }

                Calendar now = Calendar.getInstance();
                String appliedDate = now.get(Calendar.DAY_OF_MONTH) + "/" + (now.get(Calendar.MONTH) + 1) + "/" + now.get(Calendar.YEAR);

                db.addApplication(username, company, role, pkg, eligibility, appliedDate, selectedDate, selectedTime, "Applied");

                Toast.makeText(getApplicationContext(), "Application submitted successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ApplyActivity.this, MyApplicationsActivity.class));
                finish();
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