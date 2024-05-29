package com.example.delice.ui.recipeBook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.delice.R;
import com.example.delice.databinding.FragmentRecipebookBinding;
import com.example.delice.databinding.FragmentSearchBinding;
import com.example.delice.ui.recipe.Recipe;
import com.example.delice.ui.recipe.RecipeCardAdapter;
import com.example.delice.ui.search.IngredientFilterAdapter;
import com.example.delice.utilities.LoginController;

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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class RecipeBookFragment extends Fragment {

    private FragmentRecipebookBinding binding;
    private boolean viewingOwnCookbook = true;
    private RecipeCardAdapter recipeCardAdapter;

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
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        LoginController appController = (LoginController) getActivity().getApplicationContext();
        String userId = appController.getUserId();

        executorService.execute(() -> {
            try {
                URL url = new URL("https://lamp.ms.wits.ac.za/home/s2670867/get_user_favorites.php?user_id="+userId);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in);
                Log.d("setupRecyclerView", "Response: " + response);

                JSONArray jsonArray = new JSONArray(response);
                ArrayList<Recipe> recipes = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject recipeJson = jsonArray.getJSONObject(i);
                    String title = recipeJson.getString("title");
                    String description = recipeJson.getString("description");
                    String author = recipeJson.getString("author"); // Assuming author_id is sufficient for now
                    String imageUrl = recipeJson.getString("image_url");

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

                handler.post(() -> {
                    recipeCardAdapter = new RecipeCardAdapter(recipes, appController);
                    binding.recipesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.recipesRecyclerView.setAdapter(recipeCardAdapter);
                });

                urlConnection.disconnect();
            } catch (Exception e) {
                Log.e("setupRecyclerView", "Error fetching recipes", e);
            }
        });
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Clean up references to avoid memory leaks
    }
}
