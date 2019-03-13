package com.nazir.shopping.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface IGoogleService {

    /**
     * This converts API into java interface
     * @param url
     * @return
     */
    @GET
    Call<String> getAddressName(@Url String url);

}
