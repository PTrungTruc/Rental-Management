package com.example.rental_management.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rental_management.R;
import com.example.rental_management.models.Group;
import com.example.rental_management.others.SessionManager;
import com.example.rental_management.views.GroupProfile;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupHolder> {

    int position;
    String auth;

    private Context context;
    private List<Group> groups;
    private SessionManager session;

    public GroupAdapter(Context context, List<Group> groups) {
        this.context = context;
        this.groups = groups;
        session = new SessionManager(context);
    }

    @NonNull
    @Override
    public GroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group, parent, false);

        return new GroupHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupHolder holder, int position) {
        Group group = groups.get(position);

        holder.tvName.setText(group.getName());
        holder.tvPrice.setText(group.getPrice());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, GroupProfile.class);
            intent.putExtra("group", group);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (groups == null) {
            return 0;
        }
        return groups.size();
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    class GroupHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;

        GroupHolder(@NonNull final View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAge);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}