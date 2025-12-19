package com.nukkadshops.mark03.data.models;

import com.google.gson.annotations.SerializedName;

public class VoidRequest {

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

    @SerializedName("Clientid")
    public int cid;

    @SerializedName("TxnType")
    public int txnType;

    @SerializedName("OriginalPlutusTransactionReferenceID")
    public long originalPtr;

    public VoidRequest(String tn, int sn, String apm, String amt,
                       String uid, int mid, String stk,
                       String sid, int cid, int txnType, long originalPtr)
    {

        this.tn = tn;
        this.sn = sn;
        this.apm = apm;
        this.amt = amt;
        this.uid = uid;
        this.mid = mid;
        this.stk = stk;
        this.sid = sid;
        this.cid = cid;
        this.txnType = txnType;
        this.originalPtr = originalPtr;
    }
}
