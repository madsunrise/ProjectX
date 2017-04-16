package com.github.projectx.network;

import com.github.projectx.model.Login;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by igor on 16.04.17.
 */

public interface AuthAPI {

    @POST("login")
    Call<Void> login(@Body Login login);

    @POST("signup")
    Call<Void> signup(String name, String email, String phone, String password);
}
