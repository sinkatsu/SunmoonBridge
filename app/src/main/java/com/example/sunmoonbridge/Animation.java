package com.example.sunmoonbridge;

import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Animation extends AppCompatActivity {

    private static int SPLASH_SCREEN = 5000;

    android.view.animation.Animation topAnim;
    android.view.animation.Animation bottomAnim;
    ImageView logo;
    TextView teamN;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_animation);
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        logo = findViewById(R.id.logo);
        teamN = findViewById(R.id.teamname);

        logo.setAnimation(topAnim);
        teamN.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                finish();
            }
        },SPLASH_SCREEN);

    }
}
