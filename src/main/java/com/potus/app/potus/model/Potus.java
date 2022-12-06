package com.potus.app.potus.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.potus.app.exception.ResourceNotFoundException;
import com.potus.app.potus.repository.ModifierRepository;
import com.potus.app.potus.service.ModifierService;
import com.potus.app.potus.service.PotusService;
import com.potus.app.potus.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.*;

import static com.potus.app.potus.utils.EventsUtils.getStateValue;
import static com.potus.app.potus.utils.PotusExceptionMessages.POTUS_MODIFIER_DOES_NOT_EXISTS;


@Entity
@Table(name="potus")
public class Potus {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @NotNull
    @NotBlank
    private String name;

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

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
    @MapKeyEnumerated(EnumType.STRING)
    @JoinColumn(name = "potus_id")
    private Map<Actions, PotusAction> actions;

    private Integer currencyMultiplier;

    private Integer permanentBonus;
    private Integer eventBonus;
    private Integer festivityBonus;

    private Integer waterRecovery;

    private Long pruningMaxCurrency;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "buff_id")
    @JsonIgnore
    private Set<PotusModifier> buffs;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "debuff_id")
    @JsonIgnore
    private Set<PotusModifier> debuffs;

    boolean ignored;

    private String state;

    public Potus() {
    }

    public Potus(String name) {
        initialize(name);
    }

    public void initialize(String name){
        Date now = new Date();

        this.name = name;
        this.health = 75;//this.health = PotusUtils.MAX_HEALTH;
        this.waterLevel = 0;//this.waterLevel = PotusUtils.MAX_WATER_LEVEL;
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

    public void initializeBuffs(Set<PotusModifier> buffs){
        this.buffs = buffs;
    }

    public String getName() {
        return name;
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

    public Set<PotusModifier> getBuffs() {
        return buffs;
    }

    public PotusModifier getBuff(String name){
       return buffs.stream().filter(buff ->buff.getModifier().getName().equals(name)).findFirst()
               .orElseThrow(() -> new ResourceNotFoundException(POTUS_MODIFIER_DOES_NOT_EXISTS));
    }

    public void setBuffs(Set<PotusModifier> buffs) {
        this.buffs = buffs;
    }

    public Set<PotusModifier> getDebuffs() {
        return debuffs;
    }

    public void setDebuffs(Set<PotusModifier> debuffs) {
        this.debuffs = debuffs;
    }

    public void setName(String name) {
        this.name = name;
    }
}

