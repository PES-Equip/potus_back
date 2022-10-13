package com.potus.app.airquality.model;


import javax.persistence.*;
import java.util.Map;

@Entity
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    private Regions name;

    private Double latitude;

    private Double length;

    @OneToMany(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private Map<Gases,GasRegistry> registry;

    public Region() {}

    public Region(Regions name, Double latitude, Double lenght, Map<Gases, GasRegistry> registry) {
        this.name = name;
        this.latitude = latitude;
        this.length = lenght;
        this.registry = registry;
    }

    public Regions getName() {
        return name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Map<Gases, GasRegistry> getRegistry() {
        return registry;
    }

    public void setRegistry(Map<Gases, GasRegistry> registry) {
        this.registry = registry;
    }
}
