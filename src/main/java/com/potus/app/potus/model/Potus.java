package com.potus.app.potus.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.potus.app.potus.utils.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.potus.app.potus.utils.EventsUtils.getStateValue;


@Entity
@Table(name="potus")
public class Potus {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    //@Column(columnDefinition = "int check(health >= 0 and health <= 100")
    private Integer health;

    //@Column(columnDefinition = "int check(waterLevel >= 0 and waterLevel <= 100")
    private Integer waterLevel;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified;
    private Boolean infested;

    private Boolean alive;

    @OneToMany(fetch = FetchType.EAGER)
    @MapKeyEnumerated(EnumType.STRING)
    @JoinColumn(name = "potus_id")
    private Map<Actions, PotusAction> actions;

    private Integer currencyMultiplier;

    private Integer permanentBonus;
    private Integer eventBonus;
    private Integer festivityBonus;

    private Integer waterRecovery;

    private Long pruningMaxCurrency;

    boolean ignored;

    private String state;

    public Potus() {

        Date now = new Date();

        this.health = PotusUtils.MAX_HEALTH;
        this.waterLevel = PotusUtils.MAX_WATER_LEVEL;
        this.createdDate = now;
        this.lastModified = now;
        this.alive = true;
        this.infested = false;
        this.actions = new HashMap<>();
        this.state = States.DEFAULT.toString();
        this.currencyMultiplier = 1;
        this.pruningMaxCurrency = 120L;
        this.permanentBonus = 1;
        this.eventBonus = 0;
        this.festivityBonus = 0;
        this.waterRecovery = 10;
        this.ignored = false;
    }

    public Long getId() {
        return id;
    }

    public Integer getHealth() {
        return health;
    }

    public Integer getWaterLevel() {
        return waterLevel;
    }

    public Boolean getInfested() {
        return infested;
    }

    public Boolean isAlive() {
        return alive;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public Map<Actions, PotusAction> getActions() {
        return actions;
    }

    public void setActions(Map<Actions, PotusAction> actions) {
        this.actions = actions;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setHealth(Integer health) {
        this.health = health;
    }

    public void setWaterLevel(Integer waterLevel) {
        this.waterLevel = waterLevel;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public void setInfested(Boolean infested) {
        this.infested = infested;
    }

    public void setAlive(Boolean alive) {
        this.alive = alive;
    }

    public PotusAction getAction(Actions action){
        return actions.get(action);
    }

    public GasesAndStates getState() { return getStateValue(state); }

    public void setState(GasesAndStates state) { this.state = state.toString(); }

    public Integer getCurrencyMultiplier() { return currencyMultiplier; }

    public void setCurrencyMultiplier(Integer currencyMultiplier) { this.currencyMultiplier = currencyMultiplier; }

    public Integer getPermanentBonus() { return permanentBonus; }

    public void setPermanentBonus(Integer permanentBonus) { this.permanentBonus = permanentBonus; }

    public Integer getEventBonus() { return eventBonus; }

    public void setEventBonus(Integer eventBonus) { this.eventBonus = eventBonus; }

    public Integer getFestivityBonus() { return festivityBonus; }

    public void setFestivityBonus(Integer festivityBonus) { this.festivityBonus = festivityBonus; }

    public Long getPruningMaxCurrency() { return pruningMaxCurrency; }

    public void setPruningMaxCurrency(Long pruningMaxCurrency) { this.pruningMaxCurrency = pruningMaxCurrency; }

    public Integer getWaterRecovery() { return waterRecovery; }

    public void setWaterRecovery(Integer waterRecovery) { this.waterRecovery = waterRecovery; }

    public boolean getIgnored() { return ignored; }

    public void setIgnored(boolean ignored) { this.ignored = ignored; }


    public Map<CurrencyGenerators, Integer> getCurrencyGenerators() {
        Map<CurrencyGenerators, Integer> result = new HashMap<>();

        result.put(CurrencyGenerators.PERMANENT_BONUS, permanentBonus);
        result.put(CurrencyGenerators.EVENT_BONUS, eventBonus);
        result.put(CurrencyGenerators.FESTIVITY_BONUS, festivityBonus);

        return result;
    }


}

