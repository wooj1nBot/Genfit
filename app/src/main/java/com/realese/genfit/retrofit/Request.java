package com.realese.genfit.retrofit;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.realese.genfit.items.User;

public class Request {

    @Expose
    @SerializedName("prompt")
    public String prompt;

    @Expose
    @SerializedName("negative_prompt")
    public String negative_prompt;

    @Expose
    @SerializedName("width")
    public int width;

    @Expose
    @SerializedName("height")
    public int height;

    @Expose
    @SerializedName("steps")
    public int steps;

    @Expose
    @SerializedName("cfg_scale")
    public double cfg_scale;

    @Expose
    @SerializedName("sampler_index")
    public String sampler_index;

    @Expose
    @SerializedName("hr_scale")
    public float hr_scale;

    @Expose
    @SerializedName("hr_upscaler")
    public String hr_upscaler;

    @Expose
    @SerializedName("hr_second_pass_steps")
    public int hr_second_pass_steps;

    @Expose
    @SerializedName("denoising_strength")
    public float denoising_strength;


    @Expose
    @SerializedName("enable_hr")
    public boolean enable_hr;

    public Request(){
        this.negative_prompt = "((nsfw, nude)), sketch, sketches, fingers, (worst quality:2), (low quality:2), (normal quality:2), lowres, normal quality, loli, children, ((monochrome)), ((grayscale)), skin spots, acnes, skin blemishes, age spot, (outdoor:1.6), backlight,(ugly:1.331), (duplicate:1.331), (morbid:1.21), (mutilated:1.21), (tranny:1.331), mutated hands, (poorly drawn hands:1.5), blurry, (bad anatomy:1.21), (bad proportions:1.331), extra limbs, (disfigured:1.331), (more than 2 nipples:1.331), (missing arms:1.331), (extra legs:1.331), (fused fingers:1.61051), (too many fingers:1.61051), (extra_anus:1.6),(unclear eyes:1.331), lowers, bad hands, missing fingers, extra digit, (futa:1.1),bad hands, missing fingers";
        this.sampler_index = "DPM++ 2M Karras";
        this.steps = 20;
        this.width = 386;
        this.height = 512;
        this.cfg_scale = 7.5;
        this.denoising_strength = 0.4f;
        this.hr_upscaler = "R-ESRGAN 4x+";
        this.hr_second_pass_steps = 15;
        this.enable_hr = true;
        this.hr_scale = 2f;
    }

    public void setPrompt(String prompt, int sex){
        String s = "";
        if (sex == User.SEX_MALE){
            s = "1boy, male";
        }else{
            s = "1girl";
        }

        this.prompt = "(masterpiece),(best quality:1.0), (8k, RAW photo:1.2), (ultra highres:1.0), (photorealistic:1.4), detailed, (depth of field:1.1), " +
                "((fullbody, standing)), ((" + s + ", solo, fashion)), " + "( " + prompt + ")".toLowerCase() + ",  <lora:fashigirl-v5.5-lora-naivae-64dim:0.4>";
    }

}
