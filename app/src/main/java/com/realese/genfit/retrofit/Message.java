package com.realese.genfit.retrofit;

import com.google.gson.annotations.SerializedName;

public class Message {


    @SerializedName("role")
    public String role;


    @SerializedName("content")
    public String content;

    public Message(String role , String content){
        this.role = role;
        this.content = content;
    }

}
