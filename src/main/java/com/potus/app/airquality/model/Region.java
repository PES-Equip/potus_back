package com.potus.app.airquality.model;


import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "AirQuality")
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double latitude;

    private Double lenght;

    @OneToMany(cascade = CascadeType.ALL)
    @MapKeyColumn (name="gasname")
    private Map<Gases,GasRegistry> registry;

    public Region() {}

    public Region(String name, Double latitude, Double lenght, Map<Gases, GasRegistry> registry) {
        this.name = name;
        this.latitude = latitude;
        this.lenght = lenght;
        this.registry = registry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLenght() {
        return lenght;
    }

    public void setLenght(Double lenght) {
        this.lenght = lenght;
    }

    public Map<Gases, GasRegistry> getRegistry() {
        return registry;
    }

    public void setRegistry(Map<Gases, GasRegistry> registry) {
        this.registry = registry;
    }
}
