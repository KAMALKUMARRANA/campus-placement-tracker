package com.example.campusplacementtracker;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;

public class CompanyViewApplicationsActivity extends AppCompatActivity {

    ListView listView;
    Button btnBack;
    TextView tvTitle, tvNoApps;
    String companyName;
    Database db;
    ArrayList<String> rawApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_view_applications);

        listView = findViewById(R.id.listViewCompanyApps);
        btnBack = findViewById(R.id.btnCompanyViewBack);
        tvTitle = findViewById(R.id.tvCompanyViewTitle);
        tvNoApps = findViewById(R.id.tvCompanyNoApps);

        companyName = getIntent().getStringExtra("companyName");
        tvTitle.setText("Candidates for " + companyName);

        db = new Database(getApplicationContext());
        loadApps();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String[] parts = rawApps.get(position).split("\\|");
            if (parts.length < 10) return;
            Intent it = new Intent(CompanyViewApplicationsActivity.this, ApplicationDetailsActivity.class);
            it.putExtra("company", parts[0]);
            it.putExtra("role", parts[1]);
            it.putExtra("pkg", parts[2]);
            it.putExtra("eligibility", parts[3]);
            it.putExtra("applieddate", parts[4]);
            it.putExtra("interviewdate", parts[5]);
            it.putExtra("interviewtime", parts[6]);
            it.putExtra("status", parts[7]);
            it.putExtra("rowid", parts[8]);
            it.putExtra("username", parts[9]);
            it.putExtra("isAdmin", true); // HR functions as admin for status updates
            startActivity(it);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadApps() {
        rawApps = db.getCompanyApplications(companyName);
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        for (String app : rawApps) {
            String[] p = app.split("\\|");
            if (p.length < 10) continue;
            
            String user = p[9];
            String[] profile = db.getProfile(user);
            
            HashMap<String, String> item = new HashMap<>();
            item.put("student", "Candidate: " + (profile[0].isEmpty() ? user : profile[0]));
            item.put("role", p[1] + " (" + p[2] + ")");
            item.put("status", "Status: " + p[7]);
            item.put("quick_info", "CGPA: " + profile[3] + " | Skills: " + (profile[6].isEmpty() ? "Not Set" : profile[6]));
            list.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.admin_application_row,
                new String[]{"student", "role", "status", "quick_info"},
                new int[]{R.id.rowAdminStudent, R.id.rowAdminRole, R.id.rowAdminStatus, R.id.rowAdminQuickInfo});
        listView.setAdapter(adapter);

        if (list.isEmpty()) {
            tvNoApps.setVisibility(android.view.View.VISIBLE);
            listView.setVisibility(android.view.View.GONE);
        } else {
            tvNoApps.setVisibility(android.view.View.GONE);
            listView.setVisibility(android.view.View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadApps();
    }
}
