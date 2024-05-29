package com.example.delice.ui.PreMain;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.delice.R;
import com.example.delice.utilities.LoginController;
import com.example.delice.utilities.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameField, passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        usernameField = findViewById(R.id.username);
        passwordField = findViewById(R.id.password);
        setupButtons();
    }

    private void setupButtons() {
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> loginUser());

        TextView signUpLink = findViewById(R.id.signupLink);
        signUpLink.setOnClickListener(v -> startSignUpActivity());
    }

    private void loginUser() {
        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            Toast.makeText(this, "Error hashing password", Toast.LENGTH_SHORT).show();
            return;
        }

        NetworkUtils.loginUser(this, username, hashedPassword,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("success")) {
                            String userId = jsonResponse.getString("user_id"); // Extract user_id from the response
                            LoginController appController = (LoginController)getApplicationContext();
                            appController.setLogged(true);
                            appController.setUserId(userId); // Store user_id in LoginController

                            startActivity(new Intent(this, OnboardingActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, jsonResponse.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Login failed: " + error.getMessage(), Toast.LENGTH_LONG).show());

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

    private void startSignUpActivity() {
        startActivity(new Intent(this, SignUpActivity.class));
    }
}
