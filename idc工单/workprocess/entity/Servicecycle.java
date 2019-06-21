package com.workprocess.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "servicecycle")
public class Servicecycle implements Serializable {
    @Id
    private String id;
    private String name;
    private Integer cycleType;
    private String startWeek;
    private String startMonths;
    private String startMonthsDays;
    private Integer startHour;
    private Boolean enabled;
    private String description;

    @Id
    @Column
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column
    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    @Column
    public Integer getCycleType() {
        return cycleType;
    }


    public void setCycleType(Integer cycleType) {
        this.cycleType = cycleType;
    }

    @Column
    public String getStartWeek() {
        return startWeek;
    }

    public void setStartWeek(String startWeek) {
        this.startWeek = startWeek;
    }


    @Column
    public String getStartMonthsDays() {
        return startMonthsDays;
    }

    public void setStartMonthsDays(String startMonthsDays) {
        this.startMonthsDays = startMonthsDays;
    }


    @Column
    public String getStartMonths() {
        return startMonths;
    }

    public void setStartMonths(String startMonths) {
        this.startMonths = startMonths;
    }


    @Column
    public Integer getStartHour() {
        return startHour;
    }

    public void setStartHour(Integer startHour) {
        this.startHour = startHour;
    }

    @Column
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Column
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
