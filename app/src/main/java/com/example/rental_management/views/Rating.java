package com.example.rental_management.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rental_management.R;
import com.example.rental_management.adapters.RatingAdapter;
import com.example.rental_management.models.Account;

import java.util.ArrayList;
import java.util.List;

public class Rating extends AppCompatActivity {

    private RecyclerView list;
    private Button btnSave;

    private List<Account> accounts = new ArrayList<>();
    private List<Account> accountsForSave = new ArrayList<>();
    private RatingAdapter ratingAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        list = findViewById(R.id.rvMember);
        btnSave = findViewById(R.id.btnEdit);

        ratingAdapter = new RatingAdapter(this, accounts);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(ratingAdapter);

        btnSave.setOnClickListener(view -> {
            saveRating();
        });
    }

    private void saveRating(){

        Intent intent = new Intent(Rating.this, Profile.class);
        startActivity(intent);
        finish();
    }
}