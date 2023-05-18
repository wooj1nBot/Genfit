package com.realese.Frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.realese.R;

import java.util.ArrayList;
import java.util.List;

public class FragMyPageFavorite extends Fragment {

    GridView gridView;
    List<Item> itemList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_list, container, false);

        gridView = view.findViewById(R.id.grid_mypage_favorite);

        itemList = new ArrayList<>();
        itemList.add(new Item(R.drawable.img_1_1, "MK474", "# 꾸안꾸", "120", R.drawable.basic_img, false));

        GridViewAdapter_MyPage adapter_myPage = new GridViewAdapter_MyPage(getContext(), itemList);
        gridView.setAdapter(adapter_myPage);

        return view;
    }
}
