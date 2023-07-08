package com.example.todolist;

import java.io.Serializable;

public class Task implements Serializable {
    private int id;
    private String title;
    private int completed;

    public Task(int id, String title, int completed) {
        this.id = id;
        this.title = title;
        this.completed = completed;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        boolean check = false;
       if(completed != 0){
           check = true;
       }
        return check;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public void setCompleted(boolean completed) {
        int complete;
        if(completed){
            complete = 1;
        }else{
            complete = 0;
        }
        this.completed = complete;
    }
}
