package com.nukkadshops.mark03.data.models;

import com.google.gson.annotations.SerializedName;

public class CancelRequest {

    @SerializedName("StoreID")
    public String sid;

    @SerializedName("Clientid")
    public int cid;

    @SerializedName("MerchantID")
    public int mid;

    @SerializedName("SecurityToken")
    public String stk;

    @SerializedName("PlutusTransactionReferenceID")
    public long ptr;

    @SerializedName("Amount")
    public String amt;

    @SerializedName("TakeToHomeScreen")
    public boolean home;

    public CancelRequest(String sid, int cid, int mid, String stk,
                         long ptr, String amt, boolean home) {

        this.sid = sid;
        this.cid = cid;
        this.mid = mid;
        this.stk = stk;
        this.ptr = ptr;
        this.amt = amt;
        this.home = home;
    }
}
