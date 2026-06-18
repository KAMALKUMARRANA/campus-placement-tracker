package com.example.campusplacementtracker;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.HashMap;

public class CompanyListActivity extends AppCompatActivity {

    // Hardcoded company data: {Company Name, Role, Package, Eligibility}
    private String[][] companies = {
            {"TCS", "Software Engineer", "4.5 LPA", "60% throughout, no active backlog"},
            {"Infosys", "Systems Engineer", "5 LPA", "65% in graduation"},
            {"Wipro", "Project Engineer", "4 LPA", "60% throughout, B.Tech/MCA"},
            {"Accenture", "Associate Software Engineer", "5.5 LPA", "65% aggregate, no backlog"},
            {"Cognizant", "Programmer Analyst", "4.8 LPA", "60% throughout"},
            {"Capgemini", "Analyst", "5.2 LPA", "60% aggregate, 2017-2026 passouts"}
    };

    ListView listView;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_list);

        listView = findViewById(R.id.listViewCompanies);
        btnBack = findViewById(R.id.btnBackToProfile);

        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        for (int i = 0; i < companies.length; i++) {
            HashMap<String, String> item = new HashMap<>();
            item.put("name", companies[i][0]);
            item.put("role", "Role: " + companies[i][1]);
            item.put("pkg", "Package: " + companies[i][2]);
            list.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.company_row,
                new String[]{"name", "role", "pkg"},
                new int[]{R.id.rowCompanyName, R.id.rowRole, R.id.rowPackage});
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent it = new Intent(CompanyListActivity.this, ApplyActivity.class);
                it.putExtra("company", companies[position][0]);
                it.putExtra("role", companies[position][1]);
                it.putExtra("pkg", companies[position][2]);
                it.putExtra("eligibility", companies[position][3]);
                startActivity(it);
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