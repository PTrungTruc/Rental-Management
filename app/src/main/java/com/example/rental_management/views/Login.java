package com.example.rental_management.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rental_management.R;
import com.example.rental_management.databases.AccountFirestore;
import com.example.rental_management.models.Account;
import com.example.rental_management.others.SessionManager;

public class Login extends AppCompatActivity {

    private AccountFirestore accountHelper;
    private EditText txtPhone, txtPass;
    private Button btnLogin;
    private TextView register;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        accountHelper = new AccountFirestore(Login.this);

        txtPhone = findViewById(R.id.txtPhone);
        txtPass = findViewById(R.id.txtPass);
        btnLogin = findViewById(R.id.btnLogin);
        register = findViewById(R.id.register);

        txtPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher("VN"));

        btnLogin.setOnClickListener(onSignIn);
        register.setOnClickListener(onRegister);

        session = new SessionManager(Login.this);
    }

    private View.OnClickListener onSignIn = view -> {
        String phone = txtPhone.getText().toString().replace(" ", ""),
                pass = txtPass.getText().toString(),
                msg = "Username or password is incorrect";

        if (!(phone.isEmpty() && pass.isEmpty())) {
            if (phone.length() != 10) {
                txtPhone.setError("Phone number must be 10 digits");
            }
            else {
                Account account = accountHelper.getAccount(phone);

                if (account != null && account.getPass().equals(pass)) {
                    msg = "Login successful";
                    session.createLoginSession(account.getId(), account.getPhone(), account.getGroupId());
                    Intent intent = new Intent(Login.this, Profile.class);
                    startActivity(intent);
                }
            }
        }
        else {
            msg = "Username and password can't be empty";
        }

        Toast.makeText(Login.this, msg, Toast.LENGTH_SHORT).show();
    };

    private View.OnClickListener onRegister = view -> {
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
    };
}