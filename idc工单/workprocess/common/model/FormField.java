package com.workprocess.common.model;

import org.activiti.engine.form.FormProperty;

import java.io.Serializable;

/**
 * 表单字段属性
 */
public class FormField implements Serializable {
    /**
     * 字段
     */
    private String id;
    /**
     * 字段显示名称
     */
    private String name;
    /**
     * 是否可读
     */
    private boolean readable;
    /**
     * 是否可写
     */
    private boolean writable;
    /**
     * 是否必须（暂时没用留着）
     */
    private boolean required;

    public static FormField getFormField(FormProperty property){
        FormField formField = new FormField();
        formField.setId(property.getId());
        formField.setName(property.getName());
        formField.setReadable(property.isReadable());
        formField.setWritable(property.isWritable());
        formField.setRequired(property.isRequired());
        return formField;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReadable() {
        return readable;
    }

    public void setReadable(boolean readable) {
        this.readable = readable;
    }

    public boolean isWritable() {
        return writable;
    }

    public void setWritable(boolean writable) {
        this.writable = writable;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
