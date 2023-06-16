package com.realese.genfit.retrofit;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.realese.genfit.items.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class Request {



    @Expose
    @SerializedName("uid")
    public String uid;

    @Expose
    @SerializedName("taskId")
    public String taskId;

    @Expose
    @SerializedName("input_tag")
    public String input_tag;

    @Expose
    @SerializedName("sex")
    public String sex;

    @Expose
    @SerializedName("text")
    public String text;

    @Expose
    @SerializedName("clothes")
    public Map<String, String> clothes;


    public Request(String uid, String taskId, String text, String input_tag, int sex, Map<String, String> clothes){
        this.uid = uid;
        this.taskId = taskId;

        String s = "";
        if (sex == User.SEX_MALE){
            s = "1boy, male";
        }else{
            s = "1girl";
        }
        this.sex = s;
        this.input_tag = input_tag.toLowerCase();
        this.text = text.toLowerCase();
        this.clothes = clothes;
    }

}
