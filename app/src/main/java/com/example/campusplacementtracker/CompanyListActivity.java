package com.example.campusplacementtracker;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.HashMap;

public class CompanyListActivity extends AppCompatActivity {

    private String[][] companies = {
            {"TCS", "Software Engineer", "4.5 LPA", "60% throughout, no active backlog"},
            {"Infosys", "Systems Engineer", "5 LPA", "65% in graduation"},
            {"Wipro", "Project Engineer", "4 LPA", "60% throughout, B.Tech/MCA"},
            {"Accenture", "Associate Software Engineer", "5.5 LPA", "65% aggregate, no backlog"},
            {"Cognizant", "Programmer Analyst", "4.8 LPA", "60% throughout"},
            {"Capgemini", "Analyst", "5.2 LPA", "60% aggregate, 2017-2026 passouts"}
    };

    private ArrayList<Integer> visibleIndices = new ArrayList<>();

    ListView listView;
    EditText edSearch;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_list);

        listView = findViewById(R.id.listViewCompanies);
        edSearch = findViewById(R.id.edSearchCompany);
        btnBack = findViewById(R.id.btnBackToProfile);

        showCompanies("");

        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showCompanies(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void showCompanies(String query) {
        visibleIndices.clear();
        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        String lowerQuery = query.toLowerCase().trim();

        for (int i = 0; i < companies.length; i++) {
            String name = companies[i][0].toLowerCase();
            String role = companies[i][1].toLowerCase();

            if (lowerQuery.isEmpty() || name.contains(lowerQuery) || role.contains(lowerQuery)) {
                HashMap<String, String> item = new HashMap<>();
                item.put("name", companies[i][0]);
                item.put("role", "Role: " + companies[i][1]);
                item.put("pkg", "Package: " + companies[i][2]);
                list.add(item);
                visibleIndices.add(i);
            }
        }

        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.company_row,
                new String[]{"name", "role", "pkg"},
                new int[]{R.id.rowCompanyName, R.id.rowRole, R.id.rowPackage});
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                int realIndex = visibleIndices.get(position);
                Intent it = new Intent(CompanyListActivity.this, ApplyActivity.class);
                it.putExtra("company", companies[realIndex][0]);
                it.putExtra("role", companies[realIndex][1]);
                it.putExtra("pkg", companies[realIndex][2]);
                it.putExtra("eligibility", companies[realIndex][3]);
                startActivity(it);
            }
        });
    }
}