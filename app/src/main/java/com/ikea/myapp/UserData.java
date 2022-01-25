package com.ikea.myapp;

public class UserData {
    private String firstName, email;

    //    public String birthday;
    public UserData() {

    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    //    public UserData(String fullName, String birthday, String email) {
    public UserData(String firstName, String email) {
        this.firstName = firstName;
        //this.birthday = birthday;
        this.email = email;
    }
}
