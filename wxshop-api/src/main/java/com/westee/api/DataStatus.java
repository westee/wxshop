package com.westee.api;

public enum DataStatus {
    OK(),
    DELETED(),
    PENDING(),
    PAID(),
    DELIVERED(),
    RECEIVED();


    public static DataStatus fromStatus(String name) {
        try{
            if(name == null) return  null;
            return DataStatus.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e){
            return null;
        }
    }

    public String getName(){
        return name().toLowerCase();
    }
}
