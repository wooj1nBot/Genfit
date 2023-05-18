package com.realese.Frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.fragment.app.Fragment;

import com.realese.R;

import java.util.ArrayList;
import java.util.List;

public class FragHome extends Fragment {
    GridView gridView;
    List<Item> itemList;

    public FragHome() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_home, container, false);

        gridView = view.findViewById(R.id.main_grid);

        itemList = new ArrayList<>();
        itemList.add(new Item(R.drawable.img_1_1, "MK474", "# 꾸안꾸", "120", R.drawable.basic_img, false));
        itemList.add(new Item(R.drawable.img_1_2, "김우진", "# 어려워", "324", R.drawable.basic_img, false));
        itemList.add(new Item(R.drawable.img_2_1, "No Time", "# 시티", "1200", R.drawable.basic_img,false));

        GridViewAdapter adapter = new GridViewAdapter(getContext(), itemList);
        gridView.setAdapter(adapter);

        adapter.setOnHeartClickListener(new GridViewAdapter.OnHeartClickListener() {
            @Override
            public void onHeartClick(int position) {

            }
        });

        return view;
    }
}

