package com.example.delice.ui.recipe;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.delice.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {
    private List<Comment> comments;
    private CommentAdapter commentAdapter;
    private RatingBar ratingBar;
    private Recipe recipe; // Assuming Recipe is a class with a boolean 'favourite' field

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
}
