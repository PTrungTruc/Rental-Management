package com.example.rental_management.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.example.rental_management.R;
import com.example.rental_management.adapters.GroupAdapter;
import com.example.rental_management.databases.GroupFirestore;
import com.example.rental_management.models.Group;
import com.example.rental_management.others.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivityGroup extends AppCompatActivity {
    private Button btnCreate;
    private EditText searchBar;
    private RecyclerView rvGroup;
    private GroupAdapter recomAdapter, groupAdapter;

    private GroupFirestore groupHelper;
    private List<Group> groups, recoms;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_group);

        session = new SessionManager(MainActivityGroup.this);
        if (!session.getGroupId().isEmpty()) {
            Intent intent = new Intent(MainActivityGroup.this, GroupProfile.class);
            startActivity(intent);
            finish();
        }

        groupHelper = new GroupFirestore(MainActivityGroup.this);

//        rvRecom = findViewById(R.id.rvRecom);
        rvGroup = findViewById(R.id.rvGroup);

//        recoms = new ArrayList<>();
        groups = groupHelper.getGroups();

//        recomAdapter = new GroupAdapter(this, recoms);
        groupAdapter = new GroupAdapter(this, groups);

//        rvRecom.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvGroup.setLayoutManager(new GridLayoutManager(this, 2));

//        rvRecom.setAdapter(recomAdapter);
        rvGroup.setAdapter(groupAdapter);

        btnCreate = findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(view -> {
            Intent intent = new Intent(this, CreateNewGroup.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (session.getGroupId().isEmpty()) {
            groups.clear();
            groups.addAll(groupHelper.getGroups());
            groupAdapter.notifyDataSetChanged();
        }
        else {
            Intent intent = new Intent(MainActivityGroup.this, GroupProfile.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.miSearch);
        menuItem.setActionView(R.layout.search_bar);

        searchBar = menuItem.getActionView().findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                groups.clear();
                groups.addAll(
                        groupHelper.getGroups()
                                .stream()
                                .filter(group ->
                                        group.getName().toLowerCase().contains(
                                                searchBar.getText().toString().toLowerCase()
                                        ) ||
                                        group.getPrice().contains(searchBar.getText().toString()))
                                .collect(Collectors.toList())
                );
                groupAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Class nextActivity = Profile.class;

        if (id == R.id.miLogout) {
            nextActivity = Login.class;
            session.logoutUser();
        }

        Intent intent = new Intent(MainActivityGroup.this, nextActivity);
        startActivity(intent);
        finish();

        return true;
    }
}