package com.westee.api;

public enum DataStatus {
    OK(),
    DELETED(),
    PENDING(),
    PAID(),
    DELIVERED();
    public String getName(){
        return name().toLowerCase();
    }
}
