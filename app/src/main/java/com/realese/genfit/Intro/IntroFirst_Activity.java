package com.realese.genfit.Intro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.realese.genfit.Preset.PresetGenderActivity;
import com.realese.genfit.R;
import com.realese.genfit.items.Util;

public class IntroFirst_Activity extends AppCompatActivity {

    RelativeLayout intro_first_next;
    TextView intro_first_skip_textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_first);

        intro_first_next = findViewById(R.id.intro_first_next);
        intro_first_skip_textView = findViewById(R.id.intro_first_skip_textView);

        intro_first_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("pref", MODE_PRIVATE).edit();
                editor.putBoolean("isIntro", true);
                editor.apply();
                startActivity(new Intent(getApplicationContext(), IntroSecond_Activity.class));
                finish();
                overridePendingTransition(R.anim.horizon_enter, R.anim.none);
            }
        });

        intro_first_skip_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("pref", MODE_PRIVATE).edit();
                editor.putBoolean("isIntro", true);
                editor.apply();
                startActivity(new Intent(getApplicationContext(), PresetGenderActivity.class));
                finish();
                overridePendingTransition(R.anim.horizon_enter, R.anim.none);
            }
        });

    }
}