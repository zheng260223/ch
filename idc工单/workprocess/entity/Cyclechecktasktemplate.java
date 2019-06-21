package com.workprocess.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cyclechecktasktemplate")
public class Cyclechecktasktemplate {
	@Id
	private String id;
	private String name;
	private long deviceType;
	private String serviceCycleId;
	private long enabled;
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
	public long getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(long deviceType) {
		this.deviceType = deviceType;
	}

	@Column
	public String getServiceCycleId() {
		return serviceCycleId;
	}

	public void setServiceCycleId(String serviceCycleId) {
		this.serviceCycleId = serviceCycleId;
	}

	@Column
	public long getEnabled() {
		return enabled;
	}

	public void setEnabled(long enabled) {
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
