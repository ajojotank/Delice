package com.example.delice.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.delice.R;
import com.example.delice.utilities.LoginController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddMealActivity extends AppCompatActivity {

    public static final String EXTRA_DAY = "EXTRA_DAY";
    public static final String EXTRA_MEAL_TYPE = "EXTRA_MEAL_TYPE";
    public static final int RESULT_MEAL_ADDED = 1;
    private static final String TAG = "AddMealActivity";
    private RecyclerView recyclerView;
    private MealCardAdapter adapter;
    private List<Meal> meals = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        // Retrieve day and meal type from Intent extras
        String day = getIntent().getStringExtra(EXTRA_DAY);
        String mealType = getIntent().getStringExtra(EXTRA_MEAL_TYPE);

        Log.d(TAG, "Day: " + day);
        Log.d(TAG, "Meal Type: " + mealType);

        recyclerView = findViewById(R.id.addMealRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MealCardAdapter(meals, this, meal -> {
            // Handle meal card click
            addMealToUserPlan(meal, day, mealType);
        });
        recyclerView.setAdapter(adapter);

        // Fetch user's favorite meals
        fetchUserFavorites();
    }

    private void fetchUserFavorites() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            LoginController appController = (LoginController) getApplicationContext();
            String userId = appController.getUserId();

            try {
                URL url = new URL("https://lamp.ms.wits.ac.za/home/s2670867/get_user_favorites.php?user_id=" + userId);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in);
                Log.d(TAG, "Response: " + response);

                parseMeals(response);

                urlConnection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error fetching user favorites", e);
            }
        });
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            Log.e(TAG, "Error reading stream", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing stream", e);
            }
        }
        return sb.toString();
    }

    private void parseMeals(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            List<Meal> fetchedMeals = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject mealJson = jsonArray.getJSONObject(i);
                String id = mealJson.getString("recipe_id");
                String title = mealJson.getString("title");
                String description = mealJson.getString("description");
                String author = mealJson.getString("author");
                String imageUrl = mealJson.getString("image_url");

                fetchedMeals.add(new Meal(title, description, author, imageUrl, id));
            }
            // Update the adapter with the fetched meals
            runOnUiThread(() -> {
                meals.clear();
                meals.addAll(fetchedMeals);
                adapter.notifyDataSetChanged();
            });
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing meals JSON", e);
        }
    }

    private void addMealToUserPlan(Meal meal, String day, String mealType) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            LoginController appController = (LoginController)getApplicationContext();
            String userId = appController.getUserId();

            try {
                URL url = new URL("https://lamp.ms.wits.ac.za/home/s2670867/add_meal.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                String postData = "user_id=" + userId +
                        "&recipe_id=" + meal.getId() +
                        "&day=" + day +
                        "&meal_type=" + mealType;

                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = postData.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = urlConnection.getResponseCode();
                Log.d(TAG, "POST Response Code :: " + responseCode);

                urlConnection.disconnect();

                // Set result and finish the activity
                Intent resultIntent = new Intent();
                setResult(RESULT_MEAL_ADDED, resultIntent);
                finish();
            } catch (Exception e) {
                Log.e(TAG, "Error adding meal to user plan", e);
            }
        });
    }
}
