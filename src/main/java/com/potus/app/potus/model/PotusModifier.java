package com.potus.app.potus.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "potus_modifiers")
public class PotusModifier {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @ManyToOne
    @JoinColumn(name="potus_id", referencedColumnName = "id")
    @JsonIgnore
    private Potus potus;

    @ManyToOne
    @JoinColumn(name="modifier_id", referencedColumnName = "id")
    private Modifier modifier;

    private Integer level;



    public PotusModifier() {}

    public PotusModifier(Potus potus, Modifier modifier, Integer level) {
        this.potus = potus;
        this.modifier = modifier;
        this.level = level;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Potus getPotus() { return potus; }

    public void setPotus(Potus potus) { this.potus = potus; }

    public Modifier getModifier() { return modifier; }

    public void setModifier(Modifier modifier) { this.modifier = modifier; }

    public Integer getLevel() { return level; }

    public void setLevel(Integer level) { this.level = level; }
}
