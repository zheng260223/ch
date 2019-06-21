package com.workprocess.entity;

public enum  WorkTaskStatus {
    DISPATCH("待派单",1),
    RECEIPT("待接单",2),
    HANDLE("处理中",3),
    AUDIT("待审核",4),
    COMPLETED("已完成",5);
    private String name;
    private int value;
    WorkTaskStatus(String name, int value){
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
