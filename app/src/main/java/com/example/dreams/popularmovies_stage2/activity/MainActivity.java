package com.example.dreams.popularmovies_stage2.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dreams.popularmovies_stage2.R;
import com.example.dreams.popularmovies_stage2.adapters.MoviesPosterAdapter;
import com.example.dreams.popularmovies_stage2.models.MovieModel;
import com.example.dreams.popularmovies_stage2.models.MovieResponse;
import com.example.dreams.popularmovies_stage2.provider.CoreInteractor;
import com.example.dreams.popularmovies_stage2.service.ApiService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    MoviesPosterAdapter mMoviesAdapter;
    MoviesPosterAdapter mDesMoviesAdapter;
    TextView mViewSort;
    Retrofit mRetrofit;
    ApiService mService;
    GridView mGridView;
    int mLastPositionX = 0;
    int mLastPositionY = 0;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mLastPositionX = savedInstanceState.getInt("last_position_x");
        mLastPositionY = savedInstanceState.getInt("last_position_y");
        mGridView.setSelection(mLastPositionY);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mLastPositionY = mGridView.getFirstVisiblePosition();
        mLastPositionX = mGridView.getScrollX();
        outState.putInt("last_position_x", mLastPositionX);
        outState.putInt("last_position_y", mLastPositionY);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mGridView.scrollBy(mLastPositionX, mLastPositionY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewSort = findViewById(R.id.ViewSort);

        mMoviesAdapter = new MoviesPosterAdapter(this);
        mDesMoviesAdapter = new MoviesPosterAdapter(this);

        mGridView = findViewById(R.id.filmsGridView);


        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/movie/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mService = mRetrofit.create(ApiService.class);


        Call<MovieResponse> highMovieCall = mService.voteListMovies();
        highMovieCall.enqueue(new highVoteCallback());

        Call<MovieResponse> desMovieCall = mService.mostListMovies();
        desMovieCall.enqueue(new desCallback());

        mGridView.setAdapter(mDesMoviesAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    int choosenList = 0;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.itemVote:
                choosenList = 1;
                mViewSort.setText(R.string.high_rated);
                mGridView.setAdapter(mMoviesAdapter);
                break;

            case R.id.itemPopDec:
                choosenList = 0;
                mViewSort.setText(R.string.most_popular);
                mGridView.setAdapter(mDesMoviesAdapter);
                break;

            case R.id.itemFavorite:
//                mViewSort.setText(R.string.Favorite);
                Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
                startActivity(intent);
                break;
            case R.id.itemRefresh:
                mRetrofit = new Retrofit.Builder()
                        .baseUrl("https://api.themoviedb.org/3/movie/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                mService = mRetrofit.create(ApiService.class);


                Call<MovieResponse> highMovieCall = mService.voteListMovies();
                highMovieCall.enqueue(new highVoteCallback());

                Call<MovieResponse> desMovieCall = mService.mostListMovies();
                desMovieCall.enqueue(new desCallback());

                if (choosenList == 0)
                    mGridView.setAdapter(mDesMoviesAdapter);
                else mGridView.setAdapter(mMoviesAdapter);

                break;

        }
        return super.onOptionsItemSelected(item);


    }


    public class highVoteCallback implements Callback<MovieResponse> {

        @Override
        public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
            if (response != null) {
                mMoviesAdapter.setData(response.body().getResults());
                mMoviesAdapter.notifyDataSetChanged();
                Gson gson = new Gson();
                Type type = new TypeToken<List<MovieModel>>() {}.getType();
                String json = gson.toJson(response.body().getResults(), type);
                CoreInteractor interactor = new CoreInteractor(MainActivity.this);
                try {
                    JSONArray array = new JSONArray(json);
                    interactor.putMoviesDB(array);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<MovieResponse> call, Throwable t) {
            t.printStackTrace();
            Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_LONG).show();

        }
    }

    public class desCallback implements Callback<MovieResponse> {

        @Override
        public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
            if (response != null) {
                mDesMoviesAdapter.setData(response.body().getResults());
                mDesMoviesAdapter.notifyDataSetChanged();
                Gson gson = new Gson();
                Type type = new TypeToken<List<MovieModel>>() {}.getType();
                String json = gson.toJson(response.body().getResults(), type);
                CoreInteractor interactor = new CoreInteractor(MainActivity.this);
                try {
                    JSONArray array = new JSONArray(json);
                    interactor.putMoviesDB(array);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<MovieResponse> call, Throwable t) {
            t.printStackTrace();
            Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_LONG).show();

        }
    }


}
