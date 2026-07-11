package com.example.campusplacementtracker;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;

public class AdminCompanyListActivity extends AppCompatActivity {

    ListView listView;
    Button btnBack;
    Database db;
    ArrayList<String[]> companyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_company_list);

        listView = findViewById(R.id.listViewAdminCompanies);
        btnBack = findViewById(R.id.btnAdminCompListBack);
        db = new Database(getApplicationContext());

        loadCompanies();

        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            String companyName = companyList.get(i)[0];
            new AlertDialog.Builder(AdminCompanyListActivity.this)
                    .setTitle("Company Options")
                    .setItems(new String[]{"Edit/View Details", "Delete Company"}, (dialog, which) -> {
                        if (which == 0) {
                            String[] comp = companyList.get(i);
                            Intent it = new Intent(AdminCompanyListActivity.this, AdminEditCompanyActivity.class);
                            it.putExtra("name", comp[0]);
                            it.putExtra("role", comp[1]);
                            it.putExtra("pkg", comp[2]);
                            it.putExtra("eligibility", comp[3]);
                            it.putExtra("tech", comp[4]);
                            it.putExtra("minCgpa", comp[5]);
                            startActivity(it);
                        } else {
                            new AlertDialog.Builder(AdminCompanyListActivity.this)
                                .setTitle("Confirm Delete")
                                .setMessage("Are you sure you want to delete " + companyName + "?")
                                .setPositiveButton("Delete", (d, w) -> {
                                    db.deleteCompany(companyName);
                                    Toast.makeText(this, "Company deleted", Toast.LENGTH_SHORT).show();
                                    loadCompanies();
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                        }
                    })
                    .show();
            return true;
        });

        btnBack.setOnClickListener(v -> finish());

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String[] comp = companyList.get(position);
            Intent it = new Intent(AdminCompanyListActivity.this, AdminEditCompanyActivity.class);
            it.putExtra("name", comp[0]);
            it.putExtra("role", comp[1]);
            it.putExtra("pkg", comp[2]);
            it.putExtra("eligibility", comp[3]);
            it.putExtra("tech", comp[4]);
            it.putExtra("minCgpa", comp[5]);
            startActivity(it);
        });
    }

    private void loadCompanies() {
        companyList = db.getCompanies();
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        for (String[] comp : companyList) {
            HashMap<String, String> item = new HashMap<>();
            item.put("name", comp[0]);
            item.put("role", comp[1]);
            item.put("pkg", "Pkg: " + comp[2]);
            list.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.company_row,
                new String[]{"name", "role", "pkg"},
                new int[]{R.id.rowCompanyName, R.id.rowRole, R.id.rowPackage});
        listView.setAdapter(adapter);
    }
}
