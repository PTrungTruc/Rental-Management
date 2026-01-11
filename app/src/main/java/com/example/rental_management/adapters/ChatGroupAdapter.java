package com.example.rental_management.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rental_management.R;
import com.example.rental_management.databases.AccountFirestore;
import com.example.rental_management.databases.ChatFirestore;
import com.example.rental_management.models.Chat;
import com.example.rental_management.models.Message;
import com.example.rental_management.others.SessionManager;
import com.example.rental_management.views.ChatBox;

import java.util.List;

public class ChatGroupAdapter extends RecyclerView.Adapter<ChatGroupAdapter.ChatHolder> {

    private Context context;
    private List<Chat> chats;

    private SessionManager session;
    private AccountFirestore accountHelper;
    private ChatFirestore chatHelper;

    public ChatGroupAdapter(Context context, List<Chat> chats) {
        this.context = context;
        this.chats = chats;
        session = new SessionManager(context);
        accountHelper = new AccountFirestore(context);
        chatHelper = new ChatFirestore(context);
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatHolder(LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        Chat chat = chats.get(position);

        holder.tvChatName.setText(
                chat.isIsGroup() ?
                        "Group Chat" :
                        session.getUID().equals(chat.getMembersId().get(0)) ?
                                accountHelper.getAccountById(chat.getMembersId().get(1)).getName() :
                                accountHelper.getAccountById(chat.getMembersId().get(0)).getName()
        );

        // Observe the messages for this chat
        chatHelper.getMessages(chat.getId()).observe((LifecycleOwner) context, liveMessages -> {
            if (liveMessages != null && !liveMessages.isEmpty()) {
                Message lastMsg = liveMessages.get(liveMessages.size() - 1);

                holder.tvLastMsg.setText(lastMsg.getContent());
                holder.tvTimeSent.setText(lastMsg.getStringDate());
            }
            else {
                holder.tvLastMsg.setText("No messages yet.");
                holder.tvTimeSent.setText("");
            }
        });

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ChatBox.class);
            intent.putExtra("membersId", chat.getMembersId().toArray(new String[0]));
            intent.putExtra("isGroup", chat.isIsGroup());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    class ChatHolder extends RecyclerView.ViewHolder {

        TextView tvChatName, tvLastMsg, tvTimeSent;

        ChatHolder(@NonNull final View itemView) {
            super(itemView);

            tvChatName = itemView.findViewById(R.id.tvChatName);
            tvLastMsg = itemView.findViewById(R.id.tvLastMsg);
            tvTimeSent = itemView.findViewById(R.id.tvTimeSent);
        }
    }
}