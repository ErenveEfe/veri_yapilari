package com.library.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String id;
    private String password;
    private UserType userType;
    private List<String> readIsbns;

    public User(String id, String password, UserType userType) {
        this.id = id;
        this.password = password;
        this.userType = userType;
        this.readIsbns = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserType getUserType() { return userType; }
    public void setUserType(UserType userType) { this.userType = userType; }

    public List<String> getReadIsbns() { return readIsbns; }
    public void addReadIsbn(String isbn) { 
        if (!readIsbns.contains(isbn)) {
            readIsbns.add(isbn);
        }
    }
}
