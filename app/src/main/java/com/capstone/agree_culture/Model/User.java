package com.capstone.agree_culture.Model;

import java.util.Date;

public class User {

    private String full_name;
    private String role;
    private String address;
    private String city;
    private String phone_number;
    private Date created_at;

    public User(){}

    public User(String full_name, String role, String address, String city, String phone_number, Date created_at){
        this.full_name = full_name;
        this.role = role;
        this.address = address;
        this.city = city;
        this.phone_number = phone_number;
        this.created_at = created_at;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
