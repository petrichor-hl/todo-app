package com.example.bai4_todoapp.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

public class TodoItem implements Serializable {
    private String title;
    private Date createdDate;
    private String description;
    private boolean isDone;

    public TodoItem(String title, Date createdDate, String description, boolean isDone) {
        this.title = title;
        this.createdDate = createdDate;
        this.description = description;
        this.isDone = isDone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
