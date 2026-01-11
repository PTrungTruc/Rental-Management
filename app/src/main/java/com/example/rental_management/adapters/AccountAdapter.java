package com.example.rental_management.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rental_management.R;
import com.example.rental_management.databases.GroupFirestore;
import com.example.rental_management.models.Account;
import com.example.rental_management.others.SessionManager;
import com.example.rental_management.views.AccountProfile;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountHolder>{

    int position;
    private String group;

    private Context context;
    private List<Account> accounts;
    private SessionManager session;
    private GroupFirestore groupHelper;

    public AccountAdapter(Context context, List<Account> accounts) {
        this.context = context;
        this.accounts = accounts;
        session = new SessionManager(context);
        groupHelper = new GroupFirestore(context);
    }

    @NonNull
    @Override
    public AccountHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.account, parent, false);
        return new AccountHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountHolder holder, int position) {
        Account account = accounts.get(position);

        holder.tvName.setText(account.getName());
        holder.tvPhone.setText(account.getPhone());

        if (account.getId().equalsIgnoreCase(session.getUID())) {
            holder.tvAuth.setText("You");
        }
        else if (account.getId().equalsIgnoreCase (
                groupHelper.getGroup(account.getGroupId()).getOwnerId()
        )) {
            holder.tvAuth.setText("Owner");
        }
        else {
            holder.tvAuth.setText("Member");

            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, AccountProfile.class);
                intent.putExtra("Account", account);
                context.startActivity(intent);
            });
        }

        if(holder.tvAuth.getText().toString()
                .equalsIgnoreCase("Owner")
        ) {
            holder.itemView.setOnLongClickListener(view -> {
                showDeleteConfirmDialog(position);
                return false;
            });
        }
    }

    @Override
    public int getItemCount() {
        if (accounts == null) {
            return 0;
        }
        return accounts.size();
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    class AccountHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvAuth;

        AccountHolder(@NonNull final View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvAuth = itemView.findViewById(R.id.tvAuth);
        }
    }

    private void showDeleteConfirmDialog(int position) {
        String name = accounts.get(position).getName();

        new AlertDialog.Builder(context)
                .setTitle("Delete " + name)
                .setMessage("Remove this user from group?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
//                    if (isManager) {
//                        dbHelper.deleteEmployee(name);
//                    }
//                    else {
//                        dbHelper.deleteManager(name);
//                    }

                    accounts.remove(position);
                    notifyDataSetChanged();
                })
                .setNegativeButton("No", (dialogInterface, i) -> {})
                .show();
    }
}