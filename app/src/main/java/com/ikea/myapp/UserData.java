package com.ikea.myapp;

public class UserData {
    public String fullName, email, birthday;
    public UserData() {

    }

    public UserData(String fullName, String birthday, String email) {
        this.fullName = fullName;
        this.birthday = birthday;
        this.email = email;
    }
}
