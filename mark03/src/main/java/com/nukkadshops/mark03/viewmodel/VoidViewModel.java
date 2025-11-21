package com.nukkadshops.mark03.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nukkadshops.mark03.models.VoidRequest;
import com.nukkadshops.mark03.models.VoidResponse;
import com.nukkadshops.mark03.repository.PaymentRepository;
import com.nukkadshops.mark03.sdk.PaymentConfig;
import com.nukkadshops.mark03.sdk.PaymentState;
import com.nukkadshops.mark03.sdk.VoidState;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoidViewModel extends ViewModel {

    public MutableLiveData<VoidState> state =
            new MutableLiveData<>(VoidState.IDLE);

    public MutableLiveData<VoidResponse> res1 =
            new MutableLiveData<>();

    PaymentRepository repo;

    public VoidViewModel(PaymentConfig c) {
        repo = new PaymentRepository(c);
    }

    public void voidPayment(VoidRequest req){
        state.setValue(VoidState.PROCESSING);

        repo.voidTransaction(req, new retrofit2.Callback<VoidResponse>() {
            @Override
            public void onResponse(retrofit2.Call<VoidResponse> call, retrofit2.Response<VoidResponse> response) {
                res1.setValue(response.body());
                state.setValue(VoidState.APPROVED);
            }

            @Override
            public void onFailure(retrofit2.Call<VoidResponse> call, Throwable t) {
                state.setValue(VoidState.ERROR);
            }
        });
    }
}
