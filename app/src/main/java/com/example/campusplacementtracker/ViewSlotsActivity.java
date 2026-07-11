package com.example.campusplacementtracker;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;

public class ViewSlotsActivity extends AppCompatActivity {

    ListView listView;
    Button btnBack;
    TextView tvTitle, tvNoSlots;
    String companyName; // If null, show all (Admin)
    Database db;
    ArrayList<String[]> slots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_slots);

        listView = findViewById(R.id.listViewSlots);
        btnBack = findViewById(R.id.btnViewSlotsBack);
        tvTitle = findViewById(R.id.tvViewSlotsTitle);
        tvNoSlots = findViewById(R.id.tvNoSlots);

        companyName = getIntent().getStringExtra("companyName");
        if (companyName != null) {
            tvTitle.setText(companyName + " Schedule");
        } else {
            tvTitle.setText("All Interview Slots");
        }

        db = new Database(getApplicationContext());
        loadSlots();

        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            String slotId = slots.get(i)[0];
            new AlertDialog.Builder(this)
                    .setTitle("Delete Slot")
                    .setMessage("Are you sure you want to delete this interview slot?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        db.deleteSlot(slotId);
                        Toast.makeText(this, "Slot deleted", Toast.LENGTH_SHORT).show();
                        loadSlots();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadSlots() {
        if (companyName != null) {
            slots = db.getCompanySlots(companyName);
        } else {
            slots = db.getAllSlots();
        }

        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        for (String[] s : slots) {
            HashMap<String, String> item = new HashMap<>();
            if (companyName == null) {
                // [id, company, date, time, is_booked]
                item.put("title", s[1]);
                item.put("details", s[2] + " at " + s[3] + " | " + (s[4].equals("1") ? "BOOKED" : "AVAILABLE"));
            } else {
                // [id, date, time, is_booked]
                item.put("title", s[1] + " at " + s[2]);
                item.put("details", (s[3].equals("1") ? "Status: BOOKED" : "Status: AVAILABLE"));
            }
            list.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2,
                new String[]{"title", "details"},
                new int[]{android.R.id.text1, android.R.id.text2});
        listView.setAdapter(adapter);

        if (list.isEmpty()) {
            tvNoSlots.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            tvNoSlots.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }
}
