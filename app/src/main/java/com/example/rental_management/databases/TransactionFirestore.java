package com.example.rental_management.databases;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.rental_management.models.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TransactionFirestore {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference transactions = db.collection("transactions");
    private Context context;

    public TransactionFirestore(Context context) {
        this.context = context;
        transactions = db.collection("transactions");
        this.context = context;
    }

    public List<Transaction> getTransactions(String groupId, String uid) {
        List<Transaction> list = new ArrayList<>();

        Future<List<Transaction>> future = Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Task<QuerySnapshot> task = transactions
                        .whereEqualTo("groupId", groupId)
                        .whereEqualTo("uid", uid)
                        .get();
                QuerySnapshot querySnapshot = Tasks.await(task);

                for (DocumentSnapshot documentSnapshot: querySnapshot) {
                    Transaction transaction = documentSnapshot.toObject(Transaction.class);
                    list.add(transaction);
                }
            }
            catch (Exception e) {
                Log.d("ERROR_FETCHING_TRANSACTIONS", e.getMessage());
            }

            return list;
        });

        try {
            return future.get();
        }
        catch (Exception e) {
            Log.d("ERROR_FETCHING_TRANSACTIONS", e.getMessage());
            return list;
        }
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }
}
