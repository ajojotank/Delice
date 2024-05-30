package com.example.delice.ui.recipe;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.delice.R;
import com.example.delice.utilities.IsFavoriteRecipe;
import com.example.delice.utilities.LoginController;
import com.squareup.picasso.Picasso;

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

public class RecipeCardAdapter extends RecyclerView.Adapter<RecipeCardAdapter.RecipeViewHolder> {
    private List<Recipe> recipes;
    private static final String TAG = "RecipeCardAdapter";

    private final LoginController loginController;

    public RecipeCardAdapter(List<Recipe> recipes, LoginController loginController) {
        this.recipes = recipes;
        this.loginController = loginController;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_recipecard, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.title.setText(recipe.getTitle());
        holder.description.setText(recipe.getDescription());
        holder.author.setText("Recipe by: " + recipe.getAuthor());


        Log.e("making recipe card","making recipe card for: "+recipe.getTitle());
        Log.e("checking is fav","This recipe is a fav: "+recipe.isFavourite());
        if (recipe.isFavourite()){
            holder.favorite.setImageResource(R.drawable.favourite);
        }

        // Using Picasso to load the image from URL
        Picasso.get().load(recipe.getImageURL()).into(holder.image);

        updateFavoriteIcon(holder.favorite, recipe.isFavourite());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RecipeDetailActivity.class);
            intent.putExtra("RECIPE_DATA", recipe);
            v.getContext().startActivity(intent);
        });

        holder.favorite.setOnClickListener(v -> {
            boolean isNowFavorite = !recipe.isFavourite();
            recipe.toggleFavourite();
            updateFavoriteDataBind(isNowFavorite,recipe.getTitle());
            updateFavoriteIcon(holder.favorite, isNowFavorite);
        });
    }

    private void updateFavoriteIcon(ImageView imageView, boolean isFavourite) {
        if (isFavourite) {
            imageView.setImageResource(R.drawable.favourite);
        } else {
            imageView.setImageResource(R.drawable.favourite_outline);
        }
    }


    private void updateFavoriteDataBind(boolean isFavorite, String recipeName) {
        String userId = loginController.getUserId();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {

            String recipeId = "";
            try {
                URL url = new URL("https://lamp.ms.wits.ac.za/home/s2670867/get_recipeID.php?name="+ recipeName);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in);
                Log.d("setupRecyclerView", "Response: " + response);

                JSONArray jsonArray = new JSONArray(response);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                recipeId = jsonObject.getString("recipe_id");

            } catch (Exception e) {
                Log.e("setupRecyclerView", "Error getting recipesID", e);
            }

            try {
                if (!isFavorite) {
                    // Removing from favorites
                    URL url = new URL("https://lamp.ms.wits.ac.za/home/s2670867/remove_favorite_recipe.php?user_id=" + userId + "&recipe_id=" + recipeId);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    String response = convertStreamToString(in);
                    Log.d(TAG, "Response: " + response);

                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    //Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();

                    urlConnection.disconnect();
                } else {
                    // Adding to favorites
                    URL url = new URL("https://lamp.ms.wits.ac.za/home/s2670867/add_favorite_recipe.php?user_id=" + userId + "&recipe_id=" + recipeId);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    String response = convertStreamToString(in);
                    Log.d(TAG, "Response: " + response);

                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

//                    JSONArray jsonArray = new JSONArray(response);
//                    ArrayList<Recipe> recipes = new ArrayList<>();

                    urlConnection.disconnect();
                }

            } catch (Exception e) {
                Log.e(TAG, "Error updating favorite status", e);
            }
        });

    }

    @Override
    public int getItemCount() {
        return recipes != null ? recipes.size() : 0;
    }

    public void updateData(List<Recipe> newRecipes) {
        this.recipes = newRecipes;
        notifyDataSetChanged();
    }
    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, author;

        ImageView image, favorite;
        RecipeViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textRecipeTitle);
            description = itemView.findViewById(R.id.textRecipeDescription);
            author = itemView.findViewById(R.id.textRecipeAuthor);
            image = itemView.findViewById(R.id.imageRecipe);
            favorite = itemView.findViewById(R.id.imageFavorite);
        }

    }
    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            Log.e("convertStreamToString", "Error reading stream", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e("convertStreamToString", "Error closing stream", e);
            }
        }
        return sb.toString();
    }
}
