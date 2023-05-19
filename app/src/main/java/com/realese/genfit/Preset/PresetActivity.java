package com.realese.genfit.Preset;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.realese.genfit.Frags.FragMain;
import com.realese.genfit.R;

public class PresetActivity extends AppCompatActivity {

    ImageView back_btn;
    Button finish_btn;
    TextView next_btn;

    EditText height, weight, age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preset);

        back_btn = findViewById(R.id.bback);
        height = findViewById(R.id.ki_input);
        weight = findViewById(R.id.muge_input);
        age = findViewById(R.id.nai_input);

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
                if (String.valueOf(height.getText()).length() == 0
                        || String.valueOf(weight.getText()).length() == 0
                        || String.valueOf(age.getText()).length() == 0)
                    Toast.makeText(getApplicationContext(), "빈 칸을 채워주세요.", Toast.LENGTH_LONG).show();
                else {
                    Intent intent = new Intent(getApplicationContext(), FragMain.class);
                    startActivity(intent);
                    finish();
                }
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