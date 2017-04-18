package com.github.projectx.network.api;

import com.github.projectx.model.NewServiceRequest;
import com.github.projectx.model.Service;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by ivan on 15.04.17.
 */

public interface ServiceAPI {
    @GET("services")
    Call<List<Service>> getListServices(
            @Query("category") String category,
            @Query("sort") String sort,
            @Query("page") Integer page,
            @Query("limit") int limit);     // required param

    @GET("my_services")
    Call<List<Service>> getMyServices(
            @Query("page") Integer page,
            @Query("limit") int limit);     // required param

    @POST("services/")
    Call<Void> createService(@Body NewServiceRequest request);

    @GET("services/{id}")
    Call<Service> getService(@Path("id") long id);
}
