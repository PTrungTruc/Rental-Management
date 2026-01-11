package com.example.rental_management.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.rental_management.R;
import com.example.rental_management.databases.ChatFirestore;
import com.example.rental_management.models.Account;
import com.example.rental_management.others.SessionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccountProfile extends AppCompatActivity {

    private ImageView profile_img;
    private TextView tvName, tvAge, tvPhone, tvJob, tvHabit, tvHobby;
    private Button btnChat;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_profile);

        session = new SessionManager(AccountProfile.this);

        profile_img = findViewById(R.id.profile_img);
        tvName = findViewById(R.id.tvName);
        tvAge = findViewById(R.id.tvAge);
        tvPhone = findViewById(R.id.tvPhone);
        tvJob = findViewById(R.id.tvJob);
        tvHabit = findViewById(R.id.tvHabit);
        tvHobby = findViewById(R.id.tvHobby);

        Intent intent = getIntent();
        Account account = (Account) intent.getSerializableExtra("Account");
        tvName.setText(account.getName());
        tvAge.setText(account.getAge());
        tvPhone.setText(account.getPhone());
        tvJob.setText(account.getJob());
        tvHabit.setText(account.getHabit());
        tvHobby.setText(account.getHobby());

        Glide.with(this)
                .load(account.getImg())
                .placeholder(R.drawable.baseline_access_time_24)
                .error(R.drawable.baseline_close_24)
                .into(profile_img);

        btnChat = findViewById(R.id.btnChat);
        btnChat.setOnClickListener(view -> {
            String[] membersId = {session.getUID(), account.getId()};

            Intent chat = new Intent(this, ChatBox.class);
            chat.putExtra("membersId", membersId);
            chat.putExtra("isGroup", false);
            startActivity(chat);
        });
    }
}