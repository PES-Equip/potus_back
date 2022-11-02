package com.potus.app.garden.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.potus.app.user.model.User;

import javax.persistence.*;

@Entity
@Table(name="garden_members")
public class GardenMember {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @ManyToOne
    @JoinColumn(name="garden_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"members"})
    private Garden garden;

    @JsonIgnoreProperties({"potus", "currency", "id", "email", "status", "garden"})
    @OneToOne
    @JoinColumn(name="user_id", referencedColumnName = "id")
    private User user;

    private GardenRole role;

    public GardenMember() {
    }

    public GardenMember(Garden garden, User user, GardenRole role) {
        this.garden = garden;
        this.user = user;
        this.role = role;
    }

    public Garden getGarden() {
        return garden;
    }

    public User getUser() {
        return user;
    }

    public GardenRole getRole() {
        return role;
    }

    public void setRole(GardenRole role) {
        this.role = role;
    }
}
