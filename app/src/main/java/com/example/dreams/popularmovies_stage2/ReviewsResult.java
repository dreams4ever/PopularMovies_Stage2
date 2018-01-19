package com.example.dreams.popularmovies_stage2;


import com.example.dreams.popularmovies_stage2.models.ReviewsModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;


public class ReviewsResult {

        @SerializedName("results")
        @Expose
        private List<ReviewsModel> results = null;

        public List<ReviewsModel> getResults() {
            return results;
        }
    }


