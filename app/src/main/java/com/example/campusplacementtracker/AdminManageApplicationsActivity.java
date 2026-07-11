package com.example.campusplacementtracker;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;

public class AdminManageApplicationsActivity extends AppCompatActivity {

    ListView listView;
    Button btnBack;
    EditText edSearch;
    TextView tvNoApps;
    Database db;
    ArrayList<String> rawApplications;
    ArrayList<Integer> filteredIndices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_applications);

        listView = findViewById(R.id.listViewAllApplications);
        btnBack = findViewById(R.id.btnAdminBack);
        edSearch = findViewById(R.id.edAdminSearchApps);
        tvNoApps = findViewById(R.id.tvAdminNoApps);

        db = new Database(getApplicationContext());
        loadAllApplications("");

        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadAllApplications(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadAllApplications(String query) {
        rawApplications = db.getAllApplications();
        filteredIndices.clear();
        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        String q = query.toLowerCase().trim();

        for (int i = 0; i < rawApplications.size(); i++) {
            String[] parts = rawApplications.get(i).split("\\|");
            if (parts.length < 10) continue;

            String user = parts[9];
            String company = parts[0];
            
            if (q.isEmpty() || user.toLowerCase().contains(q) || company.toLowerCase().contains(q)) {
                String[] profile = db.getProfile(user);
                
                HashMap<String, String> item = new HashMap<>();
                item.put("student", "Student: " + (profile[0].isEmpty() ? user : profile[0]));
                item.put("company", company);
                item.put("role", "Role: " + parts[1] + " | " + parts[2]);
                item.put("status", "Status: " + parts[7]);
                item.put("quick_info", "CGPA: " + profile[3] + " | Branch: " + profile[2]);
                list.add(item);
                filteredIndices.add(i);
            }
        }

        if (list.isEmpty()) {
            tvNoApps.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            tvNoApps.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.admin_application_row,
                new String[]{"student", "company", "role", "status", "quick_info"},
                new int[]{R.id.rowAdminStudent, R.id.rowAdminCompany, R.id.rowAdminRole, R.id.rowAdminStatus, R.id.rowAdminQuickInfo});
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            int realIndex = filteredIndices.get(position);
            String[] parts = rawApplications.get(realIndex).split("\\|");
            if (parts.length < 10) return;
            Intent it = new Intent(AdminManageApplicationsActivity.this, ApplicationDetailsActivity.class);
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
            it.putExtra("isAdmin", true);
            startActivity(it);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllApplications(edSearch.getText().toString());
    }
}
