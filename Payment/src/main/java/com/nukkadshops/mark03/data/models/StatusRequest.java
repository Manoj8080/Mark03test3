package com.nukkadshops.mark03.data.models;

import com.google.gson.annotations.SerializedName;

public class StatusRequest {

    @SerializedName("MerchantID")
    public int mid;

    @SerializedName("SecurityToken")
    public String stk;

    @SerializedName("StoreID")
    public String sid;

    @SerializedName("Clientid")
    public int cid;   // MUST match exact spelling: "Clientid"

    @SerializedName("PlutusTransactionReferenceID")
    public long ptr;

    public StatusRequest(int mid, String stk, String sid, int cid, long ptr) {
        this.mid = mid;
        this.stk = stk;
        this.sid = sid;
        this.cid = cid;
        this.ptr = ptr;
    }
}
