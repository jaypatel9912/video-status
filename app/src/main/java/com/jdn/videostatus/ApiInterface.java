package com.jdn.videostatus;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("search_v4_appver12_cliflip.php")
    Call<String> getLatestVideos(@Query("id") String id, @Query("category") String category, @Query("language") String language,
                                 @Query("V") String V);

    @GET("search_v4_appver12_cliflip.php")
    Call<String> getPopularVideos(@Query("id") String id, @Query("category") String category, @Query("V") String V, @Query("language") String language);
}
