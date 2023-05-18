package com.realese.Frags;

public class Item {
    private int imageResId;
    private String nick_name;
    private String text_tag;
    private String likes;

    private int profile_img;
    private boolean isHeartFilled;

    public Item(int imageResId, String nick_name, String text_tag, String likes, int profile_img, boolean isHeartFilled) {
        this.imageResId = imageResId;
        this.nick_name = nick_name;
        this.text_tag = text_tag;
        this.likes = likes;
        this.profile_img = profile_img;
        this.isHeartFilled = isHeartFilled;
    }

    public int getImageResId() {
        return imageResId;
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
}