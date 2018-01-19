package com.example.dreams.popularmovies_stage2.provider;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;
import com.example.dreams.popularmovies_stage2.provider.Contract.*;


public class CoreInteractor {
    
    private Context mContext;

    public CoreInteractor(Context mContext) {
        this.mContext = mContext;
    }

    public void putMoviesDB(JSONArray movies){

        final String movie_list = "results";
        final String movie_id = "id";
        final String movie_title = "original_title";
        final String movie_image = "poster_path";
        final String movie_poster = "backdrop_path";
        final String movie_overview = "overview";
        final String movie_release_date = "release_date";
        final String movie_vote_rate = "vote_average";
        try{

            Vector<ContentValues> cVVector = new Vector<>(movies.length());

            for (int i=0; i < movies.length();i++){
                String id,title,image,poster,overview,release_date,fav,vote_rate;
                JSONObject movie = movies.getJSONObject(i);
                id = movie.getString(movie_id);
                title = movie.getString(movie_title);
                image = movie.getString(movie_image);
                poster = movie.getString(movie_poster);
                overview = movie.getString(movie_overview);
                release_date = movie.getString(movie_release_date);
                vote_rate = movie.getString(movie_vote_rate);
                fav = "0";

                ContentValues moviesValues = new ContentValues();

                moviesValues.put(MovieEntry.COLUMN_ID,id);
                moviesValues.put(MovieEntry.COLUMN_FAV,fav);
                moviesValues.put(MovieEntry.COLUMN_TITLE,title);
                moviesValues.put(MovieEntry.COLUMN_IMAGE_PATH,image);
                moviesValues.put(MovieEntry.COLUMN_POSTER_PATH,poster);
                moviesValues.put(MovieEntry.COLUMN_OVERVIEW,overview);
                moviesValues.put(MovieEntry.COLUMN_RELEASE_DATE,release_date);
                moviesValues.put(MovieEntry.COLUMN_VOTE_RATE,vote_rate);

                cVVector.add(moviesValues);
            }

            int inserted = 0;
            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvArray);
            }

            Log.i(CoreInteractor.class.getSimpleName(), "Movie adding Complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int updateFavoriteDB(int fav,String id){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieEntry.COLUMN_FAV, fav);
        return mContext.getContentResolver().update(MovieEntry.CONTENT_URI, contentValues,
                MovieEntry.COLUMN_ID + "=" + id , null);
    }

    public void putReviewsDB(JSONObject jsonObject){

        final String review_list = "results";
        final String review_id = "id";
        final String review_author = "author";
        final String review_content = "content";
        final String review_movie_id = "id";
        try{
            String movie_id = jsonObject.getString(review_movie_id);
            JSONArray reviews = jsonObject.getJSONArray(review_list);
            Vector<ContentValues> cVVector = new Vector<>(reviews.length());

            for (int i=0; i < reviews.length();i++){
                String id,author,content;
                JSONObject review = reviews.getJSONObject(i);
                id = review.getString(review_id);
                author = review.getString(review_author);
                content = review.getString(review_content);

                ContentValues reviewsValues = new ContentValues();

                reviewsValues.put(ReviewEntry.COLUMN_REVIEW_ID,id);
                reviewsValues.put(ReviewEntry.COLUMN_AUTHOR,author);
                reviewsValues.put(ReviewEntry.COLUMN_CONTENT,content);
                reviewsValues.put(ReviewEntry.COLUMN_MOVIE_ID,movie_id);

                cVVector.add(reviewsValues);
            }

            int inserted = 0;
            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                try {
                    inserted = mContext.getContentResolver().bulkInsert(ReviewEntry.CONTENT_URI, cvArray);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            Log.i(CoreInteractor.class.getSimpleName(), "Review adding Complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void putVideosDB(JSONObject jsonObject){

        final String video_list = "results";
        final String video_id = "id";
        final String video_key = "key";
        final String video_name = "name";
        final String video_site = "site";
        final String video_movie_id = "id";
        try{
            String movie_id = jsonObject.getString(video_movie_id);
            JSONArray reviews = jsonObject.getJSONArray(video_list);
            Vector<ContentValues> cVVector = new Vector<>(reviews.length());

            for (int i=0; i < reviews.length();i++){
                String id,key,name,site;
                JSONObject review = reviews.getJSONObject(i);
                id = review.getString(video_id);
                key = review.getString(video_key);
                name = review.getString(video_name);
                site = review.getString(video_site);

                ContentValues videosValues = new ContentValues();

                videosValues.put(VideoEntry.COLUMN_VIDEO_ID,id);
                videosValues.put(VideoEntry.COLUMN_NAME,name);
                videosValues.put(VideoEntry.COLUMN_KEY,key);
                videosValues.put(VideoEntry.COLUMN_MOVIE_ID,movie_id);
                videosValues.put(VideoEntry.COLUMN_SITE,site);

                cVVector.add(videosValues);
            }

            int inserted = 0;
            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                try {
                    inserted = mContext.getContentResolver().bulkInsert(VideoEntry.CONTENT_URI, cvArray);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            Log.i(CoreInteractor.class.getSimpleName(), "Videos adding Complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
