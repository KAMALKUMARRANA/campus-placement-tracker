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
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;

public class CompanyListActivity extends AppCompatActivity {

    private ArrayList<String[]> companyList = new ArrayList<>();
    private ArrayList<Integer> visibleIndices = new ArrayList<>();

    ListView listView;
    EditText edSearch;
    TextView tvNoRecord;
    Button btnBack;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_list);

        listView = findViewById(R.id.listViewCompanies);
        edSearch = findViewById(R.id.edSearchCompany);
        tvNoRecord = findViewById(R.id.tvNoCompanies);
        btnBack = findViewById(R.id.btnBackToProfile);
        db = new Database(getApplicationContext());

        loadCompaniesFromDb();
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

    private void loadCompaniesFromDb() {
        companyList = db.getCompanies();
    }

    private void showCompanies(String query) {
        visibleIndices.clear();
        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        String lowerQuery = query.toLowerCase().trim();

        for (int i = 0; i < companyList.size(); i++) {
            String[] company = companyList.get(i);
            String name = company[0].toLowerCase();
            String role = company[1].toLowerCase();

            if (lowerQuery.isEmpty() || name.contains(lowerQuery) || role.contains(lowerQuery)) {
                HashMap<String, String> item = new HashMap<>();
                item.put("name", company[0]);
                item.put("role", "Role: " + company[1] + " | Tech: " + company[4]);
                item.put("pkg", "Package: " + company[2] + " | Min CGPA: " + company[5]);
                list.add(item);
                visibleIndices.add(i);
            }
        }

        if (list.isEmpty()) {
            tvNoRecord.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            tvNoRecord.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.company_row,
                new String[]{"name", "role", "pkg"},
                new int[]{R.id.rowCompanyName, R.id.rowRole, R.id.rowPackage});
        listView.setAdapter(adapter);

        if (list.isEmpty()) {
            Toast.makeText(this, "No companies found matching your search", Toast.LENGTH_SHORT).show();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                int realIndex = visibleIndices.get(position);
                String[] selectedCompany = companyList.get(realIndex);
                Intent it = new Intent(CompanyListActivity.this, ApplyActivity.class);
                it.putExtra("company", selectedCompany[0]);
                it.putExtra("role", selectedCompany[1]);
                it.putExtra("pkg", selectedCompany[2]);
                it.putExtra("eligibility", selectedCompany[3]);
                it.putExtra("tech_stack", selectedCompany[4]);
                it.putExtra("min_cgpa", selectedCompany[5]);
                startActivity(it);
            }
        });
    }
}
