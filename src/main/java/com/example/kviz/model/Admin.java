package com.example.kviz.model;

import jakarta.persistence.*;

@Entity
public class Admin {
    @Column(nullable=false)
    String username;
    @Column(nullable=false)
    String password;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
