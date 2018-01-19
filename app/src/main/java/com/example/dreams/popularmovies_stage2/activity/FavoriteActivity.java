package com.example.dreams.popularmovies_stage2.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;
import android.widget.TextView;

import com.example.dreams.popularmovies_stage2.R;
import com.example.dreams.popularmovies_stage2.adapters.MoviesPosterAdapter;
import com.example.dreams.popularmovies_stage2.models.MovieModel;
import com.example.dreams.popularmovies_stage2.provider.Contract;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    GridView mFavoriteGrid;
MoviesPosterAdapter mFavoriteAdapter;
    List<MovieModel> mFavoriteList;
    TextView mViewSort;

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
                new String[]{String.valueOf(1)},null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Define Grid View
        mFavoriteGrid=  findViewById(R.id.filmsGridView);
        //Init and Attach Adapter
        mFavoriteAdapter = new MoviesPosterAdapter(this);
        mViewSort = findViewById(R.id.ViewSort);


    }

    @Override
    protected void onResume() {
        super.onResume();

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


        mFavoriteAdapter.setData(mFavoriteList);
        mFavoriteGrid.setAdapter(mFavoriteAdapter);

    }
}
