package com.realese.genfit.Frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.realese.genfit.R;

import java.util.List;

public class FragMyPageMain extends Fragment {

    List<Item> itemList;
    Fragment grid_frag_notes, grid_frag_favorites;

    TabLayout tab_bar;

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.activity_mypage, container, false);

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

    }