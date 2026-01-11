package com.example.rental_management.models;

public class Event {

    private int id;
    private String title;
    private String date;
    private String time;
    private String month;
    private String year;
    private String duration;
    private boolean isNotify;
    private boolean isRecurring;
    private String recurringPeriod;
    private String note;
    private String groupid;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isNotify() {
        return isNotify;
    }

    public void setNotify(boolean notify) {
        isNotify = notify;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public String getRecurringPeriod() {
        return recurringPeriod;
    }

    public void setRecurringPeriod(String recurringPeriod) {
        this.recurringPeriod = recurringPeriod;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

//    @Override
//    public String toString() {
//        return title + '\n' +
//                "DATE=" + date + '\n' +
//                "TIME=" + time + '\n' +
//                "NOTE=" + note + '\n';
//    }


    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", month='" + month + '\'' +
                ", year='" + year + '\'' +
                ", duration='" + duration + '\'' +
                ", isNotify=" + isNotify +
                ", isRecurring=" + isRecurring +
                ", recurringPeriod='" + recurringPeriod + '\'' +
                ", note='" + note + '\'' +
                ", groupid='" + groupid + '\'' +
                '}';
    }
}