package com.example.rental_management.databases;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.rental_management.models.Chat;
import com.example.rental_management.models.Message;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ChatFirestore {
    private FirebaseFirestore db;
    private CollectionReference chats;
    private Context context;
    private MutableLiveData<List<Message>> messagesLiveData = new MutableLiveData<>();

    public ChatFirestore(Context context) {
        db = FirebaseFirestore.getInstance();
        chats = db.collection("chats");
        this.context = context;
    }

    public boolean chatExist(List<String> membersId, boolean isGroup) {
        Future<Boolean> future = Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Task<QuerySnapshot> task = chats.get();
                QuerySnapshot querySnapshot = Tasks.await(task);

                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    Chat chat = document.toObject(Chat.class);
                    List<String> chatMembers = chat.getMembersId();

                    if (chat.isIsGroup() == isGroup &&
                            membersId.size() == chatMembers.size() &&
                            new HashSet<>(membersId).containsAll(chatMembers)
                    ) {
                        return true;
                    }
                }
            }
            catch (Exception e) {
                Log.d("ERROR_CHECKING_CHAT", e.getMessage());
            }

            return false;
        });

        try {
            return future.get();
        }
        catch (Exception e) {
            Log.d("ERROR_CHECKING_CHAT", e.getMessage());
            return false;
        }
    }

    public void createChat(List<String> members, boolean isGroup) {
        Chat chat = new Chat(members, isGroup);
        chats.add(chat);
    }

    public List<Chat> getChats(String uid) {
        List<Chat> list = new ArrayList<>();

        Future<List<Chat>> future = Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Task<QuerySnapshot> task = chats
                        .whereArrayContains("membersId", uid)
                        .get();
                QuerySnapshot querySnapshot = Tasks.await(task);
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    list.add(document.toObject(Chat.class));
                }
            }
            catch (Exception e) {
                Log.d("ERROR_FETCHING_CHATS", e.getMessage());
            }

            return list;
        });

        try {
            return future.get();
        }
        catch (Exception e) {
            Log.d("ERROR_FETCHING_CHATS", e.getMessage());
            return list;
        }
    }

    public Chat getChat(List<String> membersId, boolean isGroup) {
        Future<Chat> future = Executors.newSingleThreadExecutor().submit(() -> {
            Chat chat = new Chat(new ArrayList<>(), isGroup);

            try {
                Task<QuerySnapshot> task = chats.get();
                QuerySnapshot querySnapshot = Tasks.await(task);
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    chat = document.toObject(Chat.class);
                    List<String> chatMembers = chat.getMembersId();

                    if (membersId.size() == chatMembers.size() && new HashSet<>(membersId).containsAll(chatMembers)) {
                        break;
                    }
                    else {
                        chat = new Chat(new ArrayList<>(), isGroup);
                    }
                }
            }
            catch (Exception e) {
                Log.d("ERROR_FETCHING_CHAT", e.getMessage());
            }

            return chat;
        });

        try {
            return future.get();
        }
        catch (Exception e) {
            Log.d("ERROR_FETCHING_CHAT", e.getMessage());
            return null;
        }
    }

    public Chat getGroupChat(String id) {
        Future<Chat> future = Executors.newSingleThreadExecutor().submit(() -> {
            Chat chat = new Chat(new ArrayList<>(), true);

            try {
                Task<DocumentSnapshot> task = chats.document(id).get();
                DocumentSnapshot document = Tasks.await(task);
                chat = document.toObject(Chat.class);
            }
            catch (Exception e) {
                Log.d("ERROR_FETCHING_CHAT", e.getMessage());
            }

            return chat;
        });

        try {
            return future.get();
        }
        catch (Exception e) {
            Log.d("ERROR_FETCHING_CHAT", e.getMessage());
            return null;
        }
    }

    public LiveData<List<Message>> getMessages(String id) {
        CollectionReference messages = db.collection("chats").document(id).collection("messages");

        messages.orderBy("date", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("ERROR_FETCHING_MESSAGES", error.getMessage());
                    return;
                }

                List<Message> updatedMessages = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : value) {
                    Message message = documentSnapshot.toObject(Message.class);
                    updatedMessages.add(message);
                }

                // Update LiveData with new messages
                messagesLiveData.setValue(updatedMessages);
            }
        });

        return messagesLiveData;
    }

    public void addMsg(Message message, String id) {
        CollectionReference messages = db.collection("chats").document(id).collection("messages");
        messages.add(message);
    }

    public void addMember(String id, String uid) {
        Chat chat = getGroupChat(id);
        List<String> membersId = chat.getMembersId();
        membersId.add(uid);
        chats.document(id).update("membersId", membersId);
    }

    public void removeMember(String id, String uid) {
        Chat chat = getGroupChat(id);
        List<String> membersId = chat.getMembersId();
        membersId.remove(uid);
        chats.document(id).update("membersId", membersId);
    }
}
