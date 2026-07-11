package com.example.campusplacementtracker;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;

public class MyApplicationsActivity extends AppCompatActivity {

    ListView listView;
    TextView tvNoApplications;
    Button btnBack;
    String username;
    Database db;

    ArrayList<String> rawApplications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_applications);

        listView = findViewById(R.id.listViewApplications);
        tvNoApplications = findViewById(R.id.tvNoApplications);
        btnBack = findViewById(R.id.btnAppsBack);

        SharedPreferences sp = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        username = sp.getString("username", "");

        db = new Database(getApplicationContext());
        loadApplications();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loadApplications() {
        rawApplications = db.getApplications(username);

        if (rawApplications.size() == 0) {
            tvNoApplications.setVisibility(View.VISIBLE);
            listView.setAdapter(null);
            return;
        } else {
            tvNoApplications.setVisibility(View.GONE);
        }

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        for (int i = 0; i < rawApplications.size(); i++) {
            String[] parts = rawApplications.get(i).split("\\|");
            if (parts.length < 8) continue;

            HashMap<String, String> item = new HashMap<>();
            item.put("company", parts[0]);
            item.put("role", "Role: " + parts[1] + "  |  Package: " + parts[2]);
            item.put("interview", "Interview: " + parts[5] + " at " + parts[6]);
            item.put("status", statusLabel(parts[7]));
            list.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.application_row,
                new String[]{"company", "role", "interview", "status"},
                new int[]{R.id.rowAppCompany, R.id.rowAppRole, R.id.rowAppInterview, R.id.rowAppStatus});
        listView.setAdapter(adapter);

        listView.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < listView.getChildCount(); i++) {
                    View row = listView.getChildAt(i);
                    if (row != null) {
                        TextView statusView = row.findViewById(R.id.rowAppStatus);
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
                Intent it = new Intent(MyApplicationsActivity.this, ApplicationDetailsActivity.class);
                it.putExtra("company", parts[0]);
                it.putExtra("role", parts[1]);
                it.putExtra("pkg", parts[2]);
                it.putExtra("eligibility", parts[3]);
                it.putExtra("applieddate", parts[4]);
                it.putExtra("interviewdate", parts[5]);
                it.putExtra("interviewtime", parts[6]);
                it.putExtra("status", parts[7]);
                it.putExtra("rowid", parts[8]);
                startActivity(it);
            }
        });
    }

    private String statusLabel(String status) {
        return "Status: " + status;
    }

    private void applyStatusColor(TextView view, String text) {
        if (text.contains("Selected")) {
            view.setTextColor(getResources().getColor(R.color.accentGreen));
        } else if (text.contains("Rejected")) {
            view.setTextColor(0xFFD32F2F); // Red
        } else if (text.contains("Round") || text.contains("Scheduled")) {
            view.setTextColor(0xFFEF6C00); // Orange
        } else {
            view.setTextColor(getResources().getColor(R.color.primaryBlue));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadApplications();
    }
}