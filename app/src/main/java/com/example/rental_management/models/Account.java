package com.example.rental_management.models;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;

public class Account implements Serializable {
    @DocumentId
    private String id;
    private String img, name, pass, phone, age, hobby, job, habit, groupId;
    private float rating;
    private int count_rating = 0, point = 0;

    public Account(){}

    public Account(String name, String age, String phone, String job, String habit, String hobby, String pass) {
        this.img = "";
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.job = job;
        this.habit = habit;
        this.hobby = hobby;
        this.pass = pass;
    }

    public Account(String name, String age, String phone, String job, String habit, String hobby, String pass, String img) {
        this.img = img;
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.job = job;
        this.habit = habit;
        this.hobby = hobby;
        this.pass = pass;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getHabit() {
        return habit;
    }

    public void setHabit(String habit) {
        this.habit = habit;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void addRating(float rating) {
        this.rating = this.rating*this.count_rating + rating;
        this.count_rating += 1;
        this.rating /= this.count_rating;
    }

    public int getCount_rating() {
        return count_rating;
    }

    public void setCount_rating(int count_rating) {
        this.count_rating = count_rating;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "Account{" +
                "groupId='" + groupId + '\'' +
                ", habit='" + habit + '\'' +
                ", job='" + job + '\'' +
                ", hobby='" + hobby + '\'' +
                ", phone='" + phone + '\'' +
                ", pass='" + pass + '\'' +
                ", name='" + name + '\'' +
                ", img='" + img +
                '}';
    }
}
