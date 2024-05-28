package com.example.delice.ui.recipeBook;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.delice.databinding.FragmentRecipebookBinding;
import com.example.delice.ui.recipe.Recipe;
import com.example.delice.ui.recipe.RecipeCardAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class RecipeBookFragment extends Fragment {

    private FragmentRecipebookBinding binding;
    private boolean viewingOwnCookbook = true;

    // Use a static method to create the fragment with arguments
    public static RecipeBookFragment newInstance(boolean isUserCookbook) {
        RecipeBookFragment fragment = new RecipeBookFragment();
        Bundle args = new Bundle();
        args.putBoolean("viewingOwnCookbook", isUserCookbook);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            viewingOwnCookbook = getArguments().getBoolean("viewingOwnCookbook", true);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecipebookBinding.inflate(inflater, container, false);
        setupRecyclerView();
        setupAddRecipeButton();
        return binding.getRoot();
    }

    private void setupAddRecipeButton() {
        binding.buttonAddRecipe.setVisibility(viewingOwnCookbook ? View.VISIBLE : View.GONE);
        if (viewingOwnCookbook) {
            binding.buttonAddRecipe.setOnClickListener(v -> {
                if (viewingOwnCookbook) {
                    Intent intent = new Intent(getActivity(), AddRecipeActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void setupRecyclerView() {
        ArrayList<Recipe> recipes = new ArrayList<>(); // This should be replaced with actual data retrieval logic
        recipes.add(new Recipe(
                "Breakfast: Oatmeal",
                "Oatmeal with honey, fruits and nuts",
                true,  // Favourite
                Arrays.asList("1 cup oatmeal", "1 tbsp honey", "1/2 cup mixed fruits", "1/4 cup nuts"),
                Arrays.asList("Boil water or milk", "Add oatmeal and cook for 5 minutes", "Mix in honey, fruits, and nuts before serving"),
                "Joash Paul",
                "https://images.pexels.com/photos/90894/pexels-photo-90894.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"
        ));

        recipes.add(new Recipe(
                "Lunch: Chicken Salad",
                "Grilled chicken salad with vinaigrette",
                true,  // Not favourite
                Arrays.asList("200g chicken breast", "Mixed greens", "2 tbsp olive oil", "1 tbsp vinegar"),
                Arrays.asList("Grill the chicken until cooked", "Toss the greens in olive oil and vinegar", "Top with sliced chicken"),
                "Camilla Driks",
                "https://images.pexels.com/photos/764925/pexels-photo-764925.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"
        ));

        recipes.add(new Recipe(
                "Dinner: Pasta",
                "Pasta with marinara sauce and basil",
                true,  // Favourite
                Arrays.asList("200g pasta", "100g marinara sauce", "Fresh basil", "Parmesan cheese"),
                Arrays.asList("Cook pasta according to package instructions", "Heat sauce over medium flame", "Combine pasta and sauce", "Garnish with basil and parmesan"),
                "Aimee Harding",
                "https://images.pexels.com/photos/1487511/pexels-photo-1487511.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"
        ));

        RecipeCardAdapter adapter = new RecipeCardAdapter(recipes);
        binding.recipesRecyclerView.setAdapter(adapter);
        binding.recipesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Clean up references to avoid memory leaks
    }
}
