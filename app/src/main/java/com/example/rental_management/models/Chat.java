package com.example.rental_management.models;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;
import java.util.List;

public class Chat implements Serializable {

    @DocumentId
    private String id;
    private List<String> membersId;
    private boolean isGroup;

    public Chat() {}

    public Chat(List<String> membersId, boolean isGroup) {
        this.membersId = membersId;
        this.isGroup = isGroup;
    }

    public String getId() {
        return id;
    }

    public List<String> getMembersId() {
        return membersId;
    }

    public boolean isIsGroup() {
        return isGroup;
    }
}