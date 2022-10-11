package com.potus.app.airquality.model;


import javax.persistence.*;

@Entity
@Table(name = "GasRegistries")
public class GasRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gasname")
    private Gases name;

    private Double value;

    private Units unit;

    public GasRegistry(){}

    public GasRegistry(Gases name, Double value, Units unit) {
        this.name = name;
        this.value = value;
        this.unit = unit;
    }

    public Gases getName() {
        return name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Units getUnit() {
        return unit;
    }

    public void setUnit(Units unit) {
        this.unit = unit;
    }
}
