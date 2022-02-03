package com.kosmo.nbbangapp.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LoginService {

    @GET("/sociallogin.do")
    Call<String> confirms(@Query("query") String query);

}
