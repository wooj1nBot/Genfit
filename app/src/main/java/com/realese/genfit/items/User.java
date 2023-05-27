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
import java.util.List;

import retrofit2.http.Url;

public class User {
    private static final String COLLECTION_NAME = "users";

    private FirebaseFirestore db;
    private CollectionReference usersCollection;
    private String User_id;
    private int height;
    private int weight;
    private int age;
    private String sex;
    private List<Uri> codyURiList;
    private boolean GUEST = true;
    public User() {}

    public User(Context context, String sex) {
        this.User_id = GetDeviceId.getDeviceId(context);
        db = FirebaseFirestore.getInstance();
        usersCollection = db.collection(COLLECTION_NAME);
        this.codyURiList = new ArrayList<>(10);
        if (sex.equals("male")) {
            this.height = 175;
            this.weight = 65;
            this.age = 20;
            this.sex = "male";
        }
        else {
            this.height = 160;
            this.weight = 40;
            this.age = 20;
            this.sex = "female";
        }
    }

    public User(Context context, int height, int weight, int age, String sex) {
        this(context, sex);
        this.height = height;
        this.weight = weight;
        this.age = age;
        this.sex = sex;
        this.GUEST = false;
        Log.d("User_init", "ing...");
    }

    public void uploadUserToFirestore() {
        Log.d("User_upload", "User uploaded ... ");
        Log.d("User_upload", "device_name: " + User_id);
        DocumentReference userDocRef = usersCollection.document(User_id);

        userDocRef.set(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("User_upload", "User uploaded successfully");
                        } else {
                            Log.e("User_upload", "Failed to upload user", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("User_upload", "Failed to upload user", e);
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.e("User_upload", "User upload canceled");
                    }
                });
    }

    public boolean isGUEST() {
        return GUEST;
    }

    public void setGUEST(boolean GUEST) {
        this.GUEST = GUEST;
    }

    public List<Uri> getCodyURLList() {
        return codyURiList;
    }

    public void setCodyURLList(List<Uri> codyURLList) {
        this.codyURiList = codyURLList;
    }

    public void setUser_id(String User_id) {
        this.User_id = User_id;
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
