package com.potus.app.potus.payload.request;



import com.potus.app.potus.model.Potus;
import org.springframework.data.util.Pair;

import javax.swing.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class PotusEventRequest {
    @NotNull()
    @NotEmpty()
    private Double latitude;
    private Double length;

    public void setValues(Double latitude, Double length) {
        this.latitude = latitude;
        this.length = length;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLength() {
        return length;
    }


}
