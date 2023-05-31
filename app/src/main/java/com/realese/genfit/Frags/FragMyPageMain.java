package com.realese.genfit.Frags;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.realese.genfit.items.Cody;
import com.realese.genfit.R;
import com.realese.genfit.items.User;
import com.realese.genfit.items.Util;

import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragMyPageMain extends Fragment {

    CircleImageView profile;
    TextView tv_nick;
    TextView tv_inform;

    Fragment grid_frag_notes, grid_frag_favorites;

    TabLayout tab_bar;

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.activity_mypage, container, false);

            profile = view.findViewById(R.id.profile);
            tv_nick = view.findViewById(R.id.user_id);
            tv_inform = view.findViewById(R.id.user_information);

            grid_frag_notes = new FragMtPageNotes();
            grid_frag_favorites = new FragMyPageFavorite();

            getChildFragmentManager().beginTransaction().replace(R.id.mypage_fragment_layout, grid_frag_notes).commitAllowingStateLoss();

            tab_bar = view.findViewById(R.id.tab_bar);

            tab_bar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab.getPosition() == 0)
                        getChildFragmentManager().beginTransaction().replace(R.id.mypage_fragment_layout, grid_frag_notes).commitAllowingStateLoss();
                    else
                        getChildFragmentManager().beginTransaction().replace(R.id.mypage_fragment_layout, grid_frag_favorites).commitAllowingStateLoss();
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {}

                @Override
                public void onTabReselected(TabLayout.Tab tab) {}
            });


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

            db.collection("users").document(Util.getID(getContext())).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        tv_nick.setText(user.nickname);
                        if (user.age == 0){
                            tv_inform.setText("-cm  · -kg");
                        }else{
                            tv_inform.setText(String.format("%dcm  · %dkg", user.height, user.weight));
                        }
                        if (user.profile != null) {
                            Glide.with(getActivity()).load(Uri.parse(user.profile)).into(profile);
                        }else{
                            profile.setImageResource(R.drawable.userprofile);
                        }
                    }
                }
            });
        }
    }

    }