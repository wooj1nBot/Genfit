package com.realese.genfit.items;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
public class Cody {
    private int imageURL;
    private String nick_name;
    private String text_tag;
    private String likes;
    private String GPT_texts;

    private int profile_img;
    private boolean isHeartFilled;
    long cur_time;

    public Cody(int imageURL, String nick_name, String text_tag, String likes, String GPT_texts, int profile_img, boolean isHeartFilled) {
        this.imageURL = imageURL;
        this.nick_name = nick_name;
        this.text_tag = text_tag;
        this.likes = likes;
        this.GPT_texts = GPT_texts;
        this.profile_img = profile_img;
        this.isHeartFilled = isHeartFilled;
        this.cur_time = System.currentTimeMillis();
    }
    // ------ << start Getter & Setter >> ---------- //

    public void setImageURL(int imageURL) {
        this.imageURL = imageURL;
        codyUpload(imageURL);
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public void setText_tag(String text_tag) {
        this.text_tag = text_tag;
    }

    public void setProfile_img(int profile_img) {
        this.profile_img = profile_img;
    }

    public int getImageURL() {
        return imageURL;
    }

    public String getNick_name() {
        return nick_name;
    }

    public String getText_tag() {
        return text_tag;
    }

    public String getLikes() {
        return likes;
    }

    public int getProfile_img() {
        return profile_img;
    }

    public boolean isHeartFilled() {
        return isHeartFilled;
    }
    public void setHeartFilled(boolean heartFilled) {
        isHeartFilled = heartFilled;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    // ------- << end Setter & Getter >> ---------- //

    private void codyUpload(int imageURL) {
        /*
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        String file_name = nick_name + cur_time + ".jpg";

        StorageReference fileRef = storageRef.child(file_name);

        fileRef.putFile(Uri.parse(String.valueOf(imageURL)))
                .addOnSuccessListener(taskSnapshot -> {

                })
                .addOnFailureListener(exception -> {

                });
         */
    }

}