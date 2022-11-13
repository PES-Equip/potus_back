package com.potus.app.garden.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.potus.app.user.model.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "garden_requests", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"garden_id", "user_id"})
})
public class GardenRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "garden_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Garden garden;

    @JsonIgnoreProperties({"potus", "currency", "id", "email", "status", "garden"})
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Enumerated(EnumType.STRING)
    private GardenRequestType type;


    public GardenRequest() {
    }

    public GardenRequest(Garden garden, User user, Date createdDate, GardenRequestType type) {
        this.garden = garden;
        this.user = user;
        this.createdDate = createdDate;
        this.type = type;
    }

    public Garden getGarden() {
        return garden;
    }

    public User getUser() {
        return user;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public GardenRequestType getType() {
        return type;
    }

}
