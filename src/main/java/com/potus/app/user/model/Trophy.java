package com.potus.app.user.model;

import javax.persistence.*;


@Entity
@Table(name="trophies")
public class Trophy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int base;

    @Column(unique = true)
    private TrophyType name;


    public Trophy() {
    }

    public Trophy(int base, TrophyType name) {
        this.base = base;
        this.name = name;

    }


    public int getBase() {
        return base;
    }

    public void setBase(int base) {
        this.base = base;
    }

    public TrophyType getName() {
        return name;
    }

    public void setName(TrophyType name) {
        this.name = name;
    }

}
