package com.realese.Preset;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.realese.Frags.FragMain;
import com.realese.R;

public class PresetGenderActivity extends AppCompatActivity {

    Button man_btn;
    Button woman_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preset_gender);

        man_btn = findViewById(R.id.man);
        man_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PresetActivity.class);
                startActivity(intent);
                finish();
            }
        });

        woman_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PresetActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}