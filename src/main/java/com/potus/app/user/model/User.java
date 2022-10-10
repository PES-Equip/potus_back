package com.potus.app.user.model;

import com.potus.app.potus.model.Potus;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String username;

    @OneToOne(cascade = CascadeType.ALL )
    @JoinColumn(name = "usrid")
    private Potus potus;

    public User(){}



    public User(String email, String username){
        this.email = email;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public Potus getPotus() {
        return potus;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public void setPotus(Potus potus){
        this.potus = potus;
    }

    public UserStatus getStatus(){
        if (username == null || potus == null)
            return UserStatus.NEW;

        return  UserStatus.NORMAL;
    }

}
