package com.realese.Frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.realese.R;

import java.util.ArrayList;
import java.util.List;

public class FragMyPage extends Fragment {

    List<Item> itemList;
    GridView gridView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_mypage, container, false);

        gridView = view.findViewById(R.id.grid_mypage);

        itemList = new ArrayList<>();
        itemList.add(new Item(R.drawable.img_1_1, "MK474", "# 꾸안꾸", "120", R.drawable.basic_img, false));
        itemList.add(new Item(R.drawable.img_1_1, "MK474", "# 꾸안꾸", "120", R.drawable.basic_img, false));
        itemList.add(new Item(R.drawable.img_1_1, "MK474", "# 꾸안꾸", "120", R.drawable.basic_img, false));
        itemList.add(new Item(R.drawable.img_1_1, "MK474", "# 꾸안꾸", "120", R.drawable.basic_img, false));
        itemList.add(new Item(R.drawable.img_1_1, "MK474", "# 꾸안꾸", "120", R.drawable.basic_img, false));


        GridViewAdapter_MyPage adapter_myPage = new GridViewAdapter_MyPage(getContext(), itemList);
        gridView.setAdapter(adapter_myPage);


        return view;
    }
}
