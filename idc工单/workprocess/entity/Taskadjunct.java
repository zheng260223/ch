package com.workprocess.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "taskadjunct")
public class Taskadjunct {
  @Id
  private String id;
  private String workTaskId;
  private String fileName;
  private String fileExtName;
  private String filePath;
  private String url;
  private java.sql.Timestamp createTime;

  @Id
  @Column
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Column
  public String getWorkTaskId() {
    return workTaskId;
  }

  public void setWorkTaskId(String workTaskId) {
    this.workTaskId = workTaskId;
  }

  @Column
  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  @Column
  public String getFileExtName() {
    return fileExtName;
  }

  public void setFileExtName(String fileExtName) {
    this.fileExtName = fileExtName;
  }

  @Column
  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  @Column
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Column
  public java.sql.Timestamp getCreateTime() {
    return createTime;
  }

  public void setCreateTime(java.sql.Timestamp createTime) {
    this.createTime = createTime;
  }

}
