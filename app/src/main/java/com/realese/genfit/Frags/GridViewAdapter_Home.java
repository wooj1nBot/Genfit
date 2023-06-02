package com.realese.genfit.Frags;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.realese.genfit.Chat.ChatActivity;
import com.realese.genfit.GalleryActivity;
import com.realese.genfit.items.Cody;
import com.realese.genfit.R;
import com.realese.genfit.items.User;
import com.realese.genfit.items.Util;

import java.util.*;

public class GridViewAdapter_Home extends RecyclerView.Adapter<GridViewAdapter_Home.ViewHolder> {

    private Context context;
    private List<Cody> items;
    private List<String> likes;


    public GridViewAdapter_Home(Context context, List<Cody> items, List<String> likes) {
        this.context = context;
        this.items = items;
        this.likes = likes;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        View view = inflater.inflate(R.layout.grid_item_home, parent, false);
        return new ViewHolder(view);
    }

    public void addList(List<Cody> codies){
        items.addAll(codies);
        notifyItemChanged(0, items.size());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cody item = items.get(position);
        holder.itemView.setTag(item);

        Glide.with(context).load(Uri.parse(item.imageURI)).into(holder.imageView);
        if (item.profile != null) {
            Glide.with(context).load(Uri.parse(item.profile)).into(holder.profile_img);
        } else {
            holder.profile_img.setImageResource(R.drawable.basic_img);
        }

        Log.d("likes", item.tags);

        if (likes.contains(item.docId)) {
            holder.heartImageView.setImageResource(R.drawable.favorite_filled);
            holder.heartImageView.setImageTintList(ColorStateList.valueOf(Color.parseColor("#F44336")));
        } else {
            holder.heartImageView.setImageResource(R.drawable.favorite_48px);
            holder.heartImageView.setImageTintList(ColorStateList.valueOf(Color.parseColor("#A7A7A7")));
        }

        holder.textView_nickname.setText(item.nick_name);
        holder.likes.setText(item.likes + "");
        holder.heartImageView.setTag(new Object[]{item, holder.likes});


        if (item.tags.equals("")){
            holder.textView_tagging.setText("");
        }else{
            String tag = item.tags.replace("\n", "");
            holder.textView_tagging.setText(tag);
            Spannable spannable = (Spannable) holder.textView_tagging.getText();
            for (int i = 0; i < tag.length(); i++) {
                if (tag.charAt(i) == '#'){
                    spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#2E2E2E")), i, i+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable.setSpan(new RelativeSizeSpan(1.15f), i, i+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }

        holder.heartImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object[] ob = (Object[]) v.getTag();
                ImageView heart = (ImageView) v;
                Cody cody = (Cody) ob[0];
                TextView tv_likes = (TextView) ob[1];
                String id = Util.getID(context);
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                if (likes.contains(cody.docId)) {
                    likes.remove(cody.docId);
                    heart.setImageResource(R.drawable.favorite_48px);
                    heart.setImageTintList(ColorStateList.valueOf(Color.parseColor("#A7A7A7")));
                    if (Util.isLogin(context)) {

                        db.collection("cody").document(cody.docId).update("likes", FieldValue.increment(-1));
                        db.collection("users").document(id).update("codyLikeList", FieldValue.arrayRemove(cody.docId));
                    } else {
                        User user = Util.getUser(context);
                        user.codyLikeList.remove(cody.docId);
                        Util.setUser(context, user);
                    }

                    db.collection("cody").document(cody.docId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Cody c = documentSnapshot.toObject(Cody.class);
                            Date date = c.gen_time.toDate();
                            double hot = Util.hot(c.likes,date,new Date(System.currentTimeMillis()));
                            db.collection("cody").document(c.docId).update("hot", hot);
                        }
                    });

                    tv_likes.setText(String.valueOf(--cody.likes));
                } else {
                    likes.add(cody.docId);
                    heart.setImageResource(R.drawable.favorite_filled);
                    heart.setImageTintList(ColorStateList.valueOf(Color.parseColor("#F44336")));
                    if (Util.isLogin(context)) {
                        db.collection("cody").document(cody.docId).update("likes", FieldValue.increment(1));
                        db.collection("users").document(id).update("codyLikeList", FieldValue.arrayUnion(cody.docId));
                    } else {
                        User user = Util.getUser(context);
                        user.codyLikeList.add(cody.docId);
                        Util.setUser(context, user);
                    }
                    tv_likes.setText(String.valueOf(++cody.likes));

                    db.collection("cody").document(cody.docId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Cody c = documentSnapshot.toObject(Cody.class);
                            Date date = c.gen_time.toDate();
                            double hot = Util.hot(c.likes,date,new Date(System.currentTimeMillis()));
                            db.collection("cody").document(c.docId).update("hot", hot);
                        }
                    });
                }


            }
        });
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void showDialog(Cody cody){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View mdialog = inflater.inflate(R.layout.cody_item_dialog, null);
        AlertDialog.Builder buider = new AlertDialog.Builder(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        buider.setView(mdialog);
        buider.setCancelable(true);
        Dialog dialog = buider.create();
        dialog.show();

        ImageView imageView = mdialog.findViewById(R.id.image);
        TextView tv_tag = mdialog.findViewById(R.id.tv_tag);
        ImageView heart = mdialog.findViewById(R.id.heart_shape);
        TextView tv_like = mdialog.findViewById(R.id.grid_likes);
        TextView tv_nick = mdialog.findViewById(R.id.grid_text_nickname);


        TextView tv_1 = mdialog.findViewById(R.id.tv_1);
        TextView tv_2 = mdialog.findViewById(R.id.tv_2);
        TextView tv_3 = mdialog.findViewById(R.id.tv_3);
        TextView tv_4 = mdialog.findViewById(R.id.tv_4);

        RecyclerView rc = mdialog.findViewById(R.id.rc);

        LottieAnimationView animationView = mdialog.findViewById(R.id.av);
        animationView.setVisibility(View.VISIBLE);
        animationView.playAnimation();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cody").document(cody.docId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                animationView.setVisibility(View.GONE);
                animationView.cancelAnimation();
                if (task.isSuccessful() && task.getResult().exists()){
                    Cody c = task.getResult().toObject(Cody.class);
                    Glide.with(context).load(Uri.parse(c.imageURI)).into(imageView);
                    Log.d("gpttag", c.tags);
                    if (!c.tags.equals("")) {
                        String tag = c.tags.replace("\n", "");
                        tv_tag.setText(tag);
                        Spannable spannable = (Spannable) tv_tag.getText();
                        for (int i = 0; i < tag.length(); i++) {
                           if (tag.charAt(i) == '#'){
                               spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#2E2E2E")), i, i+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                               spannable.setSpan(new RelativeSizeSpan(1.15f), i, i+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                           }
                        }
                    }
                    if (likes.contains(cody.docId)){
                        heart.setImageResource(R.drawable.favorite_filled);
                        heart.setImageTintList(ColorStateList.valueOf(Color.parseColor("#F44336")));
                    }else{
                        heart.setImageResource(R.drawable.favorite_48px);
                        heart.setImageTintList(ColorStateList.valueOf(Color.parseColor("#A7A7A7")));
                    }

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, GalleryActivity.class);
                            intent.putExtra("uri", cody.imageURI);
                            intent.putExtra("tags", cody.tags);
                            context.startActivity(intent);
                        }
                    });

                    tv_like.setText(cody.likes + "");
                    tv_nick.setText(cody.nick_name);
                    if (cody.clothes != null) {
                        if (cody.clothes.containsKey("0")) {
                            tv_1.setText(cody.clothes.get("0").replace("\n", "").trim());
                        }
                        if (cody.clothes.containsKey("1")) {
                            tv_2.setText(cody.clothes.get("1").replace("\n", "").trim());
                        }
                        if (cody.clothes.containsKey("2")) {
                            tv_3.setText(cody.clothes.get("2").replace("\n", "").trim());
                        }
                        if (cody.clothes.containsKey("3")) {
                            tv_4.setText(cody.clothes.get("3").replace("\n", "").trim());
                        }
                    }

                    Window window = dialog.getWindow();
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

                }
            }
        });







    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView heartImageView;
        ImageView imageView;
        ImageView profile_img;
        TextView textView_tagging;
        TextView textView_nickname;
        TextView likes;
        ViewHolder(View itemView) {
            super(itemView);
            heartImageView = itemView.findViewById(R.id.heart_shape);
            imageView = itemView.findViewById(R.id.grid_imageView);
            profile_img = itemView.findViewById(R.id.chat_img);
            textView_tagging = itemView.findViewById(R.id.grid_text_tagging);
            textView_nickname = itemView.findViewById(R.id.grid_text_nickname);
            likes = itemView.findViewById(R.id.grid_likes);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cody c = (Cody) v.getTag();
                    showDialog(c);
                }
            });
        }
    }

}
