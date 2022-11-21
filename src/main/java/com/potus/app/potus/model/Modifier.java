package com.potus.app.potus.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name="modifiers")
public class Modifier {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank
    private String name;

    @OneToMany(mappedBy="modifier",orphanRemoval = true)
    private Set<PotusModifier> potusModifiers;

    private ModifierType type;

    private Double value;

    private Double price;

    boolean buff;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public ModifierType getType() { return type; }

    public void setType(ModifierType type) { this.type = type; }

    public boolean isBuff() { return buff; }

    public void setBuff(boolean buff) { this.buff = buff; }

    public Double getValue() { return value; }

    public void setValue(Double value) { this.value = value; }

    public Double getPrice() { return price; }

    public void setPrice(Double price) { this.price = price; }
}
