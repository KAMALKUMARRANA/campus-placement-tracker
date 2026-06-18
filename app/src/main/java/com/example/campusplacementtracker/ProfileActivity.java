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

public class ProfileActivity extends AppCompatActivity {

    TextView tvWelcome;
    EditText edFullName, edRollNo, edBranch, edCgpa, edEmail;
    Button btnSaveProfile, btnGoCompanies, btnGoApplications, btnLogout;
    String username;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvWelcome = findViewById(R.id.tvWelcome);
        edFullName = findViewById(R.id.edFullName);
        edRollNo = findViewById(R.id.edRollNo);
        edBranch = findViewById(R.id.edBranch);
        edCgpa = findViewById(R.id.edCgpa);
        edEmail = findViewById(R.id.edEmail);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnGoCompanies = findViewById(R.id.btnGoCompanies);
        btnGoApplications = findViewById(R.id.btnGoApplications);
        btnLogout = findViewById(R.id.btnLogout);

        // Get the currently logged-in username saved during login
        SharedPreferences sp = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        username = sp.getString("username", "");
        tvWelcome.setText("Welcome, " + username);

        db = new Database(getApplicationContext());

        // Load any previously saved profile details into the form
        String[] profile = db.getProfile(username);
        edFullName.setText(profile[0]);
        edRollNo.setText(profile[1]);
        edBranch.setText(profile[2]);
        edCgpa.setText(profile[3]);
        edEmail.setText(profile[4]);

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullname = edFullName.getText().toString();
                String rollno = edRollNo.getText().toString();
                String branch = edBranch.getText().toString();
                String cgpa = edCgpa.getText().toString();

                if (fullname.length() == 0 || rollno.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter at least Name and Roll Number", Toast.LENGTH_SHORT).show();
                    return;
                }

                db.updateProfile(username, fullname, rollno, branch, cgpa);
                Toast.makeText(getApplicationContext(), "Profile saved", Toast.LENGTH_SHORT).show();
            }
        });

        btnGoCompanies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, CompanyListActivity.class));
            }
        });

        btnGoApplications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, MyApplicationsActivity.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.apply();
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}