package com.example.kts.ui.splash;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.kts.R;
import com.example.kts.ui.login.LoginActivity;
import com.example.kts.ui.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SplashViewModel viewModel = new ViewModelProvider(this).get(SplashViewModel.class);

        viewModel.nextScreenMLiveData.observe(this, nextActivity -> {
            if (nextActivity == SplashViewModel.NextActivity.MAIN) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (nextActivity == SplashViewModel.NextActivity.LOGIN) {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        });
    }
}