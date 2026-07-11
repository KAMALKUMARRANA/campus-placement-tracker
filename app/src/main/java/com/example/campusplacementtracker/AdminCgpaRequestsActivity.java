package com.example.campusplacementtracker;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;

public class AdminCgpaRequestsActivity extends AppCompatActivity {

    ListView listView;
    Button btnBack;
    TextView tvNoReq;
    Database db;
    ArrayList<String[]> requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_cgpa_requests);

        listView = findViewById(R.id.listViewCgpaRequests);
        btnBack = findViewById(R.id.btnCgpaRequestBack);
        tvNoReq = findViewById(R.id.tvAdminNoCgpaReq);
        db = new Database(getApplicationContext());

        loadRequests();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] req = requests.get(position);
                String reqId = req[0];
                String user = req[1];
                String val = req[2];

                new AlertDialog.Builder(AdminCgpaRequestsActivity.this)
                        .setTitle("Approve CGPA Update")
                        .setMessage("Update CGPA for " + user + " to " + val + "?")
                        .setPositiveButton("Approve", (dialog, which) -> {
                            db.updateCgpa(user, val);
                            db.updateCgpaRequestStatus(reqId, "Approved");
                            Toast.makeText(getApplicationContext(), "CGPA updated", Toast.LENGTH_SHORT).show();
                            loadRequests();
                        })
                        .setNegativeButton("Reject", (dialog, which) -> {
                            db.updateCgpaRequestStatus(reqId, "Rejected");
                            Toast.makeText(getApplicationContext(), "Request rejected", Toast.LENGTH_SHORT).show();
                            loadRequests();
                        })
                        .setNeutralButton("Cancel", null)
                        .show();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadRequests() {
        requests = db.getAllCgpaRequests();
        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        for (String[] r : requests) {
            HashMap<String, String> item = new HashMap<>();
            item.put("user", "Student: " + r[1]);
            item.put("cgpa", "Requested CGPA: " + r[2]);
            list.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2,
                new String[]{"user", "cgpa"},
                new int[]{android.R.id.text1, android.R.id.text2});
        listView.setAdapter(adapter);

        if (list.isEmpty()) {
            tvNoReq.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            tvNoReq.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }
}
