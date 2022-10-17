package com.potus.app.airquality.model;


import javax.persistence.*;
import java.util.Map;

@Entity
@Table(name="REGIONS")
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Regions name;

    private String code;

    private Double latitude;

    private Double length;

    @OneToMany(mappedBy = "region")
    private Map<Gases,GasRegistry> registry;

    public Region() {}

    public Region(Regions name, Double latitude, Double length, String code) {
        this.name = name;
        this.latitude = latitude;
        this.length = length;
        this.code = code;
    }

    public Regions getName() {
        return name;
    }

    public String getCode(){return code;}

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
