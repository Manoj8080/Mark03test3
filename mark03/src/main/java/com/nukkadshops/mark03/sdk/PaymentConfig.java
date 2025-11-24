package com.nukkadshops.mark03.sdk;

public class PaymentConfig {
    public final String baseUrl,storeId,securityToken,userId;
    public final int merchantId,clientId,timeout;
    public PaymentConfig(String baseUrl,int merchantId,int clientId,String storeId,String securityToken,String userId,int timeout){
        this.baseUrl=baseUrl;
        this.merchantId=merchantId;
        this.clientId=clientId;
        this.storeId=storeId;
        this.securityToken=securityToken;
        this.userId=userId;
        this.timeout=timeout;
    }
    public PaymentConfig(String baseUrl,int merchantId,int clientId,String storeId,String securityToken,String userId){
        this(baseUrl,merchantId,clientId,storeId,securityToken,userId,30);
    }
    public PaymentConfig(int merchantId,int clientId,String storeId,String securityToken,String userId){
        this("https://7f0901046751.ngrok-free.app/",merchantId,clientId,storeId,securityToken,userId,30);
    }
    public String getBaseUrl(){
        return baseUrl;
    }
    public int getMerchantId() {
        return merchantId;
    }
    public int getClientId() {
        return clientId;
    }
    public String getStoreId() {
        return storeId;
    }
    public String getSecurityToken() {
        return securityToken;
    }
    public String getUserId() {
        return userId;
    }
    public int getTimeoutInSeconds() {
        return timeout;
    }
}
