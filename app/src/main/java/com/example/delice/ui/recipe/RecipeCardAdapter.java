package com.example.delice.ui.recipe;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.delice.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeCardAdapter extends RecyclerView.Adapter<RecipeCardAdapter.RecipeViewHolder> {
    private List<Recipe> recipes;

    public RecipeCardAdapter(List<Recipe> recipes) {
        this.recipes = recipes;
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
}
