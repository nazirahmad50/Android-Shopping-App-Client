package com.nazir.shopping.Remote;

import com.nazir.shopping.Model.MyResponse;
import com.nazir.shopping.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA2jkTOg0:APA91bEH7Vpumt-LzZfBhUTvp4A9HxQ7H4J9M0wq513UGMooObst0A3e2orCD-QS2B7eBq1CGex-3JR3mW9lPMWidRlx8mxMUN8vvssJYjxhz99aFApqArOcsJmF7k2wuuC5d_C4snUH"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
