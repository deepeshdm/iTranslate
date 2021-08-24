package com.deepesh.itranslate;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // shows splash screen for 1.5 sec
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        },1500);

    }
}