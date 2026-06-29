package com.saqi.time_scheduler.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.saqi.time_scheduler.R;

public class SplashScreen extends AppCompatActivity {
    private ImageView logo;
    private TextView text;
    private static int splashTimeOut = 3000;
    SpinKitView spinKitView;
    private  int progressStatus=0;
    private Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        logo = (ImageView) findViewById(R.id.icon1);
        text = findViewById(R.id.sign);
        spinKitView = findViewById(R.id.spin_kit);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2000);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, Login.class);
                startActivity(i);
                finish();
            }
        }, splashTimeOut);

        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.topanimation);
        Animation myanima = AnimationUtils.loadAnimation(this, R.anim.bottomanimation);
        logo.startAnimation(myanim);
        text.startAnimation(myanima);

    }
}