package com.jdn.videostatus;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Videos implements Serializable {
    @SerializedName("video")
    public ArrayList<VideoItem> listVideos;
}
