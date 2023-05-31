package com.realese.genfit.Frags;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.realese.genfit.items.Cody;
import com.realese.genfit.R;
import com.realese.genfit.items.User;
import com.realese.genfit.items.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FragMtPageNotes extends Fragment {
    RecyclerView rc;
    List<Cody> itemList;
    List<String> likes;

    User user;

    int count = 0;
    GridViewAdapter_MyPage adapter_myPage;
    LottieAnimationView main_anim;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);

        rc = view.findViewById(R.id.grid_mypage_notes);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 3);
        rc.setLayoutManager(manager);
        rc.addItemDecoration(new FragHome.SpacesItemDecoration(1));

        likes = new ArrayList<>();
        itemList = new ArrayList<>();
        main_anim = view.findViewById(R.id.av);

        adapter_myPage = new GridViewAdapter_MyPage(getContext(), itemList, likes);
        rc.setAdapter(adapter_myPage);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getUserData();
    }

    public void getUserData() {
        if (Util.isLogin(getContext())) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            main_anim.setVisibility(View.VISIBLE);
            main_anim.playAnimation();
            count = 0;

            db.collection("users").document(Util.getID(getContext())).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    user = documentSnapshot.toObject(User.class);
                    likes.addAll(user.codyLikeList);
                    itemList.clear();

                    if (user.codySaveList.size() == 0){
                        main_anim.setVisibility(View.GONE);
                        main_anim.cancelAnimation();
                        return;
                    }

                    for (String doc : user.codySaveList) {
                        db.collection("cody").document(doc).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                count++;
                                if (task.isSuccessful() && task.getResult().exists()) {
                                    itemList.add(task.getResult().toObject(Cody.class));
                                }
                                if (count == user.codySaveList.size()) {
                                    main_anim.setVisibility(View.GONE);
                                    main_anim.cancelAnimation();
                                    itemList.sort(new Comparator<Cody>() {
                                        @Override
                                        public int compare(Cody o1, Cody o2) {
                                            return o2.gen_time.compareTo(o1.gen_time);
                                        }
                                    });
                                    adapter_myPage.notifyDataSetChanged();
                                }
                                if (itemList.size() == 0){
                                    main_anim.setVisibility(View.GONE);
                                    main_anim.cancelAnimation();
                                }
                            }
                        });
                    }
                }
            });
        }
    }
}


