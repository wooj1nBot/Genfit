package com.realese.genfit.Intro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.realese.genfit.Preset.PresetGenderActivity;
import com.realese.genfit.R;

public class IntroThird_Activity extends AppCompatActivity {

    View intro_thd_start_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_third);

        intro_thd_start_button = findViewById(R.id.intro_thd_start_button);
        intro_thd_start_button.setOnClickListener(new View.OnClickListener() {
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