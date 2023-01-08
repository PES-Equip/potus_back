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

    private ModifierEffectType modifierEffectType;


    private Double val;

    private Double price;

    private ModifierType modifierType;


    public Modifier() {}

    public Modifier(String name, ModifierEffectType modifierEffectType, Double val, Double price, ModifierType modifierType) {
        this.name = name;
        this.modifierEffectType = modifierEffectType;
        this.val = val;
        this.price = price;
        this.modifierType = modifierType;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public ModifierEffectType getModifierEffectType() { return modifierEffectType; }

    public void setModifierEffectType(ModifierEffectType modifierEffectType) { this.modifierEffectType = modifierEffectType; }

    public ModifierType getModifierType() { return modifierType; }

    public void setBuff(ModifierType modifierType) { this.modifierType = modifierType; }

    public Double getPrice() { return price; }

    public void setPrice(Double price) { this.price = price; }

    public Double getValue() {
        return val;
    }

    public void setValue(Double val) {
        this.val = val;
    }

}
