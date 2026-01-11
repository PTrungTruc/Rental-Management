package com.example.rental_management.views;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rental_management.R;
import com.example.rental_management.databases.AccountFirestore;
import com.example.rental_management.models.Account;
import com.example.rental_management.others.SessionManager;

public class ActivityReward extends AppCompatActivity {

    private TextView tvPoints;
    private Button btnChange20, btnChange50, btnChange100;

    private SessionManager session;
    private AccountFirestore accountHelper;
    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        accountHelper = new AccountFirestore(ActivityReward.this);
        session = new SessionManager(ActivityReward.this);

        account = accountHelper.getAccount(session.getUserPhone());

        tvPoints = findViewById(R.id.tvPoints);
        tvPoints.setText(String.valueOf(account.getPoint()));

        btnChange20 = findViewById(R.id.btnChange20);
        btnChange50 = findViewById(R.id.btnChange50);
        btnChange100 = findViewById(R.id.btnChange100);

        btnChange20.setOnClickListener(exchange(20));
        btnChange50.setOnClickListener(exchange(45));
        btnChange100.setOnClickListener(exchange(80));
    }

    private View.OnClickListener exchange(int point) {
        return view -> {
            if (account.getPoint() >= point * 1000) {
                new AlertDialog.Builder(ActivityReward.this)
                        .setTitle("Reward exchange")
                        .setMessage("Are you sure you want to exchange for this reward?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            account.setPoint(account.getPoint() - point * 1000);
                            accountHelper.updateAccount(account);

                            tvPoints.setText(String.valueOf(account.getPoint()));
                            showCompleteDialog();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
            else {
                new AlertDialog.Builder(ActivityReward.this)
                        .setTitle("Exchange failed")
                        .setMessage("Insufficient points for this reward.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        };
    }

    private void showCompleteDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Exchange complete")
                .setMessage("You have successfully exchanged for this reward.")
                .setPositiveButton("OK", null)
                .show();
    }
}