package com.example.rental_management.databases;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.rental_management.models.Account;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AccountFirestore {

    private FirebaseFirestore db;
    private CollectionReference accounts;
    private Context context;

    public AccountFirestore(Context context) {
        db = FirebaseFirestore.getInstance();
        accounts = db.collection("accounts");
        this.context = context;
    }

    public Account getAccountById(String id) {
        Future<Account> future = Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Task<DocumentSnapshot> task = accounts.document(id).get();
                DocumentSnapshot document = Tasks.await(task);
                return document.toObject(Account.class);
            }
            catch (Exception e) {
                Log.e("ERROR_FETCHING_ACCOUNT", e.getMessage());
            }

            return null;
        });

        try {
            return future.get();
        }
        catch (Exception e) {
            Log.e("ERROR_FETCHING_ACCOUNT", e.getMessage());
            return null;
        }
    }

    public Account getAccount(String phone) {
        Future<Account> future = Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Task<QuerySnapshot> task = accounts
                        .whereEqualTo("phone", phone)
                        .get();
                QuerySnapshot querySnapshot = Tasks.await(task);
                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                return document.toObject(Account.class);
            }
            catch (Exception e) {
                Log.e("ERROR_FETCHING_ACCOUNT", e.getMessage());
            }

            return null;
        });

        try {
            return future.get();
        }
        catch (Exception e) {
            Log.e("ERROR_FETCHING_ACCOUNT", e.getMessage());
            return null;
        }
    }

    public void createAccount(Account account) {
        Account temp = getAccount(account.getPhone());

        if (temp == null) {
            accounts.add(account);
        }
        else {
            Toast.makeText(context, "This phone number is already in use", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateAccount(Account account) {
        accounts.document(account.getId()).set(account);
    }

    public List<Account> getGroupMembers(String groupId) {
        Future<List<Account>> future = Executors.newSingleThreadExecutor().submit(() -> {
            List<Account> list = new ArrayList<>();

            try {
                Task<QuerySnapshot> task = accounts
                        .whereEqualTo("groupId", groupId)
                        .get();
                QuerySnapshot querySnapshot = Tasks.await(task);
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    Account account = document.toObject(Account.class);
                    list.add(account);
                }
            }
            catch (Exception e) {
                Log.e("ERROR_FETCHING_ACCOUNT", e.getMessage());
            }

            return list;
        });

        try {
            return future.get();
        }
        catch (Exception e) {
            Log.e("ERROR_FETCHING_ACCOUNT", e.getMessage());
            return new ArrayList<>();
        }
    }
}