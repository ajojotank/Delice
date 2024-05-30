package com.example.delice.ui.recipe;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.delice.R;
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
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.List;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecipeDetailActivity extends AppCompatActivity {
    private List<Comment> comments;
    private CommentAdapter commentAdapter;
    private RatingBar ratingBar;
    private Recipe recipe; // Assuming Recipe is a class with a boolean 'favourite' field
    private static final String TAG = "RecipeDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        // Setup RecyclerView for comments
        RecyclerView commentsRecyclerView = findViewById(R.id.recyclerViewUserComments);
        comments = new ArrayList<>(); // This would be ideally fetched from a database or similar
        commentAdapter = new CommentAdapter(comments);
        commentsRecyclerView.setAdapter(commentAdapter);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //fetch comments on recipe
        fetchComments();

        // Handle the comment posting
        EditText commentInput = findViewById(R.id.editTextComment);
        Button submitCommentButton = findViewById(R.id.buttonSubmitComment);
        ratingBar = findViewById(R.id.ratingBar);
        submitCommentButton.setOnClickListener(v -> {
            String commentText = commentInput.getText().toString().trim();
            float rating = ratingBar.getRating();
            if (!commentText.isEmpty()) {
                comments.add(new Comment("Current User", commentText, rating)); // Adding a new comment
                commentAdapter.notifyDataSetChanged();
                commentInput.setText(""); // Clear the input field
                ratingBar.setRating(0); // Reset the rating bar
            }
        });

        // Get recipe from intent
        recipe = (Recipe) getIntent().getSerializableExtra("RECIPE_DATA");

        // Find views and set the recipe data
        TextView title = findViewById(R.id.textRecipeTitle);
        TextView ingredients = findViewById(R.id.textIngredientsList);
        TextView instructions = findViewById(R.id.textInstructions);
        TextView author = findViewById(R.id.textRecipeAuthor);
        ImageView recipeImage = findViewById(R.id.imageRecipeMain);
        ImageView favoriteIcon = findViewById(R.id.imageFavorite);

        // Set text data
        title.setText(recipe.getTitle());
        author.setText("Recipe by: " + recipe.getAuthor());
        ingredients.setText(formatList(recipe.getIngredients()));
        instructions.setText(formatList(recipe.getInstructions()));

        // Set image using Picasso
        Picasso.get().load(recipe.getImageURL()).into(recipeImage);

        // Toggle favorite status when favorite icon is clicked
        updateFavoriteIcon(favoriteIcon, recipe.isFavourite());
        favoriteIcon.setOnClickListener(v -> {
            recipe.toggleFavourite(); // Toggle the favorite status
            updateFavoriteIcon(favoriteIcon, recipe.isFavourite());
        });
    }

    private void updateFavoriteIcon(ImageView icon, boolean isFavourite) {
        if (isFavourite) {
            icon.setImageResource(R.drawable.favourite); // Use filled heart icon
        } else {
            icon.setImageResource(R.drawable.favourite_outline); // Use outline heart icon
        }
    }

    // Helper method to format lists of ingredients or instructions into a single string
    private String formatList(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String item : list) {
            sb.append("• ").append(item).append("\n");
        }
        return sb.toString().trim(); // Removes the last newline character
    }

    private void fetchComments() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            int recipeId = getRecipeID();

            try {
                URL url = new URL("https://lamp.ms.wits.ac.za/home/s2670867/get_comments.php?recipe_id=" + recipeId);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in);
                Log.d(TAG, "Response: " + response);

                parseComments(response);

                urlConnection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error fetching comments", e);
            }
        });
    }

    private void parseComments(String response) {
        try {
            JSONArray commentsArray = new JSONArray(response);
            List<Comment> fetchedComments = new ArrayList<>();
            for (int i = 0; i < commentsArray.length(); i++) {
                JSONObject commentObject = commentsArray.getJSONObject(i);
                String username = commentObject.getString("commenter_username");
                String comment = commentObject.getString("comment");
                float rating = (float) commentObject.getDouble("rating");

                // Handle the comment data as needed, e.g., update UI or store in a list
                fetchedComments.add(new Comment(username, comment, rating));
            }

            //update adapter with the fetched meals
            runOnUiThread(() -> {
                comments.clear();
                comments.addAll(fetchedComments);
                commentAdapter.notifyDataSetChanged();
            });
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing comments", e);
        }
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

    private int getRecipeID() {
        // Get recipe ID
        recipe = (Recipe) getIntent().getSerializableExtra("RECIPE_DATA");
        String recipeName = recipe.getTitle();
        int recipeId = 0;
        try {
            URL url = new URL("https://lamp.ms.wits.ac.za/home/s2670867/get_recipeID.php?name=" + recipeName);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String response = convertStreamToString(in);
            Log.d(TAG, "Response: " + response);

            JSONArray jsonArray = new JSONArray(response);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            recipeId = jsonObject.getInt("recipe_id");
        } catch (Exception e) {
            Log.e(TAG, "Error getting recipesID", e);
        }
        return recipeId;
    }
}
