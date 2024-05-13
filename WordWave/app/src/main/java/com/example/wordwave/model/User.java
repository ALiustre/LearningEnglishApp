package com.example.wordwave.model;

public class User {
    String photoUrl;
    String userName;
    String userId;
    String email;

    public User(String avatar, String userName, String userId, String email) {
        this.photoUrl = avatar;
        this.userName = userName;
        this.userId = userId;
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
