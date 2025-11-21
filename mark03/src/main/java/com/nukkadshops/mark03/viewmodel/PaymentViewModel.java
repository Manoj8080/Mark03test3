/*
package com.nukkadshops.mark03.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nukkadshops.mark03.models.*;
import com.nukkadshops.mark03.repository.PaymentRepository;
import com.nukkadshops.mark03.sdk.PaymentConfig;
import com.nukkadshops.mark03.sdk.PaymentMode;
import com.nukkadshops.mark03.sdk.PaymentState;

public class PaymentViewModel extends ViewModel {

    // Existing state
    public MutableLiveData<PaymentState> state = new MutableLiveData<>(PaymentState.IDLE);
    public MutableLiveData<Long> ptr = new MutableLiveData<>(0L);

    // New: Responses LiveData
    public MutableLiveData<UploadResponse> uploadResponse = new MutableLiveData<>();
    public MutableLiveData<StatusResponse> statusResponse = new MutableLiveData<>();
    public MutableLiveData<CancelResponse> cancelResponse = new MutableLiveData<>();
    public MutableLiveData<VoidResponse> voidResponse = new MutableLiveData<>();

    PaymentRepository repo;
    PaymentConfig cfg;

    public PaymentViewModel(PaymentConfig c){
        cfg = c;
        repo = new PaymentRepository(c);
    }

    // ---------------------------
    // 1. START PAYMENT (Already exists)
    // ---------------------------
    */
/*public void startPayment(String amt, PaymentMode m, String txn){
        state.setValue(PaymentState.UPLOADING);

        UploadRequest r = new UploadRequest(
                txn,
                1,
                m.getCode(),
                amt,
                cfg.getUserId(),
                cfg.getMerchantId(),
                cfg.getSecurityToken(),
                cfg.getStoreId(),
                cfg.getClientId(),
                5
        );

        repo.upload(r, new retrofit2.Callback<UploadResponse>() {
            public void onResponse(retrofit2.Call<UploadResponse> c, retrofit2.Response<UploadResponse> r){
                if(r.body() != null){
                    ptr.setValue(r.body().ptr);
                    uploadResponse.setValue(r.body());
                }
            }
            public void onFailure(retrofit2.Call<UploadResponse> c, Throwable t){
                state.setValue(PaymentState.ERROR);
            }
        });
    }*//*


    // ---------------------------
    // 2. UPLOAD PAYMENT
    // ---------------------------
    public void uploadPayment(UploadRequest request) {
        state.setValue(PaymentState.UPLOADING);

        repo.upload(request, new retrofit2.Callback<UploadResponse>() {
            @Override
            public void onResponse(retrofit2.Call<UploadResponse> call, retrofit2.Response<UploadResponse> response) {
                uploadResponse.setValue(response.body());
                state.setValue(PaymentState.UPLOAD_DONE);
            }

            @Override
            public void onFailure(retrofit2.Call<UploadResponse> call, Throwable t) {
                state.setValue(PaymentState.ERROR);
            }
        });
    }

    // ---------------------------
    // 3. CHECK STATUS
    // ---------------------------
    public void checkStatus(StatusRequest req){
        state.setValue(PaymentState.CHECKING);

        repo.status(req, new retrofit2.Callback<StatusResponse>() {
            @Override
            public void onResponse(retrofit2.Call<StatusResponse> call, retrofit2.Response<StatusResponse> response) {
                statusResponse.setValue(response.body());
                state.setValue(PaymentState.CHECK_DONE);
            }

            @Override
            public void onFailure(retrofit2.Call<StatusResponse> call, Throwable t) {
                state.setValue(PaymentState.ERROR);
            }
        });
    }

    // ---------------------------
    // 4. CANCEL PAYMENT
    // ---------------------------
    public void cancelPayment(CancelRequest req){
        state.setValue(PaymentState.CANCELING);

        repo.cancel(req, new retrofit2.Callback<CancelResponse>() {
            @Override
            public void onResponse(retrofit2.Call<CancelResponse> call, retrofit2.Response<CancelResponse> response) {
                cancelResponse.setValue(response.body());
                state.setValue(PaymentState.CANCEL_DONE);
            }

            @Override
            public void onFailure(retrofit2.Call<CancelResponse> call, Throwable t) {
                state.setValue(PaymentState.ERROR);
            }
        });
    }

    // ---------------------------
    // 5. VOID PAYMENT
    // ---------------------------
    */
/*public void voidPayment(VoidRequest req){
        state.setValue(PaymentState.VOIDING);

        repo.voidTransaction(req, new retrofit2.Callback<VoidResponse>() {
            @Override
            public void onResponse(retrofit2.Call<VoidResponse> call, retrofit2.Response<VoidResponse> response) {
                voidResponse.setValue(response.body());
                state.setValue(PaymentState.VOID_DONE);
            }

            @Override
            public void onFailure(retrofit2.Call<VoidResponse> call, Throwable t) {
                state.setValue(PaymentState.ERROR);
            }
        });
    }*//*

}
*/
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
                    if (msg.contains("success") ||
                            msg.contains("failed") ||
                            msg.contains("cancel") ||
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
                cancelResponse.setValue(response.body());
                state.setValue(PaymentState.CANCEL_DONE);
            }

            @Override
            public void onFailure(Call<CancelResponse> call, Throwable t) {
                state.setValue(PaymentState.ERROR);
            }
        });
    }
}
