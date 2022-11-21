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

    @OneToOne
    @JoinColumn(name="potus_id", referencedColumnName = "id")
    private Potus potus;

    @ManyToOne(fetch = FetchType.LAZY)
    private Modifier modifier;

    private Integer level;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Potus getPotus() { return potus; }

    public void setPotus(Potus potus) { this.potus = potus; }

    public Modifier getModifier() { return modifier; }

    public void setModifier(Modifier modifier) { this.modifier = modifier; }

    public Integer getLevel() { return level; }

    public void setLevel(Integer level) { this.level = level; }
}
