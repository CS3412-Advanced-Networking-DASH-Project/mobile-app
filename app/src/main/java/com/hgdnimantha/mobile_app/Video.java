package com.hgdnimantha.mobile_app;

import com.google.gson.annotations.SerializedName;

public class Video {

    @SerializedName("_id")
    private String _id;

    @SerializedName("title")
    private String superTitle;

    @SerializedName("description")
    private String superDescription;


    public Video(String _id,String title, String description) {
        this._id = _id;
        this.superTitle = title;
        this.superDescription = description;
    }

    public String getTitle() {
        return superTitle;
    }

    public String getID() {
        return _id;
    }

    public String getDescription() {
        return superDescription;
    }

}
