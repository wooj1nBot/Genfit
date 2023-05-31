package com.realese.genfit.Frags;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.realese.genfit.items.Cody;
import com.realese.genfit.R;
import com.realese.genfit.items.User;
import com.realese.genfit.items.Util;

public class FragHome extends Fragment {
    RecyclerView rc;
    DocumentSnapshot lastDoc;
    List<Cody> codies;
    List<String> likes;
    List<Cody> sub;
    private long mLastClickTime = 0;
    private long mLastClickTime2 = 0;

    public int sorttype = 0;
    LottieAnimationView lottieAnimationView;

    GridViewAdapter_Home adapter;
    User user;
    LottieAnimationView main_anim;
    boolean isVisible = true;
    int scrollDist = 0;
    static final float MINIMUM = 25;

    RelativeLayout bar;
    ImageView iv_sort;
    TextView tv_sort;

    public FragHome(RelativeLayout bar) {
        // Required empty public constructor
        this.bar = bar;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_home, container, false);

        rc = view.findViewById(R.id.main_grid);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        rc.setLayoutManager(manager);
        rc.addItemDecoration(new SpacesItemDecoration(10));

        lottieAnimationView = view.findViewById(R.id.animation_view);
        main_anim = view.findViewById(R.id.av);
        RelativeLayout sort_view = view.findViewById(R.id.sort_mode);
        iv_sort = view.findViewById(R.id.sort_img);
        tv_sort = view.findViewById(R.id.sort_text);



        sort_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(sort_view);
            }
        });

        rc.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isVisible && scrollDist > MINIMUM) {
                    bar.animate().translationY(bar.getHeight()).setInterpolator(new AccelerateInterpolator(2)).withLayer(). start ();
                    scrollDist = 0;
                    isVisible = false;
                }
                else if (!isVisible && scrollDist < -MINIMUM) {
                    bar.animate().translationY(0).setInterpolator(new AccelerateInterpolator(2)).withLayer(). start ();
                    scrollDist = 0;
                    isVisible = true;
                }

                if ((isVisible && dy > 0) || (!isVisible && dy < 0)) {
                    scrollDist += dy;
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!rc.canScrollVertically(1)) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime2 < 1000) {
                        return;
                    }
                    mLastClickTime2 = SystemClock.elapsedRealtime();
                    if(sub != null) {
                        if (sub.size() == 4) {
                            lottieAnimationView.setVisibility(View.VISIBLE);
                            lottieAnimationView.playAnimation();
                        }
                        sortByScroll(sorttype);
                    }
                }
            }
        });

        likes = new ArrayList<>();
        codies = new ArrayList<>();
        sub = new ArrayList<>();

        adapter = new GridViewAdapter_Home(getContext(), codies, likes);
        rc.setAdapter(adapter);

        sorttype = 0;




        return view;
    }

    public static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }

    public void showDialog(View view){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View mdialog = inflater.inflate(R.layout.sort_dialog, null);
        AlertDialog.Builder buider = new AlertDialog.Builder(getContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        buider.setView(mdialog);
        buider.setCancelable(true);
        Dialog dialog = buider.create();
        dialog.show();

        LinearLayout r1 = dialog.findViewById(R.id.r1);
        LinearLayout r2 = dialog.findViewById(R.id.r2);
        LinearLayout r3 = dialog.findViewById(R.id.r3);

        r1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sorttype = 0;
                sortBy(sorttype);
                dialog.dismiss();
                tv_sort.setText("최신 순");
                iv_sort.setImageResource(R.drawable.time);

            }
        });

        r2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sorttype = 1;
                sortBy(sorttype);
                dialog.dismiss();
                tv_sort.setText("인기 순");
                iv_sort.setImageResource(R.drawable.hot);
            }
        });

        r3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sorttype = 2;
                sortBy(sorttype);
                dialog.dismiss();
                tv_sort.setText("추천 순");
                iv_sort.setImageResource(R.drawable.favorite_48px);
            }
        });

        Window window = dialog.getWindow();
        window.setGravity(Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = view.getRight() - params.width;
        params.y = (int) (view.getY() + ConvertDPtoPX(getContext(), 200));
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onStart() {
        super.onStart();
        getUserData();
        sortBy(sorttype);
    }

    public static int ConvertDPtoPX(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public void getUserData(){
        if (Util.isLogin(getContext())){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(Util.getID(getContext())).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    user = documentSnapshot.toObject(User.class);
                    likes.addAll(user.codyLikeList);
                    Log.d("likes", String.valueOf(likes.size()));
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    public void sortBy(int sort){
        String sort_type = "";
        switch (sort){
            case 0:
                sort_type = "gen_time";
                break;
            case 1:
                sort_type = "hot";
                break;
            case 2:
                sort_type = "likes";
                break;
        }
        main_anim.setVisibility(View.VISIBLE);
        main_anim.playAnimation();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("cody").orderBy(sort_type, Query.Direction.DESCENDING).whereEqualTo("isShare", true).limit(4);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                main_anim.setVisibility(View.GONE);
                main_anim.cancelAnimation();

                if (task.isSuccessful()) {
                    QuerySnapshot queryDocumentSnapshots = task.getResult();
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        codies.clear();
                        sub.clear();

                        for (DocumentSnapshot documentSnapshot : list) {
                            Cody cody = documentSnapshot.toObject(Cody.class);
                            sub.add(cody);
                            Date date = cody.gen_time.toDate();
                            double hot = Util.hot(cody.likes,date,new Date(System.currentTimeMillis()));
                            db.collection("cody").document(cody.docId).update("hot", hot);
                        }

                        if (sub.size() > 0) {//1개라도 있으면 불러옴
                            rc.setVisibility(View.VISIBLE);
                            codies.addAll(sub);
                            lastDoc = list.get(list.size() - 1);
                            adapter = new GridViewAdapter_Home(getContext(), codies, likes);
                            rc.setAdapter(adapter);
                        }else{
                            rc.setVisibility(View.GONE);
                        }
                    } else {
                        rc.setVisibility(View.GONE);
                    }
                } else {
                    rc.setVisibility(View.GONE);
                }
            }
        });
    }

    public void sortByScroll(int sort) {
        String sort_type = "";
        lottieAnimationView.setVisibility(View.VISIBLE);
        lottieAnimationView.playAnimation();

        switch (sort){
            case 0:
                sort_type = "gen_time";
                break;
            case 1:
                sort_type = "hot";
                break;
            case 2:
                sort_type = "likes";
                break;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("cody").orderBy(sort_type, Query.Direction.DESCENDING).whereEqualTo("isShare", true).startAfter(lastDoc).limit(4);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                lottieAnimationView.setVisibility(View.GONE);
                lottieAnimationView.cancelAnimation();

                if (task.isSuccessful()) {
                    QuerySnapshot queryDocumentSnapshots = task.getResult();
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        sub.clear();

                        for (DocumentSnapshot documentSnapshot : list) {
                            Cody cody = documentSnapshot.toObject(Cody.class);
                            sub.add(cody);
                            Date date = cody.gen_time.toDate();
                            double hot = Util.hot(cody.likes,date,new Date(System.currentTimeMillis()));
                            db.collection("cody").document(cody.docId).update("hot", hot);
                            Log.d("last", cody.gen_time.toString());
                        }

                        if (sub.size() > 0) {//1개라도 있으면 불러옴
                            lastDoc = list.get(list.size() - 1);
                            adapter.addList(sub);
                        }
                    }
                }
            }
        });
    }


}

