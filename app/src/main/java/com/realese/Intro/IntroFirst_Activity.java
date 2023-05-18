package com.realese.Intro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.realese.Preset.PresetActivity;
import com.realese.Preset.PresetGenderActivity;
import com.realese.R;

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
                startActivity(new Intent(getApplicationContext(), IntroSecond_Activity.class));
                finish();
            }
        });

        intro_first_skip_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PresetGenderActivity.class));
                finish();
            }
        });

    }
}