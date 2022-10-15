package com.potus.app.potus.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name="potusActions")
public class PotusAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Enumerated(EnumType.STRING)
    @JsonIgnore
    private Actions name;


    @Temporal(TemporalType.TIMESTAMP)
    private Date lastTime;

    public PotusAction(){}

    public PotusAction(Actions action){
        this.name = action;
        this.lastTime = new Date();
    }

    public Actions getName() {
        return name;
    }


    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

}
