package com.potus.app.garden.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.potus.app.user.model.User;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="garden_members")
public class GardenMember {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @ManyToOne
    @JoinColumn(name="garden_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"members", "members_num"})
    private Garden garden;

    @JsonIgnoreProperties({"potus", "currency", "id", "email", "status", "garden"})
    @OneToOne
    @JoinColumn(name="user_id", referencedColumnName = "id")
    private User user;

    private GardenRole role;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;


    public GardenMember() {
    }

    public GardenMember(Garden garden, User user, GardenRole role) {
        this.garden = garden;
        this.user = user;
        this.role = role;
        this.createdDate = new Date();
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
