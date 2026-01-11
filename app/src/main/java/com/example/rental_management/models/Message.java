package com.example.rental_management.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;
import java.text.SimpleDateFormat;

public class Message implements Serializable {
    @DocumentId
    private String id;

    private String content;
    private Timestamp date;
    private String senderId;

    public Message(){}

    public Message(String content, Timestamp date, String senderId) {
        this.content = content;
        this.date = date;
        this.senderId = senderId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getStringDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        return format.format(date.toDate());
    }
}
