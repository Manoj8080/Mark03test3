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


    // 🔥 Handler is now INJECTABLE for unit tests
    private Handler handler;

    // Poller
    private Runnable poller;

    // Repository + Config
    PaymentRepository repo;
    PaymentConfig cfg;

    // -------------------------------------------------------
    // PRODUCTION CONSTRUCTOR (normal app use)
    // -------------------------------------------------------
    public PaymentViewModel(PaymentConfig c) {
        this.cfg = c;
        this.repo = new PaymentRepository(c);
        this.handler = new Handler(Looper.getMainLooper());   // real Android handler
    }

    // -------------------------------------------------------
    // TEST CONSTRUCTOR (for Robolectric/JVM tests)
    // -------------------------------------------------------
    public PaymentViewModel(PaymentConfig c, Handler testHandler, PaymentRepository testRepo) {
        this.cfg = c;
        this.handler = testHandler;   // mock handler
        this.repo = testRepo;         // mock repo
    }

    // -------------------------------------------------------
    // SETTERS for injection (OPTIONAL but useful for flexibility)
    // -------------------------------------------------------
    public void setHandler(Handler h) {
        this.handler = h;
    }

    public void setRepository(PaymentRepository r) {
        this.repo = r;
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
                    long plutusPtr = res.ptr;
                    ptr.setValue(plutusPtr);
                    startStatusPolling(plutusPtr);
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
    // STATUS CHECK
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
    // POLLING LOGIC
    // ---------------------------
    public void startStatusPolling(long ptrId) {

        stopStatusPolling(); // prevent duplicate pollers

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

                handler.postDelayed(this, 5000); // repeat
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
        stopStatusPolling();
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
                cancelResponse.setValue(res);

                state.setValue(PaymentState.CANCEL_DONE);
            }

            @Override
            public void onFailure(Call<CancelResponse> call, Throwable t) {
                state.setValue(PaymentState.ERROR);
            }
        });
    }
}
