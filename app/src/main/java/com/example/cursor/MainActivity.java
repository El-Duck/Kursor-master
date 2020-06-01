package com.example.cursor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button listBtn, confBtn, exitBtn, aboutUsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listBtn = (Button)findViewById(R.id.listBtn);
        confBtn = (Button)findViewById(R.id.settingsBtn);
        exitBtn = (Button)findViewById(R.id.exitBtn);
        aboutUsBtn = (Button)findViewById(R.id.devBtn);
        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAct2();
            }
        });
        confBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openConfAct();
            }
        });
        aboutUsBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) { openAboutUs(); }
        });
        exitBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) { finish(); }
        });
    }

    public void openAct2(){
        Intent i = new Intent(this, SubMainActivity.class);
        startActivity(i);
    }

    public void openConfAct(){
        Intent i = new Intent(this, SettingActivity.class);
        startActivity(i);
    }

    public void openAboutUs(){
        Intent i = new Intent(this, aboutUsActivity.class);
        startActivity(i);
    }
}
