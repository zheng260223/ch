package com.workprocess.entity;

public enum CycleType {


    DAY("日计划", 1),
    WEEK("周计划", 2),
    MONTH("月计划", 3),
    QUARTER("季度计划", 4),
    YEAR("年计划", 5);

    private String name;
    private int value;

    CycleType(String name, int value) {
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
