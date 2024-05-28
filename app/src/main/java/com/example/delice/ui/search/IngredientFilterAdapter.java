package com.example.delice.ui.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.delice.R;

import java.util.List;

public class IngredientFilterAdapter extends RecyclerView.Adapter<IngredientFilterAdapter.ViewHolder> {
    private final List<String> ingredients;
    private final LayoutInflater inflater;

    public IngredientFilterAdapter(List<String> ingredients, LayoutInflater inflater) {
        this.ingredients = ingredients;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.component_groceryitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String ingredient = ingredients.get(position);
        holder.ingredientName.setText(ingredient);
        holder.ingredientCheckbox.setChecked(false);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ingredientName;
        CheckBox ingredientCheckbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientName = itemView.findViewById(R.id.textViewItemName);
            ingredientCheckbox = itemView.findViewById(R.id.checkboxItem);
        }
    }
}
