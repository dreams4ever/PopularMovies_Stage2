package com.example.dreams.popularmovies_stage2.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReviewsModel {


    @SerializedName("author")
    @Expose
    private String author;

    @SerializedName("content")
    @Expose
    private String content;




    public String getAuthor() {
        return author;
    }
    public String getContent() {
        return content;
    }


}
