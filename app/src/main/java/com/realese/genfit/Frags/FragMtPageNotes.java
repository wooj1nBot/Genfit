package com.realese.genfit.Frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.realese.genfit.items.Cody;
import com.realese.genfit.R;

import java.util.ArrayList;
import java.util.List;

public class FragMtPageNotes extends Fragment {
    GridView gridView;
    List<Cody> itemList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);

        gridView = view.findViewById(R.id.grid_mypage_notes);

        itemList = new ArrayList<>();
        //itemList.add(new Cody(R.drawable.img_1_1, "MK474", "# 꾸안꾸", "120", R.drawable.basic_img, false));
        //itemList.add(new Cody(R.drawable.img_1_2, "김우진", "# 어려워", "324", R.drawable.basic_img, false));
        //itemList.add(new Cody(R.drawable.img_2_1, "No Time", "# 시티", "1200", R.drawable.basic_img,false));

        GridViewAdapter_MyPage adapter_myPage = new GridViewAdapter_MyPage(getContext(), itemList);
        gridView.setAdapter(adapter_myPage);

        return view;
    }
}
