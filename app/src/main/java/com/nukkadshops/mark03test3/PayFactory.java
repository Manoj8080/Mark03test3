package com.nukkadshops.mark03test3;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.nukkadshops.mark03.sdk.PaymentConfig;
import com.nukkadshops.mark03.viewmodel.PaymentViewModel;
import com.nukkadshops.mark03.viewmodel.VoidViewModel;

public class PayFactory implements ViewModelProvider.Factory {

    private final PaymentConfig config;

    public PayFactory(PaymentConfig config) {
        this.config = config;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if (modelClass.isAssignableFrom(PaymentViewModel.class)) {
            return (T) new PaymentViewModel(config);
        }

        if (modelClass.isAssignableFrom(VoidViewModel.class)) {
            return (T) new VoidViewModel(config);
        }

        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
