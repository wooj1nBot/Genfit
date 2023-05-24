package com.realese.genfit.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Request {

    @Expose
    @SerializedName("model")
    public String model;

    @Expose
    @SerializedName("messages")
    public List<Message> messages;

    @Expose
    @SerializedName("stream")
    public boolean stream;

    @Expose
    @SerializedName("temperature")
    public double temperature;

}
