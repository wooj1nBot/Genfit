package com.realese.Intro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.realese.Preset.PresetGenderActivity;
import com.realese.R;

public class IntroSecond_Activity extends AppCompatActivity {
    TextView intro_sec_skip_textView;
    RelativeLayout intro_sec_next, intro_sec_prev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_second2);

        intro_sec_skip_textView = findViewById(R.id.intro_sec_skip_textView);
        intro_sec_next = findViewById(R.id.intro_sec_next);
        intro_sec_prev = findViewById(R.id.intro_sec_prev);

        intro_sec_skip_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PresetGenderActivity.class));
                finish();
            }
        });

        intro_sec_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), IntroThird_Activity.class));
                finish();
            }
        });

        intro_sec_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), IntroFirst_Activity.class));
                finish();
            }
        });
    }
}