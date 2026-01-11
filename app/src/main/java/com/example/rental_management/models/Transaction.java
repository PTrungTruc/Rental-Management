package com.example.rental_management.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;
import java.text.SimpleDateFormat;

public class Transaction implements Serializable {
    @DocumentId
    private String id;
    private String uid, groupId;
    Timestamp date;
    private int value;

    public Transaction(){}

    public Transaction(String uid, String groupId, Timestamp time, int value) {
        this.uid = uid;
        this.groupId = groupId;
        this.date = time;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public String getGroupId() {
        return groupId;
    }

    public Timestamp getDate() {
        return date;
    }

    public String getStringDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        return format.format(date.toDate());
    }

    public int getValue() {
        return value;
    }
}