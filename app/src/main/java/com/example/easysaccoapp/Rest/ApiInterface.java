package com.example.easysaccoapp.Rest;

import com.example.easysaccoapp.Model.LoanShareResp;
import com.example.easysaccoapp.Model.Response;
import com.example.easysaccoapp.Model.SynchData;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("Transaction/getLoanShareTypes")
    Call<LoanShareResp> getItems();
    @POST("Transaction/payments")
    Call<Response> makePayments(@Body ArrayList<SynchData> payments);
}
