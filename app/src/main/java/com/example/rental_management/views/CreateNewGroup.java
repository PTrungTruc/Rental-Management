package com.example.rental_management.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.rental_management.R;
import com.example.rental_management.databases.GroupFirestore;
import com.example.rental_management.models.Group;
import com.example.rental_management.others.SessionManager;

public class CreateNewGroup extends AppCompatActivity {

    private Button btnCreate;
    private EditText txtName, txtAddress, txtDescription, txtRule, txtPrice;

    private SessionManager session;
    private GroupFirestore groupHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);

        session = new SessionManager(CreateNewGroup.this);
        groupHelper = new GroupFirestore(CreateNewGroup.this);

        txtName = findViewById(R.id.txtPhone);
        txtAddress = findViewById(R.id.txtAddress);
        txtDescription = findViewById(R.id.txtDescription);
        txtRule = findViewById(R.id.txtRule);
        txtPrice = findViewById(R.id.txtPrice);

        btnCreate = findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(view -> {
            EditText[] ets = {txtName, txtAddress, txtDescription, txtRule};
            boolean filled = true;

            for (EditText txt : ets) {
                if(txt.getText().toString().isEmpty()){
                    txt.setError("Cannot be left blank");
                    filled = false;
                }
            }

            if (filled) {
                Group group = new Group(
                        txtName.getText().toString(),
                        txtAddress.getText().toString(),
                        txtDescription.getText().toString(),
                        txtRule.getText().toString(),
                        session.getUID(),
                        txtPrice.getText().toString()
                );

                groupHelper.createGroup(group);
                session.setGroupId(group.getId());
            }

            Intent intent = new Intent(CreateNewGroup.this, GroupProfile.class);
            startActivity(intent);
            finish();
        });
    }
}