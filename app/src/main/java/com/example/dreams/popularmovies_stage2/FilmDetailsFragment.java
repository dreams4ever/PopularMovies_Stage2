package com.example.dreams.popularmovies_stage2;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dreams.popularmovies_stage2.adapters.ReviewsAdapter;
import com.example.dreams.popularmovies_stage2.adapters.TrailersAdapter;
import com.example.dreams.popularmovies_stage2.models.MovieModel;
import com.example.dreams.popularmovies_stage2.models.ReviewsModel;
import com.example.dreams.popularmovies_stage2.models.TrailersModel;
import com.example.dreams.popularmovies_stage2.provider.Contract;
import com.example.dreams.popularmovies_stage2.provider.CoreInteractor;
import com.example.dreams.popularmovies_stage2.service.ApiService;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FilmDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    int mId;
    @BindView(R.id.film_adult)
    TextView mFilm_adult;
    @BindView(R.id.trailers)
    TextView trailers;
    @BindView(R.id.Reviews)
    TextView Reviews;
    @BindView(R.id.film_rate)
    TextView mFilm_Rate;
    @BindView(R.id.film_overview)
    TextView mFilm_Overview;
    @BindView(R.id.film_date)
    TextView mFilm_Date;
    @BindView(R.id.film_poster)
    ImageView mFilm_Poster;
    @BindView(R.id.filmPosterBackground)
    ImageView mBackDropPath;
    Retrofit mRetrofit;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.favorite_bt)
    MaterialFavoriteButton mMaterialFavoriteButton;
    List<TrailersModel> mMoviesTrailers;
    List<ReviewsModel> mMovieReviews;
    TrailersAdapter mTrailersAdapter;
    ReviewsAdapter mReviewsAdapter;
    Realm mRealm;
    @BindView(R.id.Rec_view_trailers)
    RecyclerView mTrailers;
    @BindView(R.id.Rec_view_reviews)
    RecyclerView mReviews;
    MovieModel mModel;

    private static final int MOVIES_LOADER = 0;

    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_UID = 1;
    static final int COL_MOVIE_FAV = 2;
    static final int COL_MOVIE_TITLE = 3;
    static final int COL_MOVIE_IMAGE = 4;
    static final int COL_MOVIE_POSTER = 5;
    static final int COL_MOVIE_RELEASE = 6;
    static final int COL_MOVIE_OVERVIEW = 7;
    static final int COL_MOVIE_RATE = 8;

    private static final String[] MOVIE_COLUMNS = {
            Contract.MovieEntry.TABLE_NAME + "." + Contract.MovieEntry._ID,
            Contract.MovieEntry.COLUMN_ID,
            Contract.MovieEntry.COLUMN_FAV,
            Contract.MovieEntry.COLUMN_TITLE,
            Contract.MovieEntry.COLUMN_IMAGE_PATH,
            Contract.MovieEntry.COLUMN_POSTER_PATH,
            Contract.MovieEntry.COLUMN_RELEASE_DATE,
            Contract.MovieEntry.COLUMN_OVERVIEW,
            Contract.MovieEntry.COLUMN_VOTE_RATE
    };
    private Cursor cursorMovie;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        Realm.init(getActivity());
        mTrailersAdapter = new TrailersAdapter(getActivity());
        mTrailers.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTrailers.setHasFixedSize(true);
        mTrailers.setNestedScrollingEnabled(false);

        mReviewsAdapter = new ReviewsAdapter(getActivity());
        mReviews.setLayoutManager(new LinearLayoutManager(getActivity()));
        mReviews.setHasFixedSize(true);
        mReviews.setNestedScrollingEnabled(false);
        mRealm = Realm.getDefaultInstance();


        if (getArguments() != null) {
            mId = getArguments().getInt("key");

            getLoaderManager().initLoader(MOVIES_LOADER, null, this);

            mRetrofit = new Retrofit.Builder()
                    .baseUrl("https://api.themoviedb.org/3/movie/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService service = mRetrofit.create(ApiService.class);
            final Call<MovieModel> MovieCall = service.Movie(mId);
            MovieCall.enqueue(new FilmDetailsFragment.DetailsMovie());


            ApiService TrailerService = mRetrofit.create(ApiService.class);
            final Call<TrailerResult> TrailerMovieCall = TrailerService.trailer(mId);
            TrailerMovieCall.enqueue(new FilmDetailsFragment.trailers());

            mTrailers.setAdapter(mTrailersAdapter);

            trailers.setVisibility(View.VISIBLE);
            Reviews.setVisibility(View.VISIBLE);

            ApiService ReviewsService = mRetrofit.create(ApiService.class);
            final Call<ReviewsResult> ReviewsMovieCall = ReviewsService.reviews(mId);
            ReviewsMovieCall.enqueue(new FilmDetailsFragment.reviews());

            mReviews.setAdapter(mReviewsAdapter);

            //Favorite button
            mMaterialFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean fav = mMaterialFavoriteButton.isFavorite();
                    if (!fav) {
                        saveFavorite();
                    } else {
                        removeFavorite();
                    }
                }
            });

            mMaterialFavoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                @Override
                public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {

                }
            });
        } else {
            trailers.setVisibility(View.GONE);
            Reviews.setVisibility(View.GONE);
            mFilm_Overview.setText(R.string.choose_movie_first);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case MOVIES_LOADER:
                return new CursorLoader(getActivity(), Contract.MovieEntry.CONTENT_URI,
                        MOVIE_COLUMNS, Contract.MovieEntry.COLUMN_ID + " = ?",
                        new String[]{String.valueOf(this.mId)},
                        Contract.MovieEntry._ID + " ASC");
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorMovie = data;
        if (cursorMovie.moveToFirst()) {
            int id = cursorMovie.getInt(COL_MOVIE_UID);
            int fav = cursorMovie.getInt(COL_MOVIE_FAV);
            if (cursorMovie.getInt(COL_MOVIE_FAV) == 1)
                mMaterialFavoriteButton.setFavorite(true);
            else
                mMaterialFavoriteButton.setFavorite(false);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorMovie = null;
    }

    public class DetailsMovie implements Callback<MovieModel> {

        @Override
        public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
            if (response.body() != null) {

//                mModel = mRealm.where(MovieModel.class).equalTo("id", response.body()
//                        .getId()).findFirst();
//                if (mModel != null)
//                    mMaterialFavoriteButton.setFavorite(true);
//                else {
//                    mMaterialFavoriteButton.setFavorite(false);
//                    mModel = response.body();
//                }


                String title = response.body().getOriginalTitle();
                String date = response.body().getReleaseDate();
                mFilm_Date.setText(date);
                Boolean adult = response.body().getAdult();

                if (adult == true)
                    mFilm_adult.setText(R.string.for_adults_only);
                else
                    mFilm_adult.setText(R.string.for_all);

                String overview = response.body().getOverview();
                mFilm_Overview.setText(overview);

                String rate = response.body().getRate();
                mFilm_Rate.setText(String.format("%s%s", rate, getString(R.string.per_ten)));

                Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w500" + response.body().getPosterPath()).into(mFilm_Poster);


                //coordinator layout
//                Toolbar toolbar = findViewById(R.id.toolbar);
//                getActivity().setSupportActionBar(toolbar);
                //getActivity() line shows back button
//                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                collapsingToolbarLayout.setTitle(title);
                Picasso.with(getActivity())
                        .load("http://image.tmdb.org/t/p/w780" + response.body().
                                getBackdropPath()).into(mBackDropPath);

            }
        }

        @Override
        public void onFailure(Call<MovieModel> call, Throwable t) {
            t.printStackTrace();
            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_LONG).show();
        }
    }

    public class trailers implements Callback<TrailerResult> {

        @Override
        public void onResponse(Call<TrailerResult> call, Response<TrailerResult> response) {
            if (response != null) {

                mMoviesTrailers = response.body().getResults();
                mTrailersAdapter.setData(mMoviesTrailers);
                mTrailersAdapter.notifyDataSetChanged();

            }
        }

        @Override
        public void onFailure(Call<TrailerResult> call, Throwable t) {
            {
                t.printStackTrace();
            }

        }
    }

    public class reviews implements Callback<ReviewsResult> {

        @Override
        public void onResponse(Call<ReviewsResult> call, Response<ReviewsResult> response) {
            if (response != null) {
                mMovieReviews = response.body().getResults();
                mReviewsAdapter.setData(mMovieReviews);
                mReviewsAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onFailure(Call<ReviewsResult> call, Throwable t) {
            t.printStackTrace();
        }
    }

    public void saveFavorite() {
        CoreInteractor coreInteractor = new CoreInteractor(getActivity());
        int updated = coreInteractor.updateFavoriteDB(1, String.valueOf(mId));
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);

//        mRealm.beginTransaction();
//        mRealm.copyToRealm(mModel);
//        mRealm.commitTransaction();
//        mMaterialFavoriteButton.setFavorite(true);

    }

    public void removeFavorite() {
        CoreInteractor coreInteractor = new CoreInteractor(getActivity());
        int updated = coreInteractor.updateFavoriteDB(0, String.valueOf(mId));
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);

//        mRealm.beginTransaction();
//        MovieModel model = mRealm.where(MovieModel.class).equalTo("id", mModel.getId()).findFirst();
//        if (model != null)
//            model.deleteFromRealm();
//        mRealm.commitTransaction();
//        mMaterialFavoriteButton.setFavorite(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_details_cordinator_layout, container, false);
    }
}
