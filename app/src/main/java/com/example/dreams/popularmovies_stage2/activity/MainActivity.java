package com.example.dreams.popularmovies_stage2.activity;


import android.database.Cursor;
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
import com.example.dreams.popularmovies_stage2.provider.Contract;
import com.example.dreams.popularmovies_stage2.provider.CoreInteractor;
import com.example.dreams.popularmovies_stage2.service.ApiService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    MoviesPosterAdapter mMoviesAdapter;
    TextView mViewSort;
    Retrofit mRetrofit;
    ApiService mService;
    @BindView(R.id.filmsGridView)
    GridView mGridView;
    @BindBool(R.bool.two_pane_mode)
    boolean isTwoPane;
    int mLastPositionX = 0;
    int mLastPositionY = 0;
    Call<MovieResponse> desMovieCall;
    Call<MovieResponse> highMovieCall;
    List<MovieModel> mFavoriteList;

    static final int COL_MOVIE_UID = 1;
    static final int COL_MOVIE_TITLE = 3;
    static final int COL_MOVIE_IMAGE = 4;


    private static final String[] MOVIE_COLUMNS = {
            Contract.MovieEntry.TABLE_NAME + "." + Contract.MovieEntry._ID,
            Contract.MovieEntry.COLUMN_ID,
            Contract.MovieEntry.COLUMN_FAV,
            Contract.MovieEntry.COLUMN_TITLE,
            Contract.MovieEntry.COLUMN_IMAGE_PATH,

    };

    public Cursor getMoviesFav() {
        return getContentResolver().query(Contract.MovieEntry.CONTENT_URI,
                MOVIE_COLUMNS, Contract.MovieEntry.COLUMN_FAV + " = ?",
                new String[]{String.valueOf(1)}, null);
    }

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
        ButterKnife.bind(this);

        mViewSort = findViewById(R.id.ViewSort);
        mMoviesAdapter = new MoviesPosterAdapter(this, getFragmentManager(), isTwoPane);

        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/movie/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mService = mRetrofit.create(ApiService.class);
        desMovieCall = mService.mostListMovies();
        highMovieCall = mService.voteListMovies();
        desMovieCall.enqueue(new highVoteCallback());
        mGridView.setAdapter(mMoviesAdapter);

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
                highMovieCall.clone().enqueue(new highVoteCallback());
                break;

            case R.id.itemPopDec:
                choosenList = 0;
                mViewSort.setText(R.string.most_popular);
                desMovieCall.clone().enqueue(new desCallback());
                break;

            case R.id.itemFavorite:
                mViewSort.setText(R.string.Favorite);
                Cursor moviesCursor = getMoviesFav();

                if (moviesCursor.getCount() != 0)
                    mFavoriteList = new ArrayList<>();
                while (moviesCursor.moveToNext()) {
                    MovieModel movieModel = new MovieModel();
                    movieModel.setOriginalTitle(moviesCursor.getString(COL_MOVIE_TITLE));
                    movieModel.setPosterPath(moviesCursor.getString(COL_MOVIE_IMAGE));
                    movieModel.setId(moviesCursor.getInt(COL_MOVIE_UID));

                    movieModel.setAdult(false);

                    mFavoriteList.add(movieModel);
                    mViewSort.setText(R.string.Favorite);

                }
                mMoviesAdapter.setData(mFavoriteList);
                mMoviesAdapter.notifyDataSetChanged();
                break;
            case R.id.itemRefresh:
                mRetrofit = new Retrofit.Builder()
                        .baseUrl("https://api.themoviedb.org/3/movie/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                mService = mRetrofit.create(ApiService.class);

                if (choosenList == 0) {
                    mViewSort.setText(R.string.most_popular);
                    desMovieCall.clone().enqueue(new desCallback());
                } else {
                    mViewSort.setText(R.string.high_rated);
                    highMovieCall.clone().enqueue(new highVoteCallback());
                }

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
                Type type = new TypeToken<List<MovieModel>>() {
                }.getType();
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
                mMoviesAdapter.setData(response.body().getResults());
                mMoviesAdapter.notifyDataSetChanged();
                Gson gson = new Gson();
                Type type = new TypeToken<List<MovieModel>>() {
                }.getType();
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
