package com.potus.app.user.model;

import javax.persistence.*;

@Entity
@Table(name="Users")
public class User {

    private @Id String id;

    private String email;
    @Column(unique = true)
    private String username;

    public User(){}

    public User(String id, String email, String username){
        this.id = id;
        this.email = email;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

}
