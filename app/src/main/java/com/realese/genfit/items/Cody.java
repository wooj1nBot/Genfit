package com.realese.genfit.items;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cody  {

    public String nick_name;
    public String profile;
    public String uid;
    public String docId;

    public String imageURI;
    public String tags; // 입력 태그 -> 눈에 보이는 태그
    public String prompt; //디퓨전에 돌릴때 넣은 태그
    public Timestamp gen_time;
    public Map<String, String> clothes;

    public int likes;
    public int views;

    public double hot;

    public boolean isShare;

    public boolean isUpload;

    public Cody(){}

    public Cody(String nick_name, String profile, String uid, String tags, String prompt) {
        this.nick_name = nick_name;
        this.profile = profile;
        this.uid = uid;
        this.tags = tags;
        this.prompt = prompt;
    }
    // ------ << start Getter & Setter >> ---------- //

    public String getProfile() {
        return profile;
    }

    public String getUid() {
        return uid;
    }

    public int getLikes() {
        return likes;
    }

    public int getViews() {
        return views;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getTags() {
        return tags;
    }

    public Timestamp getGen_time() {
        return gen_time;
    }

    public String getDocId() {
        return docId;
    }

    public String getImageURI() {
        return imageURI;
    }

    public String getNick_name() {
        return nick_name;
    }

    public boolean getIsShare(){
        return isShare;
    }

    public boolean getIsUpload(){
        return isUpload;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setGen_time(Timestamp gen_time) {
        this.gen_time = gen_time;
    }

    public double getHot() {
        return hot;
    }

    public Map<String, String> getClothes() {
        return clothes;
    }

    // ------- << end Setter & Getter >> ---------- //

    public void share(Context context) {
        if (docId == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("cody").document(docId);
        isShare = true;

        if (Util.isLogin(context)){
            if (isUpload) {
                userDocRef.update("isShare", true);
            }else{
                String file_name = docId + ".jpg";
                File file = new File(context.getCacheDir(), file_name);
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                final StorageReference ref = storageRef.child("users").child(uid).child(docId + ".jpg");
                UploadTask uploadTask = ref.putFile(Uri.fromFile(file));
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task){
                        // Continue with the task to get the download URL
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            Cody.this.imageURI = downloadUri.toString();
                            userDocRef.set(Cody.this);
                            db.collection("users").document(Util.getID(context)).update("codySaveList", FieldValue.arrayUnion(docId));
                        }
                    }
                });
            }
        }

    }

    public void remove(Context context){
        if (imageURI == null || docId == null) return;
        if (isUpload){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userDocRef = db.collection("cody").document(docId);
            final StorageReference ref = storageRef.child("users").child(uid).child(docId + ".jpg");
            ref.delete();
            userDocRef.delete();
            db.collection("users").document(Util.getID(context)).update("codySaveList", FieldValue.arrayRemove(docId));
        }else{
            String file_name = docId + ".jpg";
            File file = new File(context.getFilesDir(), file_name);
            file.delete();
            User user = Util.getUser(context);
            user.codySaveGuestList.remove(this);
            Util.setUser(context, user);
        }
    }

    public void generate(Context context, byte[] data){
        gen_time = new Timestamp(new Date(System.currentTimeMillis()));
        isShare = false;

        docId = String.valueOf(System.currentTimeMillis());
        String file_name = docId + ".jpg";
        File file = new File(context.getFilesDir(), file_name);
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            stream.write(data);
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (file.exists()) {
            imageURI = Uri.fromFile(file).toString();
        }

        if (Util.isLogin(context)){
            isUpload = true;
            Uri uri = Uri.parse(imageURI);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DocumentReference userDocRef = db.collection("cody").document();
            docId = userDocRef.getId();

            final StorageReference ref = storageRef.child("users").child(uid).child(docId + ".jpg");
            UploadTask uploadTask = ref.putFile(uri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task){
                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Cody.this.imageURI = downloadUri.toString();
                        userDocRef.set(Cody.this);
                        db.collection("users").document(Util.getID(context)).update("codySaveList", FieldValue.arrayUnion(docId));
                    }
                }
            });
        }else{
            isUpload = false;
            User user = Util.getUser(context);
            user.addCodySaveGuestList(Cody.this);
            Util.setUser(context, user);
        }
    }

}