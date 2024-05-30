package com.example.delice.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.delice.R;
import com.example.delice.ui.home.Meal;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MealCardAdapter extends RecyclerView.Adapter<MealCardAdapter.MealViewHolder> {

    private List<Meal> meals;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Meal meal);
    }

    public MealCardAdapter(List<Meal> meals, Context context, OnItemClickListener onItemClickListener) {
        this.meals = meals;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.component_recipecard, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = meals.get(position);
        holder.bind(meal, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;
        TextView author;
        ImageView image;
        ImageView favorite;


        MealViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textRecipeTitle);
            description = itemView.findViewById(R.id.textRecipeDescription);
            author = itemView.findViewById(R.id.textRecipeAuthor);
            image = itemView.findViewById(R.id.imageRecipe);
            favorite = itemView.findViewById(R.id.imageFavorite);


        }

        void bind(Meal meal, OnItemClickListener onItemClickListener) {
            title.setText(meal.getTitle());
            description.setText(meal.getDescription());
            author.setText("Recipe by: " + meal.getAuthor());
            Picasso.get().load(meal.getImageUrl()).into(image);
            favorite.setVisibility(View.INVISIBLE);
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(meal));
        }
    }
}
