package com.example.dreams.popularmovies_stage2.service;



import com.example.dreams.popularmovies_stage2.BuildConfig;
import com.example.dreams.popularmovies_stage2.ReviewsResult;
import com.example.dreams.popularmovies_stage2.TrailerResult;
import com.example.dreams.popularmovies_stage2.models.MovieModel;
import com.example.dreams.popularmovies_stage2.models.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface ApiService {
    @GET("popular?api_key="+ BuildConfig.THE_MOVIE_DB_API_TOKEN )
    Call<MovieResponse> mostListMovies();

    @GET("top_rated?api_key="+ BuildConfig.THE_MOVIE_DB_API_TOKEN )
    Call<MovieResponse> voteListMovies();


    @GET("{FILM_ID}?api_key="+BuildConfig.THE_MOVIE_DB_API_TOKEN )
    Call<MovieModel> Movie(@Path("FILM_ID") int filmID);


    @GET("{FILM_ID}/videos?api_key="+BuildConfig.THE_MOVIE_DB_API_TOKEN )
    Call<TrailerResult> trailer(@Path("FILM_ID") int filmID);

    @GET("{FILM_ID}/reviews?api_key="+BuildConfig.THE_MOVIE_DB_API_TOKEN )
    Call<ReviewsResult> reviews(@Path("FILM_ID") int filmID);

}
