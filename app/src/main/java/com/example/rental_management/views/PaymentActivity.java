package com.example.rental_management.views;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rental_management.R;
import com.example.rental_management.adapters.TransactionAdapter;
import com.example.rental_management.databases.AccountFirestore;
import com.example.rental_management.databases.TransactionFirestore;
import com.example.rental_management.models.Account;
import com.example.rental_management.models.Transaction;
import com.example.rental_management.others.SessionManager;
import com.google.firebase.Timestamp;

import java.util.List;

public class PaymentActivity extends AppCompatActivity {
    private RecyclerView rvHistory;
    private EditText txtPayment;
    private TextView tvMonthlyBill;
    private ImageButton btnPay;
    private SessionManager session;

    private List<Transaction> transactions;
    private TransactionAdapter transactionAdapter;
    private AccountFirestore accountHelper;
    private TransactionFirestore transactionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paying);

        session = new SessionManager(PaymentActivity.this);

        txtPayment = findViewById(R.id.txtPayment);
        tvMonthlyBill = findViewById(R.id.tvMonthlyBill);
        tvMonthlyBill.setText(getIntent().getStringExtra("monthlyBill"));

        accountHelper = new AccountFirestore(PaymentActivity.this);
        transactionHelper = new TransactionFirestore(PaymentActivity.this);
        transactions = transactionHelper.getTransactions(session.getGroupId(), session.getUID());

        rvHistory = findViewById(R.id.history);
        transactionAdapter = new TransactionAdapter(this, transactions);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(transactionAdapter);

        btnPay = findViewById(R.id.btnPay);
        btnPay.setOnClickListener(view -> {
            String msg = "Transaction complete";

            if (txtPayment.getText().toString().isEmpty()) {
                msg = "Enter an amount of money to pay";
            }
            else {
                int value = Integer.parseInt(txtPayment.getText().toString()),
                totalBill = Integer.parseInt(tvMonthlyBill.getText().toString());

                if (value > totalBill) {
                    msg = "You're overpaying the expense";
                }
                else {
                    Account account = accountHelper.getAccount(session.getUserPhone());

                    Transaction transaction = new Transaction(
                            account.getId(),
                            account.getGroupId(),
                            Timestamp.now(),
                            value
                    );

                    transactionHelper.addTransaction(transaction);

                    transactions.clear();
                    transactions.addAll(transactionHelper.getTransactions(session.getGroupId(), session.getUID()));
                    transactionAdapter.notifyDataSetChanged();

                    if (value > 50000) {
                        int multiplier = value / 50000;
                        account.setPoint(account.getPoint() + 1000 * multiplier);
                        accountHelper.updateAccount(account);
                    }
                }
            }

            Toast.makeText(PaymentActivity.this, msg, Toast.LENGTH_SHORT).show();
        });
    }
}
