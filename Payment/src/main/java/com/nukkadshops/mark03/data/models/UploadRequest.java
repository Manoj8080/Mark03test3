package com.nukkadshops.mark03.data.models;

import com.google.gson.annotations.SerializedName;

public class UploadRequest {

    @SerializedName("TransactionNumber")
    public String tn;

    @SerializedName("SequenceNumber")
    public int sn;

    @SerializedName("AllowedPaymentMode")
    public String apm;

    @SerializedName("Amount")
    public String amt;

    @SerializedName("UserID")
    public String uid;

    @SerializedName("MerchantID")
    public int mid;

    @SerializedName("SecurityToken")
    public String stk;

    @SerializedName("StoreID")
    public String sid;

    @SerializedName("Clientid")  // EXACT match: lower-case 'd'
    public int cid;

    @SerializedName("AutoCancelDurationInMinutes")
    public int autoCancelDuration;

    public UploadRequest(String tn, int sn, String apm, String amt,
                         String uid, int mid, String stk,
                         String sid, int cid, int autoCancelDuration) {

        this.tn = tn;
        this.sn = sn;
        this.apm = apm;
        this.amt = amt;
        this.uid = uid;
        this.mid = mid;
        this.stk = stk;
        this.sid = sid;
        this.cid = cid;
        this.autoCancelDuration = autoCancelDuration;
    }
}
