package com.realese.genfit.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Response {

    @Expose
    @SerializedName("images")
    public List<String> images;

}
