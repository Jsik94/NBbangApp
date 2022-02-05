package com.kosmo.nbbangapp.retrofit;

import com.kosmo.nbbangapp.models.MemberItem;
import com.kosmo.nbbangapp.models.RespItem;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LoginService {

    //회원 여부 판단
    @GET("rest/sociallogin.do")
    Call<RespItem> isMember(@Query("email") String email);

    //일반 회원 여부 판단
    @POST("rest/genlogin.do")
    Call<RespItem> isGeneralMember(@Body MemberItem item);

    //회원 여부 판단
    @POST("rest/sign.do")
    Call<RespItem> registMember(@Body MemberItem item);

    @GET("rest/memberInfo.do")
    Call<MemberItem> getMember(@Query("email") String email);
}
