package com.example.dreams.popularmovies_stage2.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrailersModel {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("key")
    @Expose
    private String key;




    public String getId() {
        return id;
    }
    public String getname() {
        return name;
    }
    public String getkey() {
        return key;
    }
}
