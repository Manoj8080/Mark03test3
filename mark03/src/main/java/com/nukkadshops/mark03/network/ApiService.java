package com.nukkadshops.mark03.network;

import com.nukkadshops.mark03.models.CancelRequest;
import com.nukkadshops.mark03.models.CancelResponse;
import com.nukkadshops.mark03.models.StatusRequest;
import com.nukkadshops.mark03.models.StatusResponse;
import com.nukkadshops.mark03.models.UploadRequest;
import com.nukkadshops.mark03.models.UploadResponse;
import com.nukkadshops.mark03.models.VoidRequest;
import com.nukkadshops.mark03.models.VoidResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/upload")
    Call<UploadResponse> uploadResponseCall(@Body UploadRequest request);

    @POST("api/status")
    Call<StatusResponse> statusResponseCall(@Body StatusRequest request);

    @POST("api/force-cancel")
    Call<CancelResponse> cancelResponseCall(@Body CancelRequest request);

    @POST("api/void")
    Call<VoidResponse> voidResponseCall(@Body VoidRequest request);
}
