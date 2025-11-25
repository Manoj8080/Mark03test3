package com.nukkadshops.mark03.viewmodel;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nukkadshops.mark03.models.*;
import com.nukkadshops.mark03.repository.PaymentRepository;
import com.nukkadshops.mark03.sdk.PaymentConfig;
import com.nukkadshops.mark03.sdk.PaymentState;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentViewModel extends ViewModel {

    // States
    public MutableLiveData<PaymentState> state = new MutableLiveData<>(PaymentState.IDLE);
    public MutableLiveData<Long> ptr = new MutableLiveData<>(0L);

    // Responses
    public MutableLiveData<UploadResponse> uploadResponse = new MutableLiveData<>();
    public MutableLiveData<StatusResponse> statusResponse = new MutableLiveData<>();
    public MutableLiveData<CancelResponse> cancelResponse = new MutableLiveData<>();


    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable poller;

    PaymentRepository repo;
    PaymentConfig cfg;

    public PaymentViewModel(PaymentConfig c) {
        cfg = c;
        repo = new PaymentRepository(c);
    }

    // ---------------------------
    // UPLOAD PAYMENT + START POLLING
    // ---------------------------
    public void uploadPayment(UploadRequest request) {
        state.setValue(PaymentState.UPLOADING);

        repo.upload(request, new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {

                UploadResponse res = response.body();
                uploadResponse.setValue(res);

                if (res != null) {
                    long plutusPtr = response.body().ptr;
                    ptr.setValue(plutusPtr);
                    startStatusPolling(plutusPtr);   // 🔥 Auto start polling
                }

                state.setValue(PaymentState.UPLOAD_DONE);
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                state.setValue(PaymentState.ERROR);
            }
        });
    }

    // ---------------------------
    // STATUS CHECK (USED BY POLLER)
    // ---------------------------
    public void checkStatus(StatusRequest req) {
        state.setValue(PaymentState.CHECKING);

        repo.status(req, new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {

                StatusResponse res = response.body();
                statusResponse.setValue(res);

                if (res != null) {
                    String msg = (res.rm != null ? res.rm.toLowerCase() : "");

                    // 🔥 Final statuses → stop polling
                    if (msg.contains("approved") ||
                            msg.contains("failed") ||
                            msg.contains("invalid") ||
                            msg.contains("void")) {

                        stopStatusPolling();
                    }
                }

                state.setValue(PaymentState.CHECK_DONE);
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                state.setValue(PaymentState.ERROR);
            }
        });
    }

    // ---------------------------
    // AUTO POLLING LOGIC
    // ---------------------------
    public void startStatusPolling(long ptrId) {

        stopStatusPolling(); // Avoid double polling

        poller = new Runnable() {
            @Override
            public void run() {

                StatusRequest req = new StatusRequest(
                        cfg.getMerchantId(),
                        cfg.getSecurityToken(),
                        cfg.getStoreId(),
                        cfg.getClientId(),
                        ptrId
                );

                checkStatus(req);

                // 🔥 Run again after 5 seconds
                handler.postDelayed(this, 5000);
            }
        };

        handler.post(poller);
    }

    public void stopStatusPolling() {
        if (poller != null) {
            handler.removeCallbacks(poller);
            poller = null;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopStatusPolling(); // Prevent leaks
    }

    // ---------------------------
    // CANCEL PAYMENT
    // ---------------------------
    public void cancelPayment(CancelRequest req) {
        state.setValue(PaymentState.CANCELING);

        repo.cancel(req, new Callback<CancelResponse>() {
            @Override
            public void onResponse(Call<CancelResponse> call, Response<CancelResponse> response) {

                CancelResponse res = response.body();
                cancelResponse.setValue(res);   // ✅ now rm goes to UI

                state.setValue(PaymentState.CANCEL_DONE);
            }

            @Override
            public void onFailure(Call<CancelResponse> call, Throwable t) {
                state.setValue(PaymentState.ERROR);
            }
        });
    }

}
