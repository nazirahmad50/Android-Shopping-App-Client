package com.nazir.shopping.Remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {


    //**********************************************Notification*************************
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseURL){

        if (retrofit == null){

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


    //**********************************************Google Maps*************************


    private static Retrofit retrofitGoogleApi = null;

    public static Retrofit getGoogleApiClient(String baseURL){

        if (retrofitGoogleApi == null){

            retrofitGoogleApi = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofitGoogleApi;
    }
}
