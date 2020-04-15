package com.westee.wxshop.entity;

public enum DataStatus {
//    public static final String DELETE_STATUS = "deleted";
    OK(),
    DELETED();

    public String getName(){
        return name().toLowerCase();
    }
}