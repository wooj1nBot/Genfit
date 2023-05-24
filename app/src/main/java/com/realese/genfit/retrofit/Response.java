package com.realese.genfit.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Response {

    @SerializedName("choices")
    public List<Choice> choices;

}
