package com.example.campusplacementtracker;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;

public class AdminManageApplicationsActivity extends AppCompatActivity {

    ListView listView;
    Button btnBack;
    Database db;
    ArrayList<String> rawApplications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_applications);

        listView = findViewById(R.id.listViewAllApplications);
        btnBack = findViewById(R.id.btnAdminBack);

        db = new Database(getApplicationContext());
        loadAllApplications();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loadAllApplications() {
        rawApplications = db.getAllApplications();

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        for (int i = 0; i < rawApplications.size(); i++) {
            String[] parts = rawApplications.get(i).split("\\|");
            if (parts.length < 10) continue;

            HashMap<String, String> item = new HashMap<>();
            item.put("student", "Student: " + parts[9]);
            item.put("company", parts[0]);
            item.put("role", "Role: " + parts[1] + " | " + parts[2]);
            item.put("status", "Status: " + parts[7]);
            list.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.admin_application_row,
                new String[]{"student", "company", "role", "status"},
                new int[]{R.id.rowAdminStudent, R.id.rowAdminCompany, R.id.rowAdminRole, R.id.rowAdminStatus});
        listView.setAdapter(adapter);

        listView.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < listView.getChildCount(); i++) {
                    View row = listView.getChildAt(i);
                    if (row != null) {
                        TextView statusView = row.findViewById(R.id.rowAdminStatus);
                        if (statusView != null) {
                            applyStatusColor(statusView, statusView.getText().toString());
                        }
                    }
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String[] parts = rawApplications.get(position).split("\\|");
                if (parts.length < 9) return;
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
                it.putExtra("isAdmin", true);
                startActivity(it);
            }
        });
    }

    private void applyStatusColor(TextView view, String text) {
        if (text.contains("Selected")) {
            view.setTextColor(0xFF2E7D32); // green
        } else if (text.contains("Rejected")) {
            view.setTextColor(0xFFC62828); // red
        } else if (text.contains("Interview Scheduled")) {
            view.setTextColor(0xFFEF6C00); // orange
        } else {
            view.setTextColor(0xFF1565C0); // blue (Applied)
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllApplications();
    }
}
