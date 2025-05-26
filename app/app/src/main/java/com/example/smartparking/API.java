package com.example.smartparking;

import com.example.smartparking.Models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface API{
    @POST("api/signup")
    Call<Response> signup(@Body User user);

    @POST("api/signin")
    Call<Response> signin(@Body User userlogin);

    class Response {
        private String message;
        private String userId;

        public String getMessage() { return message; }
        public String getUserId() { return userId; }
    }
}