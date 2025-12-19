package com.nukkadshops.mark03test3;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.nukkadshops.mark03.sdk.PaymentConfig;
import com.nukkadshops.mark03.viewmodel.PaymentViewModel;

public class PayFactory implements ViewModelProvider.Factory {

    private final PaymentConfig config;

    public PayFactory(PaymentConfig config) {
        this.config = config;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if (modelClass.isAssignableFrom(PaymentViewModel.class)) {
            return (T) new PaymentViewModel(config);
        }

        if (modelClass.isAssignableFrom(PaymentViewModel.class)) {
            return (T) new PaymentViewModel(config);
        }

        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
