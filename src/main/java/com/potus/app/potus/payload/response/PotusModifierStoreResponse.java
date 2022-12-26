package com.potus.app.potus.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.potus.app.potus.model.ModifierEffectType;
import com.potus.app.potus.model.PotusModifier;

import static com.potus.app.potus.utils.ModifierUtils.getCurrentPrice;
import static com.potus.app.potus.utils.ModifierUtils.getCurrentValue;

public class PotusModifierStoreResponse {

    private String name;
    private Double value;
    private ModifierEffectType type;
    @JsonProperty("next_value")
    private Double nextValue;
    private Integer level;
    private Double price;

    public PotusModifierStoreResponse() {
    }

    public PotusModifierStoreResponse(PotusModifier potusModifier) {
        this.level = potusModifier.getLevel();
        this.name = potusModifier.getModifier().getName();
        this.value = getCurrentValue(potusModifier.getModifier().getValue(),level);
        this.type = potusModifier.getModifier().getModifierEffectType();
        this.nextValue = getCurrentValue(potusModifier.getModifier().getValue(),level + 1);
        this.price = getCurrentPrice(potusModifier.getModifier().getPrice(), level);
    }
    public PotusModifierStoreResponse(String name, Double value, ModifierEffectType type, Double nextValue, Integer level, Double price) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.nextValue = nextValue;
        this.level = level;
        this.price = price;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public ModifierEffectType getType() {
        return type;
    }

    public void setType(ModifierEffectType type) {
        this.type = type;
    }

    public Double getNextValue() {
        return nextValue;
    }

    public void setNextValue(Double nextValue) {
        this.nextValue = nextValue;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
