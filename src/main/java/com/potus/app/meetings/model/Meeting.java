package com.potus.app.meetings.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.potus.app.airquality.model.Region;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="meetings")
public class Meeting {  //filter by tags_categor_es (concerts,exposicions, teatre)

    @Id
    private Long id; //codi (needs to parse)

    @JsonProperty("start_date")
    private Date startDate; //data_inici (needs to parse)

    @JsonProperty("end_date")
    private Date endDate; //data_fi (needs to parse)

    @JsonIgnoreProperties(value = {"registry"})
    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region; //calculated by latitud and longitud

    private String address; //adre_a

    private String city; // comarca_i_municipi[2]
    private String title;  //denominaci

    private String subtitle; //subt_tol

    private String url;

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Meeting(Long id, Date startDate, Date endDate, Region region, String address, String city, String title, String subtitle, String url) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.region = region;
        this.address = address;
        this.city = city;
        this.title = title;
        this.subtitle = subtitle;
        this.url = url;
    }

    public Meeting() {
    }

    public Long getId() {
        return id;
    }


    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
