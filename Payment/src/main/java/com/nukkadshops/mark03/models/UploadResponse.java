package com.nukkadshops.mark03.models;

import com.google.gson.annotations.SerializedName;

public class UploadResponse {

    @SerializedName("ResponseCode")
    public int rc;

    @SerializedName("ResponseMessage")
    public String rm;

    @SerializedName("PlutusTransactionReferenceID")
    public long ptr;

    @SerializedName("AdditionalInfo")
    public String info;

}
