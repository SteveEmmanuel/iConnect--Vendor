package com.steveatw.iconnectvendor;


public class Customer {
    String name;
    String email;
    String phone_number;
    String firebase_token;

    public Customer() {
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone_number;
    }

    public String getFirebase_token() {
        return firebase_token;
    }
}