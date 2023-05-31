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

import com.realese.genfit.Frags.MainActivity;
import com.realese.genfit.R;
import com.realese.genfit.items.User;
import com.realese.genfit.items.Util;

public class PresetActivity extends AppCompatActivity {

    ImageView back_btn;
    Button finish_btn;
    TextView next_btn;

    EditText height, weight, age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preset);

        Intent i = getIntent();
        int sex = i.getIntExtra("sex", User.SEX_MALE);

        back_btn = findViewById(R.id.bback);
        height = findViewById(R.id.ki_input);
        weight = findViewById(R.id.muge_input);
        age = findViewById(R.id.nai_input);
        finish_btn = findViewById(R.id.finish);
        next_btn = findViewById(R.id.next);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PresetGenderActivity.class);
                startActivity(intent);
                finish();
            }
        });
        finish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (String.valueOf(height.getText()).length() == 0
                        || String.valueOf(weight.getText()).length() == 0
                        || String.valueOf(age.getText()).length() == 0)
                    Toast.makeText(getApplicationContext(), " 빈 칸을 채워주세요.", Toast.LENGTH_LONG).show();
                else {
                    // Context context, int height, int weight, int age
                    int h = Integer.parseInt(height.getText().toString());
                    int w = Integer.parseInt(weight.getText().toString());
                    int a = Integer.parseInt(age.getText().toString());

                    Util.enrollLoginUser(PresetActivity.this, sex, h, w, a);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.enrollLoginUser(PresetActivity.this, sex);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}