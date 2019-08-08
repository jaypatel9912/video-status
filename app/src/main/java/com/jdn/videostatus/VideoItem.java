package com.jdn.videostatus;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class VideoItem implements Serializable {
    @SerializedName("dislikes")
    public int dislikes;
    @SerializedName("id")
    public int id;
    @SerializedName("likes")
    public int likes;
    @SerializedName("location")
    public String location;
    @SerializedName("name")
    public String name;
    public String thumbnail;
}
