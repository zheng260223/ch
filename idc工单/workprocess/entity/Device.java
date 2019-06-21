package com.workprocess.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "devices")
public class Device {
    @Id
    private String id;
    private String name;
    private Integer serialnumber;
    private Integer deviceType;

    @Column
    public Integer getDeviceType() {
        return deviceType;
    }

    @Column
    public Integer getSerialnumber() {
        return serialnumber;
    }

    @Id
    @Column
    public String getId() {
        return id;
    }

    @Column
    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSerialnumber(Integer serialnumber) {
        this.serialnumber = serialnumber;
    }
}
