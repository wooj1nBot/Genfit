package com.realese.genfit.Frags;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.realese.genfit.Chat.ChatActivity;
import com.realese.genfit.Intro.IntroFirst_Activity;
import com.realese.genfit.R;

public class FragMain extends AppCompatActivity {
    LinearLayout bottomNavigationItemView_HOME;
    LinearLayout bottomNavigationItemView_MyPage;
    ImageView chat_btn;

    private final String TAG = "메인";

    Fragment fragment_home;
    Fragment fragment_mypage;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);

        if (!preferences.getBoolean("isIntro", false)){
            Intent intent = new Intent(this, IntroFirst_Activity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.bottom_menu);

        fragment_home = new FragHome();
        fragment_mypage = new FragMyPageMain();

        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment_home).commitAllowingStateLoss();

        chat_btn = findViewById(R.id.chat_btn);
        bottomNavigationItemView_HOME = findViewById(R.id.home_btn);
        bottomNavigationItemView_MyPage = findViewById(R.id.my_page_btn);

        bottomNavigationItemView_HOME.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "바텀 네비게이션 홈 클릭");

                getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment_home).commitAllowingStateLoss();
            }
        });

        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ChatActivity.class));
            }
        });
        bottomNavigationItemView_MyPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "바텀 네비게이션 마이메뉴 클릭");

                getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment_mypage).commitAllowingStateLoss();
            }
        });
    }
}
