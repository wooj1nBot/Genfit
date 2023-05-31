package com.realese.genfit.items;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;

public class Util {


    public static void enrollUser(Context context, int sex){
        String uid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        User user = new User(uid, sex);
        setUser(context, user);
        enrollId(context, uid, true);
    }
    public static void enrollUser(Context context, int sex, int height, int weight, int age){
        String uid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        User user = new User(uid, sex, height, weight, age);
        setUser(context, user);
        enrollId(context, uid, true);
    }

    public static void enrollLoginUser(Context context, int sex){
        String uid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        User user = new User(uid, sex);
        enrollId(context, uid, true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(uid).set(user);
    }

    public static void enrollLoginUser(Context context, int sex, int height, int weight, int age){
        String uid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        User user = new User(uid, sex, height, weight, age);
        enrollId(context, uid, true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(uid).set(user);
    }


    public static void enrollId(Context context, String uid, boolean isLogin){
        SharedPreferences.Editor editor = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE).edit();
        editor.putString("uid", uid);
        editor.putBoolean("isLogin", isLogin);
        editor.apply();
    }

    public static String getID(Context context){
        SharedPreferences preferences = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        return preferences.getString("uid", "");
    }

    //비회원 전용
    public static void setUser(Context context, User user){
        Gson gson = new Gson();
        SharedPreferences.Editor editor = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE).edit();
        editor.putString("user", gson.toJson(user));
        editor.apply();
    }

    public static boolean isLogin(Context context){
        SharedPreferences preferences = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        return preferences.getBoolean("isLogin", false);
    }

    public static User getUser(Context context){
        SharedPreferences preferences = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String json = preferences.getString("user", "");
        Gson gson = new Gson();
        return gson.fromJson(json, User.class);
    }

    public static Task<DocumentSnapshot> getLoginUser(Context context){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("users").document(getID(context)).get();
    }

    public static double hot(long score, Date postdate, Date curretdate){
        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        calendar.setTime(postdate);
        calendar1.setTime(curretdate);
        long reqTime = calendar.getTimeInMillis();
        long resTime = calendar1.getTimeInMillis();
        double hour = (resTime - reqTime)/3600000.000;
        float gravity = 1.1f;
        double d = (score) / Math.pow(hour+2,gravity);
        double Score = Math.round(d*100000)/100000.0;
        return Score;
    }

}
