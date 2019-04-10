package com.demotmdb.interfaces;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitInterface {

    @GET("discover/movie")
    Call<ResponseBody> GetMovieList( @Query("api_key") String api_key,
                                     @Query("primary_release_year") String releaseyear ,
                                     @Query("sort_by") String sortby);

    @GET("movie/{movieId}")
    Call<ResponseBody> GetMovieDetails(@Path("movieId") String movieId, @Query("api_key") String api_key);
}