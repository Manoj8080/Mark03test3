package com.nukkadshops.mark03.repository;

import com.nukkadshops.mark03.models.CancelRequest;
import com.nukkadshops.mark03.models.CancelResponse;
import com.nukkadshops.mark03.models.StatusRequest;
import com.nukkadshops.mark03.models.StatusResponse;
import com.nukkadshops.mark03.models.UploadRequest;
import com.nukkadshops.mark03.models.UploadResponse;
import com.nukkadshops.mark03.models.VoidRequest;
import com.nukkadshops.mark03.models.VoidResponse;
import com.nukkadshops.mark03.network.ApiClient;
import com.nukkadshops.mark03.network.ApiService;
import com.nukkadshops.mark03.sdk.PaymentConfig;

import retrofit2.Callback;
public class PaymentRepository {
    ApiService apiService;

    public PaymentRepository(PaymentConfig config) {
        apiService = ApiClient.getClient(config).create(ApiService.class);
    }

    public void upload(UploadRequest request, Callback<UploadResponse>callback) {
        apiService.uploadResponseCall(request).enqueue(callback);
    }

    public void status(StatusRequest requests, Callback<StatusResponse> callback) {
        apiService.statusResponseCall(requests).enqueue(callback);
    }

    public void cancel(CancelRequest request, Callback<CancelResponse> callback) {
        apiService.cancelResponseCall(request).enqueue(callback);
    }
    public void voidTransaction(VoidRequest request, Callback<VoidResponse> callback) {
        apiService.voidResponseCall(request).enqueue(callback);
    }
}
