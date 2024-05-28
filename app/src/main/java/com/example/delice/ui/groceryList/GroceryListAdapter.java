package com.example.delice.ui.groceryList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.delice.R;

import java.util.List;

public class GroceryListAdapter extends RecyclerView.Adapter<GroceryListAdapter.GroceryViewHolder> {

    private final List<String> groceryItems;

    public GroceryListAdapter(List<String> groceryItems) {
        this.groceryItems = groceryItems;
    }

    @NonNull
    @Override
    public GroceryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_groceryitem, parent, false);
        return new GroceryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroceryViewHolder holder, int position) {
        String item = groceryItems.get(position);
        holder.itemName.setText(item);
    }

    @Override
    public int getItemCount() {
        return groceryItems.size();
    }

    static class GroceryViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        CheckBox itemCheckbox;

        GroceryViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.textViewItemName);
            itemCheckbox = itemView.findViewById(R.id.checkboxItem);
        }
    }
}
