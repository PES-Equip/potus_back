package com.potus.app.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.potus.app.garden.model.Garden;
import com.potus.app.garden.model.GardenMember;
import com.potus.app.meetings.model.Meeting;
import com.potus.app.potus.model.Potus;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
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

    private Boolean isAdmin;


    @Column(columnDefinition = "int check(currency >= 0)")
    private Integer currency;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    private Potus potus;

    @OneToOne(mappedBy = "user")
    @JsonIgnoreProperties({"user"})
    @JsonProperty("garden_info")
    private GardenMember garden;



    @OneToMany(fetch = FetchType.EAGER)
    @JsonProperty("meetings")
    private Set<Meeting> meetings;

    public User(){}



    public User(String email, String username){
        this.email = email;
        this.username = username;
        this.currency = 0;
        isAdmin = false;
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

        return  UserStatus.CONFIRMED;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Integer getCurrency() {
        return currency;
    }

    public void setCurrency(Integer currency) {
        this.currency = currency;
    }

    public GardenMember getGarden() {
        return garden;
    }

    public void setGarden(GardenMember garden) {
        this.garden = garden;
    }

    public Set<Meeting> getMeetings() {
        return meetings;
    }

    public void addMeeting(Meeting meeting) {
        this.meetings.add(meeting);
    }

    public void deleteMeeting(Meeting meeting) {
        meetings.remove(meeting);

    }
}
