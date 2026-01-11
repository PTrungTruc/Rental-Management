package com.example.rental_management.models;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;

public class Group implements Serializable {
    @DocumentId
    private String id;
    private String name, address, description, rules, ownerId, price, groupId;

    public Group(){}

    public Group(String name, String address, String description, String rules, String ownerId, String price) {
        this.name = name;
        this.address = address;
        this.description = description;
        this.rules = rules;
        this.ownerId = ownerId;
        this.price = price;
        this.groupId = "";
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}