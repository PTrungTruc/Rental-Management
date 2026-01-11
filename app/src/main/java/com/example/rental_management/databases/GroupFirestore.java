package com.example.rental_management.databases;

import android.content.Context;
import android.util.Log;

import com.example.rental_management.models.Group;
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

public class GroupFirestore {

    private FirebaseFirestore db;
    private CollectionReference groups;
    private Context context;

    public GroupFirestore(Context context) {
        db = FirebaseFirestore.getInstance();
        groups = db.collection("groups");
        this.context = context;
    }

    public Group getGroup(String id) {
        Future<Group> future = Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Task<DocumentSnapshot> task = groups.document(id).get();
                DocumentSnapshot documentSnapshot = Tasks.await(task);
                return documentSnapshot.toObject(Group.class);
            }
            catch (Exception e) {
                Log.d("ERROR_FETCHING_GROUP", e.getMessage());
            }

            return null;
        });

        try {
            return future.get();
        }
        catch (Exception e) {
            Log.d("ERROR_FETCHING_GROUP", e.getMessage());
            return null;
        }
    }

    public List<Group> getGroups() {
        Future<List<Group>> future = Executors.newSingleThreadExecutor().submit(() -> {
            List<Group> list = new ArrayList<>();

            try {
                Task<QuerySnapshot> task = groups.get();
                QuerySnapshot querySnapshot = Tasks.await(task);

                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    Group group = document.toObject(Group.class);
                    list.add(group);
                }
            }
            catch (Exception e) {
                Log.d("ERROR_FETCHING_GROUPS", e.getMessage());
            }

            return list;
        });

        try {
            return future.get();
        }
        catch (Exception e) {
            Log.d("ERROR_FETCHING_GROUPS", e.getMessage());
            return new ArrayList<>();
        }
    }

    public void createGroup(Group group) {
        groups.add(group);
    }

    public void deleteGroup(String id) {
        groups.document(id).delete();
    }

    public void updateGroup(Group group) {
        groups.document(group.getId()).set(group);
    }
}