package com.potus.app.garden.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.potus.app.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.potus.app.garden.model.GardenRole.OWNER;
import static com.potus.app.garden.utils.GardenUtils.GARDEN_MAX_SIZE;

@Entity
@Table(name="gardens")
public class Garden {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(unique = true)
    @Size(min=3, max=20)
    private String name;

    private String Description;

    @OneToMany(orphanRemoval = true)
    @Size(max=GARDEN_MAX_SIZE)
    @JsonIgnore
    private Set<GardenMember> members;

    public Garden(String name) {
        this.name = name;
    }

    public Garden() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Set<GardenMember> getMembers() {
        return members;
    }

    public void setMembers(Set<GardenMember> members) {
        this.members = members;
    }

    @JsonProperty("members_num")
    public int getMembersSize(){
        return members.size();
    }
}
