package com.potus.app.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.potus.app.user.utils.UserUtils.calculateTrophyNextLevel;


@Entity
@Table(name="user_trophies")
public class UserTrophy {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;



    @ManyToOne
    @JoinColumn(name = "trophy_id")
    private Trophy trophy;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private int level;

    private int current;

    @ElementCollection
    @JsonProperty("dates")
    private List<Date> levelDates;

    private Date updatedDate;


    public UserTrophy() {
    }

    public UserTrophy(Trophy trophy, User user) {
        this.trophy = trophy;
        this.user = user;
        this.level = 1;
        this.current = 0;
        this.levelDates = new ArrayList<>();
        this.updatedDate = new Date();
    }

    public int getLevel() {
        return level;
    }

    public void upgradeLevel(){
        ++level;
        levelDates.add(new Date());
    }
    public void setLevel(int level) {
        this.level = level;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public List<Date> getLevelDates() {
        return levelDates;
    }

    public void setLevelDates(List<Date> levelDates) {
        this.levelDates = levelDates;
    }

    @JsonProperty("next_level")
    public int getNextLevel(){
        return calculateTrophyNextLevel(trophy.getBase(), level);
    }

    @JsonIgnore
    public boolean isNext(){
        return current == getNextLevel();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Trophy getTrophy() {
        return trophy;
    }

    public void setTrophy(Trophy trophy) {
        this.trophy = trophy;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
