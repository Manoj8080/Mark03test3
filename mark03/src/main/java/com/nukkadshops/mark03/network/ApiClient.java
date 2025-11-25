package com.nukkadshops.mark03.network;

import com.nukkadshops.mark03.sdk.PaymentConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static Retrofit getClient(PaymentConfig config) {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // 🔥 ADD AUTH HEADERS HERE
        Interceptor headerInterceptor = chain -> {
            Request original = chain.request();

            Request newReq = original.newBuilder()
                    .addHeader("x-api-user", "api")
                    .addHeader("x-api-key", "key")
                    .build();

            return chain.proceed(newReq);
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)          // logs
                .addInterceptor(headerInterceptor) // adds auth headers
                .connectTimeout(config.getTimeoutInSeconds(), TimeUnit.SECONDS)
                .readTimeout(config.getTimeoutInSeconds(), TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(config.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }
}
