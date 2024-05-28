package com.example.delice.ui.PreMain;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.delice.R;
import com.example.delice.utilities.LoginController;
import com.example.delice.utilities.NetworkUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignUpActivity extends AppCompatActivity {

    private EditText nameField, usernameField, passwordField, passwordConfirmField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        nameField = findViewById(R.id.name);
        usernameField = findViewById(R.id.username);
        passwordField = findViewById(R.id.password);
        passwordConfirmField = findViewById(R.id.passwordConfirm);

        setupSignUpButton();
    }

    private void setupSignUpButton() {
        Button signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(this::registerUser);
    }

    private void registerUser(View view) {
        String name = nameField.getText().toString().trim();
        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String passwordConfirm = passwordConfirmField.getText().toString().trim();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(passwordConfirm)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        String hashedPassword = hashPassword(password);
        if (hashedPassword != null) {
            checkUsernameAvailability(name, username, hashedPassword);
        } else {
            Toast.makeText(this, "Error in password processing.", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUsernameAvailability(String name, String username, String hashedPassword) {
        NetworkUtils.checkUsernameExists(this, username, exists -> {
            if (exists) {
                Toast.makeText(this, "Username already taken, please choose another", Toast.LENGTH_LONG).show();
            } else {
                registerNewUser(name, username, hashedPassword);
            }
        }, error -> Toast.makeText(this, "Error checking username availability: " + error.getMessage(), Toast.LENGTH_LONG).show());
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void registerNewUser(String name, String username, String hashedPassword) {
        NetworkUtils.registerUser(this, name, username, hashedPassword,
                response -> {
                    LoginController appController = (LoginController) getApplicationContext();
                    appController.setLogged(true);
                    startActivity(new Intent(this, OnboardingActivity.class));
                    finish();
                },
                error -> {
                    Toast.makeText(this, "Network error: " + error.toString(), Toast.LENGTH_LONG).show();
                }
        );
    }
}