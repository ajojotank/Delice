package com.example.delice.ui.friends;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.delice.R;
import com.example.delice.ui.recipe.Recipe;
import com.example.delice.ui.recipe.RecipeCardAdapter;

import org.json.JSONArray;
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

public class FriendRecipeBookActivity extends AppCompatActivity {

    private RecyclerView recipesRecyclerView;
    private RecipeCardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendrecipebook);

        String friendUsername = getIntent().getStringExtra("friend_username");
        TextView title = findViewById(R.id.textCookbookTitle);
        title.setText(friendUsername + "'s Cookbook");

        recipesRecyclerView = findViewById(R.id.recipesRecyclerView);
        recipesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        int friendId = getIntent().getIntExtra("friend_id", -1);
        if (friendId != -1) {
            fetchFriendFavorites(friendId);
        }
    }

    private void fetchFriendFavorites(int friendId) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                URL url = new URL("https://lamp.ms.wits.ac.za/home/s2670867/get_user_favorites.php?user_id=" + friendId);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in);
                urlConnection.disconnect();

                List<Recipe> recipes = parseRecipes(response);
                runOnUiThread(() -> displayRecipes(recipes));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private List<Recipe> parseRecipes(String response) {
        List<Recipe> recipes = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject recipeJson = jsonArray.getJSONObject(i);
                String title = recipeJson.getString("title");
                String description = recipeJson.getString("description");
                String author = recipeJson.getString("author_name"); // Assuming author_id is sufficient for now
                String imageUrl = recipeJson.getString("image_path");

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

                recipes.add(new Recipe(title, description, false, ingredients, instructions, author, imageUrl));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recipes;
    }

    private void displayRecipes(List<Recipe> recipes) {
        adapter = new RecipeCardAdapter(recipes);
        recipesRecyclerView.setAdapter(adapter);
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
