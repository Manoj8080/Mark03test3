package com.nukkadshops.mark03.data.network;

import com.nukkadshops.mark03.data.models.CancelRequest;
import com.nukkadshops.mark03.data.models.CancelResponse;
import com.nukkadshops.mark03.data.models.StatusRequest;
import com.nukkadshops.mark03.data.models.StatusResponse;
import com.nukkadshops.mark03.data.models.UploadRequest;
import com.nukkadshops.mark03.data.models.UploadResponse;
import com.nukkadshops.mark03.data.models.VoidRequest;
import com.nukkadshops.mark03.data.models.VoidResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/payment/pinelabs/upload")
    Call<UploadResponse> uploadResponseCall(@Body UploadRequest request);

    @POST("api/payment/pinelabs/status")
    Call<StatusResponse> statusResponseCall(@Body StatusRequest request);

    @POST("api/payment/pinelabs/forceCancel")
    Call<CancelResponse> cancelResponseCall(@Body CancelRequest request);

    @POST("api/payment/pinelabs/void")
    Call<VoidResponse> voidResponseCall(@Body VoidRequest request);
}
