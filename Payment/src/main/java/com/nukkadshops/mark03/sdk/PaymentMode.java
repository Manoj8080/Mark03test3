package com.nukkadshops.mark03.sdk;

public enum PaymentMode{
    UPI("10"),
    CARD("1");
    private final String c;
    PaymentMode(String c){
        this.c=c;
    }
    public String getCode(){
        return c;
    }
}
