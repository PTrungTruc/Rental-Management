package com.example.rental_management.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rental_management.R;
import com.example.rental_management.databases.AccountFirestore;
import com.example.rental_management.models.Message;
import com.example.rental_management.others.SessionManager;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    int position;

    private Context context;
    private List<Message> messages;
    private SessionManager session;

    private AccountFirestore accountHelper;

    public MessageAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
        session = new SessionManager(context);
        accountHelper = new AccountFirestore(context);
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        if (i == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
            return new MessageHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
            return new MessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        Message message = messages.get(position);

        holder.tvChat.setText(message.getContent());
        holder.tvDate.setText(message.getStringDate());

        Glide.with(context)
                .load(accountHelper.getAccountById(message.getSenderId()).getImg())
                .placeholder(R.drawable.baseline_access_time_24)
                .error(R.drawable.baseline_close_24)
                .into(holder.imgProfile);
    }

    @Override
    public int getItemCount() {
        if (messages == null) {
            return 0;
        }
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSenderId().equals(session.getUID())) {
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    class MessageHolder extends RecyclerView.ViewHolder {
        TextView tvChat, tvDate;
        ImageView imgProfile;

        MessageHolder(@NonNull final View itemView) {
            super(itemView);
            tvChat = itemView.findViewById(R.id.tvMessage);
            tvDate = itemView.findViewById(R.id.tvDate);
            imgProfile = itemView.findViewById(R.id.imgProfile);
        }
    }
}
