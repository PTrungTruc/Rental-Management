package com.example.rental_management.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rental_management.R;
import com.example.rental_management.models.Account;
import com.example.rental_management.others.SessionManager;

import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingHolder>{

    int position;
    private Context context;
    private List<Account> accounts;
    private SessionManager session;

    public RatingAdapter(Context context, List<Account> accounts) {
        this.context = context;
        this.accounts = accounts;
        session = new SessionManager(context);
    }

    @NonNull
    @Override
    public RatingHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.account, parent, false);

        return new RatingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingHolder holder, int position) {
        Account account = accounts.get(position);

        holder.tvName.setText(account.getName());
//        holder.tvAuth.setText(account.getPhone());
        holder.rating.setRating(account.getRating());

        holder.rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                account.setRating(v);
            }
        });
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

    class RatingHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvAuth;
        RatingBar rating;

        RatingHolder(@NonNull final View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAge);
            tvAuth = itemView.findViewById(R.id.tvAuth);
            rating = itemView.findViewById(R.id.rating);
        }
    }
}
