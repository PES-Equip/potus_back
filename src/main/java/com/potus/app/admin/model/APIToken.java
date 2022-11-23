package com.potus.app.admin.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

import static com.potus.app.admin.utils.AdminUtils.APITOKEN_TYPE;

@Entity
@Table(name="api_tokens")
public class APIToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String token;

    @Column(unique = true)
    private String name;

    public APIToken() {
    }

    public APIToken(String token, String name) {
        this.token = token;
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    @JsonProperty("token_type")
    public String getTokenType(){
        return APITOKEN_TYPE;
    }
}
