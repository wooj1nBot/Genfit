package com.realese.genfit.retrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitService {
    // @GET( EndPoint-자원위치(URI) )
    @POST("v1/chat/completions") //HTTP 메서드 및 URL
    @Headers("Content-Type: application/json")
    //Requests 타입의 DTO 데이터와 API 키를 요청
    Call<String> getPosts(@Body Request request, @Header("Authorization") String key);
}
