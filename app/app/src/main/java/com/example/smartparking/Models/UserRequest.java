package com.example.smartparking.Models;

public class UserRequest {
    private String email;
    private String password;


    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
