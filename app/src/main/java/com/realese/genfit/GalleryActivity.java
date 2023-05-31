package com.realese.genfit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.realese.genfit.items.Cody;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        Uri uri = Uri.parse(intent.getStringExtra("uri"));
        String t = intent.getStringExtra("tags");

        PhotoView photoView = findViewById(R.id.photoview);
        TextView tv_tag = findViewById(R.id.tv_tag);
        ImageView back = findViewById(R.id.back);

        Glide.with(this).load(uri).into(photoView);

        if (!t.equals("")) {
            String tag = t.replace("\"", "").replace(" ", "");
            String[] tags = tag.split(",");
            StringBuilder complete = new StringBuilder();
            ArrayList<Integer> pos = new ArrayList<>();
            for (int i = tags.length - 1; i > 0; i--) {
                pos.add(complete.length() + 2);
                complete.append("  #  ").append(tags[i]);
            }
            tv_tag.setText(complete.toString());
            Spannable spannable = (Spannable) tv_tag.getText();
            for (int p : pos) {
                spannable.setSpan(new RelativeSizeSpan(1.15f), p, p + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}