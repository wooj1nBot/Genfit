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
import com.realese.genfit.items.User;
import com.realese.genfit.items.UserSingleton;

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
        String sex = i.getStringExtra("sex");

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
                    User user = UserSingleton.getInstance(getApplicationContext(),
                            Integer.parseInt(String.valueOf(height.getText())),
                            Integer.parseInt(String.valueOf(weight.getText())),
                            Integer.parseInt(String.valueOf(age.getText())),
                            sex);


                    Intent intent = new Intent(getApplicationContext(), FragMain.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = UserSingleton.getInstance(getApplicationContext(), sex);

                user.uploadUserToFirestore();

                Intent intent = new Intent(getApplicationContext(), FragMain.class);
                startActivity(intent);
                finish();
            }
        });
    }

}