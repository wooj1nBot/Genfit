package com.realese.genfit.Frags;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.realese.genfit.Chat.ChatActivity;
import com.realese.genfit.Intro.IntroFirst_Activity;
import com.realese.genfit.Preset.PresetGenderActivity;
import com.realese.genfit.R;
import com.realese.genfit.items.Util;

public class MainActivity extends AppCompatActivity {


    private final String TAG = "메인";

    Fragment fragment_home;
    Fragment fragment_mypage;

    public MainActivity(){

    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);

        if (!preferences.getBoolean("isIntro", false)){
            Intent intent = new Intent(this, IntroFirst_Activity.class);
            startActivity(intent);
            finish();
            return;
        }
        if (Util.getID(this).equals("")){
            Intent intent = new Intent(this, IntroFirst_Activity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.bottom_menu);

        RelativeLayout bottombar = findViewById(R.id.bar);

        fragment_home = new FragHome(bottombar);
        fragment_mypage = new FragMyPageMain();

        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment_home).commitAllowingStateLoss();

        FloatingActionButton chat_btn = findViewById(R.id.chat);
        ImageView home = findViewById(R.id.home);
        ImageView profile = findViewById(R.id.profile);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "바텀 네비게이션 홈 클릭");

                getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment_home).commitAllowingStateLoss();
            }
        });

        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ChatActivity.class));
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "바텀 네비게이션 마이메뉴 클릭");

                getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment_mypage).commitAllowingStateLoss();
            }
        });
    }
}
