package com.realese.genfit.retrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitService {
    // @GET( EndPoint-자원위치(URI) )
    @POST("sdapi/v1/txt2img") //HTTP 메서드 및 URL
    @Headers("Content-Type: application/json; charset=utf-8")
    //Requests 타입의 DTO 데이터와 API 키를 요청
    Call<Response> getPosts(@Body Request request);
}
