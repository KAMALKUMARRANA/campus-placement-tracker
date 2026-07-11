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

public class AdminUserListActivity extends AppCompatActivity {

    ListView listView;
    Button btnBack;
    EditText edSearch;
    TextView tvNoUsers;
    Database db;
    ArrayList<String[]> userList;
    ArrayList<Integer> filteredIndices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_list);

        listView = findViewById(R.id.listViewUsers);
        btnBack = findViewById(R.id.btnUserListBack);
        edSearch = findViewById(R.id.edAdminSearchUsers);
        tvNoUsers = findViewById(R.id.tvAdminNoUsers);
        db = new Database(getApplicationContext());

        loadUsers("");

        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadUsers(s.toString());
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

    private void loadUsers(String query) {
        userList = db.getAllUsers();
        filteredIndices.clear();
        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        String q = query.toLowerCase().trim();

        for (int i = 0; i < userList.size(); i++) {
            String[] user = userList.get(i);
            String username = user[0].toLowerCase();
            String fullname = user[2].toLowerCase();

            if (q.isEmpty() || username.contains(q) || fullname.contains(q)) {
                HashMap<String, String> item = new HashMap<>();
                item.put("username", user[0]);
                item.put("details", (user[2].isEmpty() ? "No Name" : user[2]) + " | " + (user[3].isEmpty() ? "No Roll" : user[3]));
                item.put("status", "Status: " + user[6].toUpperCase());
                list.add(item);
                filteredIndices.add(i);
            }
        }

        if (list.isEmpty()) {
            tvNoUsers.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            tvNoUsers.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.user_row,
                new String[]{"username", "details", "status"},
                new int[]{R.id.rowUserUsername, R.id.rowUserDetails, R.id.rowUserStatus});
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                int realIndex = filteredIndices.get(position);
                String[] selectedUser = userList.get(realIndex);
                Intent it = new Intent(AdminUserListActivity.this, AdminEditUserActivity.class);
                it.putExtra("username", selectedUser[0]);
                it.putExtra("email", selectedUser[1]);
                it.putExtra("fullname", selectedUser[2]);
                it.putExtra("rollno", selectedUser[3]);
                it.putExtra("branch", selectedUser[4]);
                it.putExtra("cgpa", selectedUser[5]);
                it.putExtra("status", selectedUser[6]);
                it.putExtra("tech_stack", selectedUser.length > 7 ? selectedUser[7] : "");
                startActivity(it);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers(edSearch.getText().toString());
    }
}
