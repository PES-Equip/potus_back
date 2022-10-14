package com.potus.app.potus.model;

import com.potus.app.potus.utils.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name="potus")
public class Potus {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@Column(columnDefinition = "int check(health >= 0 and health <= 100")
    private Integer health;

    //@Column(columnDefinition = "int check(waterLevel >= 0 and waterLevel <= 100")
    private Integer waterLevel;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified;
    private Boolean infested;

    private Boolean alive;

    public Potus() {
        this.health = PotusUtils.MAX_HEALTH;
        this.waterLevel = PotusUtils.MAX_WATER_LEVEL;
        this.createdDate = new Date();
        this.lastModified = new Date();
        this.alive = true;
        this.infested = false;
    }

    public Long getId() {
        return id;
    }

    public Integer getHealth() {
        return health;
    }

    public Integer getWaterLevel() {
        return waterLevel;
    }

    public Boolean getInfested() {
        return infested;
    }

    public Boolean isAlive() {
        return alive;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setHealth(Integer health) {
        this.health = health;
    }

    public void setWaterLevel(Integer waterLevel) {
        this.waterLevel = waterLevel;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public void setInfested(Boolean infested) {
        this.infested = infested;
    }

    public void setAlive(Boolean alive) {
        this.alive = alive;
    }
}

