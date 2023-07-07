package com.asizdurden.kullanici_giris.Model;

import java.util.List;

public class User {
    private String email;
    private List<ToDoModel> taskList;

    public User(String email, List<ToDoModel> taskList) {
        this.email = email;
        this.taskList = taskList;
    }

    public String getEmail() {
        return email;
    }

    public List<ToDoModel> getTaskList() {
        return taskList;
    }
}
