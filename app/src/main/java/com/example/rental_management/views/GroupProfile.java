package com.example.rental_management.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rental_management.R;
import com.example.rental_management.adapters.AccountAdapter;
import com.example.rental_management.databases.AccountFirestore;
import com.example.rental_management.databases.ChatFirestore;
import com.example.rental_management.databases.GroupFirestore;
import com.example.rental_management.models.Account;
import com.example.rental_management.models.Group;
import com.example.rental_management.others.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupProfile extends AppCompatActivity {

    private TextView txtName, txtAddress, txtDescription;
    private RecyclerView rvMember;
    private Button btnInteract, btnChat;
    private SessionManager session;
    private AccountAdapter accountAdapter;

    private List<Account> accounts;
    private AccountFirestore accountHelper;
    private Account account;
    private Group group;
    private GroupFirestore groupHelper;
    private ChatFirestore chatHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);

        txtName = findViewById(R.id.txtName);
        txtAddress = findViewById(R.id.txtAddress);
        txtDescription = findViewById(R.id.txtDescription);

        session = new SessionManager(GroupProfile.this);
        groupHelper = new GroupFirestore(GroupProfile.this);
        chatHelper = new ChatFirestore(GroupProfile.this);

        btnChat = findViewById(R.id.btnChat);
        if (session.getGroupId().isEmpty()) {
            Intent intent = getIntent();
            group = (Group) intent.getSerializableExtra("group");
            btnChat.setVisibility(View.GONE);
        }
        else {
            group = groupHelper.getGroup(session.getGroupId());
            btnChat.setVisibility(View.VISIBLE);

            btnChat.setOnClickListener(view -> {
                Intent intent = new Intent(GroupProfile.this, ChatBox.class);

                String[] membersId = new String[accounts.size()];
                for (int i = 0; i < accounts.size(); i++) {
                    membersId[i] = accounts.get(i).getId();
                }

                intent.putExtra("membersId", membersId);
                intent.putExtra("isGroup", true);
                intent.putExtra("group", group);
                startActivity(intent);
            });
        }

        txtName.setText(group.getName());
        txtAddress.setText(group.getAddress());
        txtDescription.setText(group.getDescription());

        btnInteract = findViewById(R.id.btnInteract);
        switchButton();

        accountHelper = new AccountFirestore(GroupProfile.this);
        account = accountHelper.getAccount(session.getUserPhone());

        accounts = accountHelper.getGroupMembers(group.getId());
        rvMember = findViewById(R.id.rvMember);

        accountAdapter = new AccountAdapter(this, accounts);
        rvMember.setLayoutManager(new LinearLayoutManager(this));
        rvMember.setAdapter(accountAdapter);

        btnInteract.setOnClickListener(view -> {
            if (session.getGroupId().isEmpty()) {
                joinGroup();
            }
            else {
                leaveGroup();
            }
        });
    }

    private void joinGroup(){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Group rules")
                .setMessage("By joining, you're agreeing to follow these rules")
                .setView(LayoutInflater.from(this).inflate(R.layout.dialog_rule,null))
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    account.setGroupId(group.getId());
                    accountHelper.updateAccount(account);
                    chatHelper.addMember(group.getGroupId(), account.getId());
                    session.setGroupId(group.getId());

                    Intent intent = new Intent(GroupProfile.this, MainActivityGroup.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();

        TextView txtMsg = dialog.findViewById(R.id.txtmsg);
        try{
            assert txtMsg != null;
            txtMsg.setText(group.getRules());
        }
        catch (Exception e) {
            Log.d("Error", e.toString());
        }
    }

    private void leaveGroup(){
        new AlertDialog.Builder(this)
                .setTitle("Leaving group")
                .setMessage("Are you sure you want to leave this group?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    boolean isValid = true;

                    if (account.getId().equals(group.getOwnerId())) {
                        if (accounts.size() > 1) {
                            Toast.makeText(GroupProfile.this, "There are still members in this group", Toast.LENGTH_SHORT).show();
                            isValid = false;
                        }
                        else {
                            groupHelper.deleteGroup(group.getId());
                        }
                    }

                    if (isValid) {
                        session.setGroupId("");

                        account.setGroupId("");
                        accountHelper.updateAccount(account);

                        chatHelper.removeMember(group.getGroupId(), account.getId());

                        Intent intent = new Intent(GroupProfile.this, MainActivityGroup.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void switchButton() {
        if (session.getGroupId().isEmpty()) {
            btnInteract.setText("Join Group");
            btnInteract.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green));
        }
        else {
            btnInteract.setText("Leave Group");
            btnInteract.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_group, menu);
        SessionManager session1 = new SessionManager(GroupProfile.this);
        try{
            String groupId = session1.getGroupId();
            boolean isView = (groupId == null || groupId.isEmpty());
            if (isView)
            {
                for (int i = 0; i < menu.size(); i++)
                    menu.getItem(i).setVisible(false);
            }
        }
        catch (Exception e){
            Log.d("Error", e.toString());
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Class nextActivity = PaymentActivity.class;

        if (id == R.id.miEvent) {
            nextActivity = MainActivityCalendar.class;
        }
        else if (id == R.id.miProfile) {
            nextActivity = Profile.class;
        }

        Intent intent = new Intent(GroupProfile.this, nextActivity);
        intent.putExtra("monthlyBill", group.getPrice());
        startActivity(intent);
//        finish();

        return true;
    }
}