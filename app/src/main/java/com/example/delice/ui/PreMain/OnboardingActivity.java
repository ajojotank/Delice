package com.example.delice.ui.PreMain;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.delice.MainActivity;
import com.example.delice.R;
import com.example.delice.utilities.LoginController;

public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is already logged in and redirect if true
        if (isUserLoggedIn()) {
            startMainActivity();
            return; // Exit early to avoid loading the onboarding layout
        }

        setContentView(R.layout.activity_onboarding);

        // Setup button click listener to start LoginActivity
        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> startLoginActivity());
    }

    private boolean isUserLoggedIn() {
        LoginController loginController = (LoginController) getApplicationContext();
        return loginController.isLogged();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
