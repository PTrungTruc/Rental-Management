package com.example.rental_management.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.rental_management.R;
import com.example.rental_management.adapters.MessageAdapter;
import com.example.rental_management.databases.ChatFirestore;
import com.example.rental_management.databases.GroupFirestore;
import com.example.rental_management.models.Chat;
import com.example.rental_management.models.Group;
import com.example.rental_management.models.Message;
import com.example.rental_management.others.SessionManager;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatBox extends AppCompatActivity {

    private RecyclerView rvChat;
    private EditText txtMessage;
    private ImageButton btnSendMsg;
    private SessionManager session;

    private List<Message> messages;
    private MessageAdapter messageAdapter;

    private ChatFirestore chatHelper;
    private GroupFirestore groupHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);

        chatHelper = new ChatFirestore(ChatBox.this);
        Intent intent = getIntent();
        List<String> membersId = new ArrayList<>(Arrays.asList(intent.getStringArrayExtra("membersId")));
        boolean isGroup = intent.getBooleanExtra("isGroup", false);

        if (!chatHelper.chatExist(membersId, isGroup)) {
            chatHelper.createChat(membersId, isGroup);
        }

        Chat chat = chatHelper.getChat(membersId, isGroup);

        groupHelper = new GroupFirestore(ChatBox.this);
        if (intent.hasExtra("group")) {
            Group group = (Group) intent.getSerializableExtra("group");
            group.setGroupId(chat.getId());
            groupHelper.updateGroup(group);
        }

        txtMessage = findViewById(R.id.txtMessage);
        session = new SessionManager(ChatBox.this);

        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messages);
        rvChat = findViewById(R.id.rvChat);
        rvChat.setLayoutManager(new LinearLayoutManager(ChatBox.this));
        rvChat.setAdapter(messageAdapter);

        chatHelper.getMessages(chat.getId()).observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> liveMessages) {
                messages.clear();
                messages.addAll(liveMessages);
                messageAdapter.notifyDataSetChanged();

                rvChat.scrollToPosition(messages.size() - 1);
            }
        });

        btnSendMsg = findViewById(R.id.btnSendMsg);
        btnSendMsg.setOnClickListener(view -> {
            String msg = txtMessage.getText().toString();
            if(!msg.isEmpty()){
                Message message = new Message(msg, Timestamp.now(), session.getUID());
                chatHelper.addMsg(message, chat.getId());

                messages.add(message);
                messageAdapter.notifyDataSetChanged();

                txtMessage.setText("");
                txtMessage.clearFocus();
            }
        });
    }
}