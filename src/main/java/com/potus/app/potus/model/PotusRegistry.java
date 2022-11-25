package com.potus.app.potus.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.potus.app.user.model.User;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "potus_registry", uniqueConstraints = { @UniqueConstraint(columnNames = {"user_id", "name"})})
public class PotusRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @NotNull
    @NotBlank
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;


    @Temporal(TemporalType.TIMESTAMP)
    private Date deathDate;

    public PotusRegistry() {
    }

    public PotusRegistry(String name, User user, Date createdDate, Date deathDate) {
        this.name = name;
        this.user = user;
        this.createdDate = createdDate;
        this.deathDate = deathDate;
    }



    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(Date deathDate) {
        this.deathDate = deathDate;
    }
}
