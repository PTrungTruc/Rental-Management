package com.example.rental_management.views;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.rental_management.R;
import com.example.rental_management.databases.AccountFirestore;
import com.example.rental_management.models.Account;
import com.example.rental_management.others.SessionManager;

public class Profile extends AppCompatActivity {

    private ImageView profile_img;
    private TextView tvName, tvAge, tvPhone, tvJob, tvHabit, tvHobby;
    private EditText txtName, txtAge, txtPhone, txtJob, txtHabit, txtHobby;
    private Button btnEdit;

    private boolean EDIT_MODE_ON = true;

    private SessionManager session;
    private Account account;
    private AccountFirestore accountHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        session = new SessionManager(Profile.this);
        session.checkLogin();
        accountHelper = new AccountFirestore(Profile.this);
        account = accountHelper.getAccount(session.getUserPhone());

        profile_img = findViewById(R.id.profile_img);
        String imageUrl = account.getImg();
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.baseline_access_time_24)
                .error(R.drawable.baseline_close_24)
                .into(profile_img);

        tvName = findViewById(R.id.tvName);
        tvAge = findViewById(R.id.tvAge);
        tvPhone = findViewById(R.id.tvPhone);
        tvJob = findViewById(R.id.tvJob);
        tvHabit = findViewById(R.id.tvHabit);
        tvHobby = findViewById(R.id.tvHobby);

        txtName = findViewById(R.id.txtName);
        txtAge = findViewById(R.id.txtAge);
        txtPhone = findViewById(R.id.txtPhone);
        txtJob = findViewById(R.id.txtJob);
        txtHabit = findViewById(R.id.txtHabit);
        txtHobby = findViewById(R.id.txtHobby);

        TextView[] tvs = {tvName, tvAge, tvPhone, tvJob, tvHabit, tvHobby};
        EditText[] ets = {txtName, txtAge, txtPhone, txtJob, txtHabit, txtHobby};
        String[] info = {
                account.getName(),
                account.getAge(),
                account.getPhone(),
                account.getJob(),
                account.getHabit(),
                account.getHobby()
        };

        for (int i = 0; i < ets.length; i++) {
            tvs[i].setText(info[i]);
            ets[i].setText(info[i]);
            ets[i].setVisibility(View.INVISIBLE);
        }

        btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(view -> {
            if (!EDIT_MODE_ON) {
                new AlertDialog.Builder(Profile.this)
                        .setTitle("Saving changes")
                        .setMessage("Are you sure you want to save your changes?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            for (int j = 0; j < ets.length; j++) {
                                tvs[j].setText(ets[j].getText().toString());
                            }

                            account.setName(txtName.getText().toString());
                            account.setAge(txtAge.getText().toString());
                            account.setJob(txtJob.getText().toString());
                            account.setHabit(txtHabit.getText().toString());
                            account.setHobby(txtHobby.getText().toString());

                            accountHelper.updateAccount(account);
                            switchMode();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
            else {
                switchMode();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!EDIT_MODE_ON) {
                    new AlertDialog.Builder(Profile.this)
                            .setTitle("Cancel changes")
                            .setMessage("Are you sure you want to cancel your changes?")
                            .setPositiveButton("Yes", (dialogInterface, i) -> {
                                for (int j = 0; j < ets.length; j++) {
                                    ets[j].setText(info[j]);
                                }
                                switchMode();
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            }
        });
    }

    private void switchMode() {
        TextView[] tvs = {tvName, tvAge, tvPhone, tvJob, tvHabit, tvHobby};
        EditText[] ets = {txtName, txtAge, txtPhone, txtJob, txtHabit, txtHobby};

        if (EDIT_MODE_ON) {
            btnEdit.setText("Save");
            btnEdit.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_save_alt_24, 0, 0, 0);
        }
        else {
            btnEdit.setText("Edit");
            btnEdit.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_create_24, 0, 0, 0);
        }

        for (int i = 0; i < ets.length; i++) {
            if (EDIT_MODE_ON) {
                tvs[i].setVisibility(View.GONE);
                ets[i].setVisibility(View.VISIBLE);
            }
            else {
                tvs[i].setVisibility(View.VISIBLE);
                ets[i].setVisibility(View.INVISIBLE);
            }

            if (!ets[i].equals(txtPhone)) {
                ets[i].setEnabled(EDIT_MODE_ON);
            }
        }

        EDIT_MODE_ON = !EDIT_MODE_ON;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Class nextActivity = ActivityChat.class;

        if (id == R.id.miLogout) {
            nextActivity = Login.class;
            session.logoutUser();
        }
        else if (id == R.id.miGroup) {
            if(session.getGroupId().isEmpty()){
                nextActivity = MainActivityGroup.class;
            }
            else{
                nextActivity = GroupProfile.class;
            }
        }
        else if (id == R.id.miReward) {
            nextActivity = ActivityReward.class;
        }

        Intent intent = new Intent(Profile.this, nextActivity);
        startActivity(intent);

        return true;
    }
}