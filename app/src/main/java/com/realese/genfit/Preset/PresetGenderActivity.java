package com.realese.genfit.Preset;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.realese.genfit.R;
import com.realese.genfit.items.User;
import com.realese.genfit.items.Util;

public class PresetGenderActivity extends AppCompatActivity {

    Button man_btn;
    Button woman_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preset_gender);

        man_btn = findViewById(R.id.man);
        woman_btn = findViewById(R.id.woman);
        man_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PresetActivity.class);
                intent.putExtra("sex", User.SEX_MALE);
                startActivity(intent);
                finish();
            }
        });

        woman_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PresetActivity.class);
                intent.putExtra("sex", User.SEX_FEMALE);
                startActivity(intent);
                finish();
            }
        });
    }
}