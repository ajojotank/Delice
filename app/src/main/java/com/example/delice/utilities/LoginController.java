package com.example.delice.utilities;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;


public class LoginController extends Application {
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String LOGGED_IN_KEY = "loggedIn";
    private static final String USER_ID_KEY = "userId";  // Key for the user ID

    private String userId ;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize global variables or services here if needed
    }

    public boolean isLogged() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getBoolean(LOGGED_IN_KEY, false); // Default to false if not found
    }

    public void setLogged(boolean logged) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(LOGGED_IN_KEY, logged);
        editor.apply(); // Apply changes asynchronously
    }

    public void setUserId(String userId) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(USER_ID_KEY, userId);
        editor.apply(); // Save the user ID asynchronously
    }

    public String getUserId() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getString(USER_ID_KEY, ""); // Default to an empty string if not found
    }

    public void logout() {
        setLogged(false); // Reset the logged-in status
        setUserId(""); // Clear the user ID on logout
    }
}
