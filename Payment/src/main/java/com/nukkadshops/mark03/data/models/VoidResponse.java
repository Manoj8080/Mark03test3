package com.nukkadshops.mark03.data.models;

import com.google.gson.annotations.SerializedName;

public class VoidResponse {

    @SerializedName("ResponseCode")
    public int rc;

    @SerializedName("ResponseMessage")
    public String rm;

    @SerializedName("PlutusTransactionReferenceID")
    public long ptr;

    @SerializedName("AdditionalInfo")
    public String info;
}
