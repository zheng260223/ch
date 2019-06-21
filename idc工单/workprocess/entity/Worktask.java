package com.workprocess.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "worktask")
public class Worktask {
    @Id
    private String id;
    private String taskName;
    private Integer priority;
    private String inspectorId;
    private Double longitude;
    private Double latitude;
    private String templateId;
    /**
     * 1=周期；2=维保；3=应急
     */
    private Integer templateType;
    private String deviceId;
    private Integer deviceType;
    private Integer status;
    private java.sql.Timestamp createTime;
    private String creatorId;
    private java.sql.Timestamp planTime;
    private java.sql.Timestamp finishTime;
    private String desription;
    private Integer originType;
    private Integer isNormal;
    private String didCondition;
    private String flowId;
    private Boolean deleted;
    private Integer serialnumber;

    @Id
    @Column
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getSerialnumber() {
        return serialnumber;
    }

    public void setSerialnumber(Integer serialnumber) {
        this.serialnumber = serialnumber;
    }

    @Column
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @Column
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Column
    public String getInspectorId() {
        return inspectorId;
    }

    public void setInspectorId(String inspectorId) {
        this.inspectorId = inspectorId;
    }

    @Column
    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Column
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @Column
    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    @Column
    public Integer getTemplateType() {
        return templateType;
    }

    public void setTemplateType(Integer templateType) {
        this.templateType = templateType;
    }

    @Column
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Column
    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }

    @Column
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Column
    public java.sql.Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(java.sql.Timestamp createTime) {
        this.createTime = createTime;
    }

    @Column
    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    @Column
    public java.sql.Timestamp getPlanTime() {
        return planTime;
    }

    public void setPlanTime(java.sql.Timestamp planTime) {
        this.planTime = planTime;
    }

    @Column
    public java.sql.Timestamp getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(java.sql.Timestamp finishTime) {
        this.finishTime = finishTime;
    }

    @Column
    public String getDesription() {
        return desription;
    }

    public void setDesription(String desription) {
        this.desription = desription;
    }

    @Column
    public Integer getOriginType() {
        return originType;
    }

    public void setOriginType(Integer originType) {
        this.originType = originType;
    }

    @Column
    public Integer getIsNormal() {
        return isNormal;
    }

    public void setIsNormal(Integer isNormal) {
        this.isNormal = isNormal;
    }

    @Column
    public String getDidCondition() {
        return didCondition;
    }

    public void setDidCondition(String didCondition) {
        this.didCondition = didCondition;
    }

    @Column
    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    @Column
    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

}
