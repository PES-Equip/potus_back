package com.potus.app.potus.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="modifiers")
public class Modifier {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank
    private String name;

    private ModifierEffectType type;


    private Double val;

    private Double price;

    private boolean buff;


    public Modifier() {}

    public Modifier(String name, ModifierEffectType type, Double val, Double price, boolean buff) {
        this.name = name;
        this.type = type;
        this.val = val;
        this.price = price;
        this.buff = buff;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public ModifierEffectType getType() { return type; }

    public void setType(ModifierEffectType type) { this.type = type; }

    public boolean isBuff() { return buff; }

    public void setBuff(boolean buff) { this.buff = buff; }

    public Double getPrice() { return price; }

    public void setPrice(Double price) { this.price = price; }

    public Double getValue() {
        return val;
    }

    public void setValue(Double val) {
        this.val = val;
    }

}
