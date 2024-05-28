package com.example.delice.ui.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.delice.R;
import com.example.delice.ui.recipe.Recipe;
import com.example.delice.ui.recipe.RecipeDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UnifiedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_RECIPE = 0;
    private static final int VIEW_TYPE_MEAL = 1;

    private List<Object> items;
    private Context context;

    public UnifiedAdapter(Context context, List<Object> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Recipe) {
            return VIEW_TYPE_RECIPE;
        } else {
            return VIEW_TYPE_MEAL;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_RECIPE) {
            View view = inflater.inflate(R.layout.component_recipecard, parent, false);
            return new RecipeViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.component_addmealcard, parent, false);
            return new MealViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_RECIPE) {
            Recipe recipe = (Recipe) items.get(position);
            RecipeViewHolder recipeHolder = (RecipeViewHolder) holder;
            recipeHolder.title.setText(recipe.getTitle());
            recipeHolder.description.setText(recipe.getDescription());
            recipeHolder.author.setText("Recipe by: " + recipe.getAuthor());

            recipeHolder.favourite.setImageResource(R.drawable.remove);
            recipeHolder.favourite.setOnClickListener(v -> {
                notifyItemRemoved(position);
                notifyDataSetChanged();
            });



            Picasso.get().load(recipe.getImageURL()).into(recipeHolder.image);

            recipeHolder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, RecipeDetailActivity.class);
                intent.putExtra("RECIPE_DATA", recipe);
                context.startActivity(intent);
            });
        } else {
            Meal meal = (Meal) items.get(position);
            MealViewHolder mealHolder = (MealViewHolder) holder;
            if (meal != null) {
                mealHolder.title.setText(meal.getTitle());
                mealHolder.description.setText(meal.getDescription());
                mealHolder.author.setText("Recipe b: " + meal.getAuthor());

                // Using Picasso to load the image from URL
                Picasso.get().load(meal.getImageUrl()).into(mealHolder.image);

                mealHolder.imageAdd.setOnClickListener(v -> {
                    // Handle add meal logic here
                });
            } else {
                mealHolder.itemView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, author;
        ImageView image, favourite;

        RecipeViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textRecipeTitle);
            description = itemView.findViewById(R.id.textRecipeDescription);
            author = itemView.findViewById(R.id.textRecipeAuthor);
            image = itemView.findViewById(R.id.imageRecipe);
            favourite = itemView.findViewById(R.id.imageFavorite);
        }
    }

    static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, author;
        ImageView image, imageAdd;

        MealViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textRecipeTitle);
            description = itemView.findViewById(R.id.textRecipeDescription);
            author = itemView.findViewById(R.id.textRecipeAuthor);
            image = itemView.findViewById(R.id.imageRecipe);
            imageAdd = itemView.findViewById(R.id.imageAdd);
        }
    }
}
