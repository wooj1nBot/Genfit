package com.realese.Preset;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.realese.Frags.FragMain;
import com.realese.R;

public class PresetActivity extends AppCompatActivity {

    ImageView back_btn;
    Button finish_btn;
    TextView next_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preset);

        back_btn = findViewById(R.id.bback);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PresetGenderActivity.class);
                startActivity(intent);
                finish();
            }
        });

        finish_btn = findViewById(R.id.finish);
        finish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        next_btn = findViewById(R.id.next);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FragMain.class);
                startActivity(intent);
                finish();
            }
        });
    }

}