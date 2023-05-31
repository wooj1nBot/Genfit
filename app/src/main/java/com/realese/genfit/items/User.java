package com.realese.genfit.items;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import android.net.Uri;
import android.provider.Settings;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import retrofit2.http.Url;

public class User {

    public static final int SEX_MALE = 0;
    public static final int SEX_FEMALE = 1;

    public static final String[] SEX_STRING = {"남자", "여자"};

    public String nickname;
    public String profile;
    public String uid;
    public int height;
    public int weight;
    public int age;
    public int sex;
    public List<String> codyLikeList;
    public List<String> codySaveList;
    public List<Cody> codySaveGuestList;

    private boolean GUEST = true;
    public User() {}

    public User(String uid, int sex) {
        this.uid = uid;
        this.codyLikeList = new ArrayList<>();
        this.codySaveList = new ArrayList<>();
        this.codySaveGuestList = new ArrayList<>();
        this.sex = sex;
        this.nickname = nNick();
    }

    public User(String uid, int sex, int height, int weight, int age) {
        this(uid, sex);
        this.height = height;
        this.weight = weight;
        this.age = age;
        this.GUEST = true;
    }

    public void uploadUserToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("users").document(uid);
        userDocRef.set(this);
    }

    public boolean isGUEST() {
        return GUEST;
    }

    public void setGUEST(boolean GUEST) {
        this.GUEST = GUEST;
    }


    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public List<String> getCodyLikeList() {
        return codyLikeList;
    }

    public List<String> getCodySaveList() {
        return codySaveList;
    }

    public void addCodyLikeList(String codyId){
        codyLikeList.add(codyId);
    }
    public void addCodySaveList(String codyId){
        codySaveList.add(codyId);
    }

    public void addCodySaveGuestList(Cody cody){
        codySaveGuestList.add(cody);
    }

    public String getProfile() {
        return profile;
    }

    public String getUid() {
        return uid;
    }

    public List<Cody> getCodySaveGuestList() {
        return codySaveGuestList;
    }

    public String getNickname() {
        return nickname;
    }

    public static String nNick() {
        List<String> 닉 = Arrays.asList("기분나쁜","기분좋은","신바람나는","상쾌한","짜릿한","그리운","자유로운","서운한","울적한","비참한","위축되는","긴장되는","두려운","당당한","배부른","수줍은","창피한","멋있는",
                "열받은","심심한","잘생긴","이쁜","시끄러운");
        List<String> 네임 = Arrays.asList("사자","코끼리","호랑이","곰","여우","늑대","너구리","침팬치","고릴라","참새","고슴도치","강아지","고양이","거북이","토끼","앵무새","하이에나","돼지","하마","원숭이","물소","얼룩말","치타",
                "악어","기린","수달","염소","다람쥐","판다");
        Collections.shuffle(닉);
        Collections.shuffle(네임);
        String text = 닉.get(0)+네임.get(0) + new Random().nextInt(1000);
        return text;
    }
}
