package com.example.rental_management.views;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rental_management.R;
import com.example.rental_management.adapters.ChatGroupAdapter;
import com.example.rental_management.databases.ChatFirestore;
import com.example.rental_management.models.Chat;
import com.example.rental_management.others.SessionManager;

import java.util.List;

public class ActivityChat extends AppCompatActivity {

    private RecyclerView rvChat;
    private ChatGroupAdapter adapter;

    private SessionManager session;
    private ChatFirestore chatHelper;
    private List<Chat> chats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        chatHelper = new ChatFirestore(ActivityChat.this);
        session = new SessionManager(ActivityChat.this);

        chats = chatHelper.getChats(session.getUID());

        rvChat = findViewById(R.id.rvChat);
        rvChat.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ChatGroupAdapter(ActivityChat.this, chats);
        rvChat.setAdapter(adapter);
    }
}