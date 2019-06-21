package com.workprocess.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cyclechecktaskitemtemplate")
public class Cyclechecktaskitemtemplate {
	@Id
	private String id;
	private long serialnumber;
	private String item;
	private String standard;
	private long enabled;
	private String templateId;

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
	public long getEnabled() {
		return enabled;
	}

	public void setEnabled(long enabled) {
		this.enabled = enabled;
	}

	@Column
	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

}
