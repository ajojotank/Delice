package com.example.delice.ui.friends;

import android.os.Bundle;
import android.widget.TextView;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import com.example.delice.R;
import com.example.delice.ui.recipe.Recipe;
import com.example.delice.ui.recipe.RecipeCardAdapter;
import com.example.delice.utilities.IsFavoriteRecipe;
import com.example.delice.utilities.LoginController;
import com.google.android.material.snackbar.Snackbar;

import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONObject;
import androidx.recyclerview.widget.LinearLayoutManager;


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

public class FriendRecipeBookActivity extends AppCompatActivity {

    private RecyclerView recipesRecyclerView;
    private RecipeCardAdapter adapter;

    private LoginController loginController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendrecipebook);

        String friendUsername = getIntent().getStringExtra("friend_username");
        TextView title = findViewById(R.id.textCookbookTitle);
        title.setText(friendUsername + "'s Cookbook");

        recipesRecyclerView = findViewById(R.id.recipesRecyclerView);
        recipesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loginController = (LoginController)getApplicationContext();

        fetchAndDisplayFavorites(friendUsername);
    }

    private void fetchAndDisplayFavorites(String friendUsername) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            String friendId="";

            try {
                URL url = new URL("https://lamp.ms.wits.ac.za/home/s2670867/get_user.php?username=" + friendUsername);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in);
                Log.d("searchUser", "Response: " + response);

                JSONArray jsonArray = new JSONArray(response);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                if (jsonObject.has("user_id")) {
                    friendId = jsonObject.getString("user_id");
                }

                urlConnection.disconnect();
            } catch (Exception e) {
                Log.e("searchUser", "Error searching user", e);
            }

            try {
                URL url = new URL("https://lamp.ms.wits.ac.za/home/s2670867/get_user_favorites.php?user_id=" + friendId);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in);
                Log.d("fetchAndDisplayFavorites", "Response: " + response);

                JSONArray jsonArray = new JSONArray(response);
                ArrayList<Recipe> recipes = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject recipeJson = jsonArray.getJSONObject(i);
                    String title = recipeJson.getString("title");
                    String description = recipeJson.getString("description");
                    String author = recipeJson.getString("author_name"); // Assuming author_id is sufficient for now
                    String imageUrl = recipeJson.getString("image_path");
                    String recipeId = recipeJson.getString("recipe_id");

                    boolean isFavorite = IsFavoriteRecipe.getIsFavorite(loginController.getUserId(), recipeId);

                    List<String> ingredients = new ArrayList<>();
                    if (recipeJson.has("ingredients")) {
                        JSONArray ingredientsArray = recipeJson.getJSONArray("ingredients");
                        for (int j = 0; j < ingredientsArray.length(); j++) {
                            ingredients.add(ingredientsArray.getString(j));
                        }
                    }

                    List<String> instructions = new ArrayList<>();
                    if (recipeJson.has("instructions")) {
                        JSONArray instructionsArray = recipeJson.getJSONArray("instructions");
                        for (int j = 0; j < instructionsArray.length(); j++) {
                            instructions.add(instructionsArray.getString(j));
                        }
                    }

                    recipes.add(new Recipe(title, description, isFavorite, ingredients, instructions, author, imageUrl));
                }

                handler.post(() -> {
                    adapter = new RecipeCardAdapter(recipes, loginController);
                    recipesRecyclerView.setAdapter(adapter);
                });

                urlConnection.disconnect();
            } catch (Exception e) {
                Log.e("fetchAndDisplayFavorites", "Error fetching recipes", e);
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
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
