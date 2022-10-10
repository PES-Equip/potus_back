package com.potus.app.potus.model;

import com.potus.app.potus.utils.*;
import javax.persistence.*;


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

    private Boolean infested;

    private Boolean alive;


    private int usrid;

    public Potus() {
        this.health = PotusUtils.MAX_HEALTH;
        this.waterLevel = PotusUtils.MAX_WATER_LEVEL;
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

    public Boolean getAlive() {
        return alive;
    }
}

