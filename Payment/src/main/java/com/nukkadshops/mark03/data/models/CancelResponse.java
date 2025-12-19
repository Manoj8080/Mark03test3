package com.nukkadshops.mark03.data.models;

import com.google.gson.annotations.SerializedName;

public class CancelResponse {

    @SerializedName("ResponseCode")
    public int rc;

    @SerializedName("ResponseMessage")
    public String rm;

}
