package com.github.projectx.network;

import android.content.Context;

import com.github.projectx.model.Service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by ivan on 15.04.17.
 */

public interface ApiService {
    String BASE_URL = "http://212.109.192.197:8081/v1/";

    @GET("services/")
    Call<List<Service>> getListServices(
            @Query("category") String category,
            @Query("sort") String sort,
            @Query("page") Integer page,
            @Query("limit") int limit);     // required param


    @GET("services/{id}")
    Call<Service> getService(@Path("id") long id);


    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BASIC);
    OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();


    Gson gson = new GsonBuilder().registerTypeAdapter(Service.class, new Service.ServiceDeserializer()).create();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)

            .addConverterFactory(GsonConverterFactory.create(gson)).client(okHttpClient)
            .build();
}
