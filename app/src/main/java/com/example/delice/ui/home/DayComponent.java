package com.example.delice.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.delice.R;
import com.example.delice.ui.recipe.Recipe;
import com.example.delice.ui.recipe.RecipeDetailActivity;
import com.example.delice.utilities.LoginController;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DayComponent extends Fragment {
    private int dayIndex;
    private static final int ADD_MEAL_REQUEST_CODE = 1001;
    private static final String TAG = "HomeFragment";
    private LinearLayout mealLayout;

    public static DayComponent newInstance(int dayIndex) {
        DayComponent fragment = new DayComponent();
        Bundle args = new Bundle();
        args.putInt("dayIndex", dayIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dayIndex = getArguments().getInt("dayIndex");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.component_day, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mealLayout = view.findViewById(R.id.mealLayout);

        // Fetch meals for the day and populate the layout
        getMealsForDay(dayIndex, meals -> {
            // Update UI on the main thread
            new Handler(Looper.getMainLooper()).post(() -> populateMeals(mealLayout, meals));
        });
    }

    private void populateMeals(LinearLayout layout, List<Object> meals) {
        Context context = getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        String[] mealTimes = {"Breakfast", "Lunch", "Dinner"};

        layout.removeAllViews(); // Clear existing views before populating

        for (int i = 0; i < meals.size(); i++) {
            Object item = meals.get(i);
            String mealTime = mealTimes[i];

            View titleView = inflater.inflate(R.layout.meal_title, layout, false);
            TextView mealTitle = titleView.findViewById(R.id.mealTitle);
            View button = titleView.findViewById(R.id.deleteMealButton);

            button.setOnClickListener(v -> {
                deleteMeal(mealTime, () -> {
                    getMealsForDay(dayIndex, updatedMeals -> {
                        new Handler(Looper.getMainLooper()).post(() -> populateMeals(layout, updatedMeals));
                    });
                });
            });

            mealTitle.setText(mealTime);
            layout.addView(titleView);

            if (item != null && item instanceof Recipe) {
                Recipe recipe = (Recipe) item;
                View recipeCard = inflater.inflate(R.layout.component_recipecard, layout, false);

                TextView title = recipeCard.findViewById(R.id.textRecipeTitle);
                TextView description = recipeCard.findViewById(R.id.textRecipeDescription);
                TextView author = recipeCard.findViewById(R.id.textRecipeAuthor);
                ImageView image = recipeCard.findViewById(R.id.imageRecipe);

                title.setText(recipe.getTitle());
                description.setText(recipe.getDescription());
                author.setText("Recipe by: " + recipe.getAuthor());
                Picasso.get().load(recipe.getImageURL()).into(image);

                recipeCard.setOnClickListener(v -> {
                    Intent intent = new Intent(context, RecipeDetailActivity.class);
                    intent.putExtra("RECIPE_DATA", recipe);
                    context.startActivity(intent);
                });

                layout.addView(recipeCard);
            } else {
                View addMealButtonView = inflater.inflate(R.layout.component_add_meal_button, layout, false);
                Button addMealButton = addMealButtonView.findViewById(R.id.add_meal_button);
                addMealButton.setText("Add " + mealTime);
                addMealButton.setOnClickListener(v -> {
                    Intent intent = new Intent(context, AddMealActivity.class);
                    intent.putExtra(AddMealActivity.EXTRA_DAY, getDayName(dayIndex)); // Pass the day
                    intent.putExtra(AddMealActivity.EXTRA_MEAL_TYPE, mealTime); // Pass the meal type
                    startActivityForResult(intent, ADD_MEAL_REQUEST_CODE);
                });

                layout.addView(addMealButtonView);
            }
        }
    }

    private void getMealsForDay(int dayIndex, MealsCallback callback) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            List<Object> items = new ArrayList<>();
            String day = getDayName(dayIndex);
            LoginController appController = (LoginController) getActivity().getApplicationContext();
            String userId = appController.getUserId();

            try {
                URL url = new URL("https://lamp.ms.wits.ac.za/home/s2670867/get_meals_for_user.php?user_id=" + userId + "&day=" + day);
                Log.d(TAG, "URL: " + url.toString()); // Log the URL
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in);
                Log.d(TAG, "Response: " + response); // Log the response

                JSONObject jsonResponse = new JSONObject(response);

                items.add(parseRecipe(jsonResponse.optJSONObject("breakfast")));
                items.add(parseRecipe(jsonResponse.optJSONObject("lunch")));
                items.add(parseRecipe(jsonResponse.optJSONObject("dinner")));

                urlConnection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error fetching meals", e);
            }

            handler.post(() -> callback.onMealsFetched(items));
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

    private Recipe parseRecipe(JSONObject recipeJson) {
        if (recipeJson == null) {
            return null;
        }
        try {
            String title = recipeJson.getString("title");
            String description = recipeJson.getString("description");
            boolean isFavorite = recipeJson.getString("is_favorite").equals("1");
            String author = recipeJson.getString("author");
            String imageUrl = recipeJson.getString("image_url");

            List<String> ingredients = new ArrayList<>();
            JSONArray ingredientsArray = recipeJson.getJSONArray("ingredients");
            for (int i = 0; i < ingredientsArray.length(); i++) {
                ingredients.add(ingredientsArray.getString(i));
            }

            List<String> instructions = new ArrayList<>();
            JSONArray instructionsArray = recipeJson.getJSONArray("instructions");
            for (int i = 0; i < instructionsArray.length(); i++) {
                instructions.add(instructionsArray.getString(i));
            }

            return new Recipe(title, description, isFavorite, ingredients, instructions, author, imageUrl);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing recipe JSON", e);
            return null;
        }
    }

    private String getDayName(int dayIndex) {
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        return days[dayIndex];
    }

    private void deleteMeal(String mealType, Runnable callback) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            LoginController appController = (LoginController) getActivity().getApplicationContext();
            String userId = appController.getUserId();
            String day = getDayName(dayIndex);

            try {
                URL url = new URL("https://lamp.ms.wits.ac.za/home/s2670867/delete_meal.php?user_id=" + userId + "&day=" + day + "&meal_type=" + mealType);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET"); // Assuming the endpoint supports GET for deletion

                int responseCode = urlConnection.getResponseCode();
                Log.d(TAG, "DELETE Response Code :: " + responseCode);

                urlConnection.disconnect();

                if (responseCode == 200) {
                    callback.run();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error deleting meal", e);
            }
        });
    }

    interface MealsCallback {
        void onMealsFetched(List<Object> meals);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_MEAL_REQUEST_CODE && resultCode == AddMealActivity.RESULT_MEAL_ADDED) {
            // Refresh the view
            getMealsForDay(dayIndex, meals -> {
                new Handler(Looper.getMainLooper()).post(() -> {
                    mealLayout.removeAllViews();
                    populateMeals(mealLayout, meals);
                });
            });
        }
    }
}
