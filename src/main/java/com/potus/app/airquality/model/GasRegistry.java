package com.potus.app.airquality.model;


import javax.persistence.*;

@Entity
@Table(name="GASREGISTRIES")
public class GasRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Gases name;

    private Double gvalue;

    private Units unit;

    @Enumerated(EnumType.STRING)
    private DangerLevel dangerLevel;

    public GasRegistry(){}

    public GasRegistry(Gases name, Double gvalue, Units unit) {
        this.name = name;
        this.gvalue = gvalue;
        this.unit = unit;
        this.dangerLevel = DangerLevel.NoDanger;
    }

    public Gases getName() {
        return name;
    }

    public Double getValue() {
        return gvalue;
    }

    public void setValue(Double gvalue) {
        this.gvalue = gvalue;
    }

    public DangerLevel getDangerLevel() {
        return dangerLevel;
    }

    public void setDangerLevel(DangerLevel dangerLevel) {
        this.dangerLevel = dangerLevel;
    }

    public Units getUnit() {
        return unit;
    }

    public void setUnit(Units unit) {
        this.unit = unit;
    }
}
