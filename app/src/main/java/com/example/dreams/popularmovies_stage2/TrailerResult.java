package com.example.dreams.popularmovies_stage2;


import com.example.dreams.popularmovies_stage2.models.TrailersModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TrailerResult {
    @SerializedName("results")
    @Expose
    private List<TrailersModel> results = null;

    public List<TrailersModel> getResults() {
        return results;
    }
}
