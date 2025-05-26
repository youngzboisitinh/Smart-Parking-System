package com.example.smartparking.Models;

public class LoginResponse {
    private String message;
    private String token;
    private User user;

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public class User {
        private String id;
        private String name;
        private String email;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
    }
}

