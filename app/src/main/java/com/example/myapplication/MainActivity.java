package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView appName;
    private View  viewTop,viewBot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appName= findViewById(R.id.appName);
        viewTop = findViewById(R.id.viewTop);
        viewBot = findViewById(R.id.viewBot);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        appName.animate().translationX(3000).setDuration(500).setStartDelay(1500);
        viewTop.animate().translationY(-1600).setDuration(500).setStartDelay(1500);
        viewBot.animate().translationY(1600).setDuration(500).setStartDelay(1500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();//when pressed back splash won't be shown
            }
        },3000);


    }
}