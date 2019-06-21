package com.workprocess.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "emergencytaskitem")
public class Emergencytaskitem {

    @Id
    private String id;
    private long serialnumber;
    private String item;
    private String standard;
    private String conditions;
    private String execution;
    private long isNormal;
    private java.sql.Timestamp checkedTime;
    private String templateId;
    private String templateItemId;
    private String workTaskId;

    @Id
    @Column
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column
    public long getSerialnumber() {
        return serialnumber;
    }

    public void setSerialnumber(long serialnumber) {
        this.serialnumber = serialnumber;
    }

    @Column
    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    @Column
    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    @Column
    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    @Column
    public String getExecution() {
        return execution;
    }

    public void setExecution(String execution) {
        this.execution = execution;
    }

    @Column
    public long getIsNormal() {
        return isNormal;
    }

    public void setIsNormal(long isNormal) {
        this.isNormal = isNormal;
    }

    @Column
    public java.sql.Timestamp getCheckedTime() {
        return checkedTime;
    }

    public void setCheckedTime(java.sql.Timestamp checkedTime) {
        this.checkedTime = checkedTime;
    }

    @Column
    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    @Column
    public String getTemplateItemId() {
        return templateItemId;
    }

    public void setTemplateItemId(String templateItemId) {
        this.templateItemId = templateItemId;
    }

    @Column
    public String getWorkTaskId() {
        return workTaskId;
    }

    public void setWorkTaskId(String workTaskId) {
        this.workTaskId = workTaskId;
    }

}
