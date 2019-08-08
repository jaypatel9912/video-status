package com.jdn.videostatus;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class VideoStatusResponse implements Serializable {
    @SerializedName("videos")
    public Videos videos;
}
